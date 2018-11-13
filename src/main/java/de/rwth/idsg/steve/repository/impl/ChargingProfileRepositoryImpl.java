package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import jooq.steve.db.tables.records.ChargingProfileRecord;
import jooq.steve.db.tables.records.ChargingSchedulePeriodRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.Tables.CHARGING_PROFILE;
import static jooq.steve.db.Tables.CHARGING_SCHEDULE_PERIOD;
import static jooq.steve.db.Tables.CONNECTOR;
import static jooq.steve.db.Tables.CONNECTOR_CHARGING_PROFILE;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.11.2018
 */
@Slf4j
@Repository
public class ChargingProfileRepositoryImpl implements ChargingProfileRepository {

    @Autowired private DSLContext ctx;

    // -------------------------------------------------------------------------
    // OCPP operations
    // -------------------------------------------------------------------------

    @Override
    public void setProfile(int chargingProfilePk, String chargeBoxId, int connectorId) {
        SelectConditionStep<Record1<Integer>> connectorPkSelect = ctx.select(CONNECTOR.CONNECTOR_PK)
                                                                     .from(CONNECTOR)
                                                                     .where(CONNECTOR.CHARGE_BOX_ID.eq(chargeBoxId))
                                                                     .and(CONNECTOR.CONNECTOR_ID.eq(connectorId));

        ctx.insertInto(CONNECTOR_CHARGING_PROFILE)
           .set(CONNECTOR_CHARGING_PROFILE.CONNECTOR_PK, connectorPkSelect)
           .set(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK, chargingProfilePk)
           .execute();
    }

    @Override
    public void clearProfile(int chargingProfilePk, String chargeBoxId) {
        SelectConditionStep<Record1<Integer>> connectorPkSelect = ctx.select(CONNECTOR.CONNECTOR_PK)
                                                                     .from(CONNECTOR)
                                                                     .where(CONNECTOR.CHARGE_BOX_ID.eq(chargeBoxId));

        ctx.delete(CONNECTOR_CHARGING_PROFILE)
           .where(CONNECTOR_CHARGING_PROFILE.CONNECTOR_PK.in(connectorPkSelect))
           .and(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(chargingProfilePk))
           .execute();
    }

    @Override
    public void clearProfile(@NotNull String chargeBoxId,
                             @Nullable Integer connectorId,
                             @Nullable ChargingProfilePurposeType purpose,
                             @Nullable Integer stackLevel) {

        // -------------------------------------------------------------------------
        // Connector select
        // -------------------------------------------------------------------------

        Condition connectorIdCondition = (connectorId == null) ? DSL.trueCondition() : CONNECTOR.CONNECTOR_ID.eq(connectorId);

        SelectConditionStep<Record1<Integer>> connectorPkSelect = ctx.select(CONNECTOR.CONNECTOR_PK)
                                                                     .from(CONNECTOR)
                                                                     .where(CONNECTOR.CHARGE_BOX_ID.eq(chargeBoxId))
                                                                     .and(connectorIdCondition);

        // -------------------------------------------------------------------------
        // Profile select
        // -------------------------------------------------------------------------

        Condition profilePkCondition;

        if (purpose == null && stackLevel == null) {
            profilePkCondition = DSL.trueCondition();
        } else {
            Condition purposeCondition = (purpose == null) ?  DSL.trueCondition() : CHARGING_PROFILE.CHARGING_PROFILE_PURPOSE.eq(purpose.value());

            Condition stackLevelCondition = (stackLevel == null) ? DSL.trueCondition() : CHARGING_PROFILE.STACK_LEVEL.eq(stackLevel);

            SelectConditionStep<Record1<Integer>> profilePkSelect = ctx.select(CHARGING_PROFILE.CHARGING_PROFILE_PK)
                                                                       .from(CHARGING_PROFILE)
                                                                       .where(purposeCondition)
                                                                       .and(stackLevelCondition);

            profilePkCondition = CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.in(profilePkSelect);
        }

        // -------------------------------------------------------------------------
        // Delete execution
        // -------------------------------------------------------------------------

        ctx.delete(CONNECTOR_CHARGING_PROFILE)
           .where(CONNECTOR_CHARGING_PROFILE.CONNECTOR_PK.in(connectorPkSelect))
           .and(profilePkCondition)
           .execute();
    }

    // -------------------------------------------------------------------------
    // CRUD stuff
    // -------------------------------------------------------------------------

    @Override
    public List<ChargingProfile.BasicInfo> getBasicInfo() {
        return ctx.select(CHARGING_PROFILE.CHARGING_PROFILE_PK, CHARGING_PROFILE.DESCRIPTION)
                  .from(CHARGING_PROFILE)
                  .fetch()
                  .map(r -> new ChargingProfile.BasicInfo(r.value1(), r.value2()));
    }

    @Override
    public List<ChargingProfile.Overview> getOverview(ChargingProfileQueryForm form) {
        Condition conditions = DSL.trueCondition();

        if (form.getChargingProfilePk() != null) {
            conditions = conditions.and(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(form.getChargingProfilePk()));
        }

        if (form.getStackLevel() != null) {
            conditions = conditions.and(CHARGING_PROFILE.STACK_LEVEL.eq(form.getStackLevel()));
        }

        if (form.getDescription() != null) {
            conditions = conditions.and(includes(CHARGING_PROFILE.DESCRIPTION, form.getDescription()));
        }

        if (form.getProfilePurpose() != null) {
            conditions = conditions.and(CHARGING_PROFILE.CHARGING_PROFILE_PURPOSE.eq(form.getProfilePurpose().value()));
        }

        if (form.getProfileKind() != null) {
            conditions = conditions.and(CHARGING_PROFILE.CHARGING_PROFILE_KIND.eq(form.getProfileKind().value()));
        }

        if (form.getRecurrencyKind() != null) {
            conditions = conditions.and(CHARGING_PROFILE.RECURRENCY_KIND.eq(form.getRecurrencyKind().value()));
        }

        if (form.getValidFrom() != null) {
            conditions = conditions.and(CHARGING_PROFILE.VALID_FROM.greaterOrEqual(form.getValidFrom().toDateTime()));
        }

        if (form.getValidTo() != null) {
            conditions = conditions.and(CHARGING_PROFILE.VALID_TO.lessOrEqual(form.getValidTo().toDateTime()));
        }

        return ctx.selectFrom(CHARGING_PROFILE)
                  .where(conditions)
                  .fetch()
                  .map(r -> ChargingProfile.Overview.builder()
                                                    .chargingProfilePk(r.getChargingProfilePk())
                                                    .stackLevel(r.getStackLevel())
                                                    .description(r.getDescription())
                                                    .profilePurpose(r.getChargingProfilePurpose())
                                                    .profileKind(r.getChargingProfileKind())
                                                    .recurrencyKind(r.getRecurrencyKind())
                                                    .validFrom(r.getValidFrom())
                                                    .validTo(r.getValidTo())
                                                    .build()
                  );
    }

    @Override
    public ChargingProfile.Details getDetails(int chargingProfilePk) {
        ChargingProfileRecord profile =
                ctx.selectFrom(CHARGING_PROFILE)
                   .where(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(chargingProfilePk))
                   .fetchOne();

        List<ChargingSchedulePeriodRecord> periods =
                ctx.selectFrom(CHARGING_SCHEDULE_PERIOD)
                   .where(CHARGING_SCHEDULE_PERIOD.CHARGING_PROFILE_PK.eq(chargingProfilePk))
                   .fetch();

        return new ChargingProfile.Details(profile, periods);
    }

    @Override
    public int add(ChargingProfileForm form) {
        return ctx.transactionResult(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                int profilePk = ctx.insertInto(CHARGING_PROFILE)
                                   .set(CHARGING_PROFILE.DESCRIPTION, form.getDescription())
                                   .set(CHARGING_PROFILE.NOTE, form.getNote())
                                   .set(CHARGING_PROFILE.STACK_LEVEL, form.getStackLevel())
                                   .set(CHARGING_PROFILE.CHARGING_PROFILE_PURPOSE, form.getChargingProfilePurpose().value())
                                   .set(CHARGING_PROFILE.CHARGING_PROFILE_KIND, form.getChargingProfileKind().value())
                                   .set(CHARGING_PROFILE.RECURRENCY_KIND, form.getRecurrencyKind() == null ? null : form.getRecurrencyKind().value())
                                   .set(CHARGING_PROFILE.VALID_FROM, DateTimeUtils.toDateTime(form.getValidFrom()))
                                   .set(CHARGING_PROFILE.VALID_TO, DateTimeUtils.toDateTime(form.getValidTo()))
                                   .set(CHARGING_PROFILE.DURATION_IN_SECONDS, form.getDurationInSeconds())
                                   .set(CHARGING_PROFILE.START_SCHEDULE, DateTimeUtils.toDateTime(form.getStartSchedule()))
                                   .set(CHARGING_PROFILE.CHARGING_RATE_UNIT, form.getChargingRateUnit().value())
                                   .set(CHARGING_PROFILE.MIN_CHARGING_RATE, form.getMinChargingRate())
                                   .returning(CHARGING_SCHEDULE_PERIOD.CHARGING_PROFILE_PK)
                                   .fetchOne()
                                   .getChargingProfilePk();

                form.setChargingProfilePk(profilePk);
                insertPeriods(ctx, form);
                return profilePk;

            } catch (DataAccessException e) {
                throw new SteveException("Failed to add the charging profile", e);
            }
        });
    }

    @Override
    public void update(ChargingProfileForm form) {
        checkProfileUsage(form.getChargingProfilePk());

        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                ctx.update(CHARGING_PROFILE)
                   .set(CHARGING_PROFILE.DESCRIPTION, form.getDescription())
                   .set(CHARGING_PROFILE.NOTE, form.getNote())
                   .set(CHARGING_PROFILE.STACK_LEVEL, form.getStackLevel())
                   .set(CHARGING_PROFILE.CHARGING_PROFILE_PURPOSE, form.getChargingProfilePurpose().value())
                   .set(CHARGING_PROFILE.CHARGING_PROFILE_KIND, form.getChargingProfileKind().value())
                   .set(CHARGING_PROFILE.RECURRENCY_KIND, form.getRecurrencyKind() == null ? null : form.getRecurrencyKind().value())
                   .set(CHARGING_PROFILE.VALID_FROM, DateTimeUtils.toDateTime(form.getValidFrom()))
                   .set(CHARGING_PROFILE.VALID_TO, DateTimeUtils.toDateTime(form.getValidTo()))
                   .set(CHARGING_PROFILE.DURATION_IN_SECONDS, form.getDurationInSeconds())
                   .set(CHARGING_PROFILE.START_SCHEDULE, DateTimeUtils.toDateTime(form.getStartSchedule()))
                   .set(CHARGING_PROFILE.CHARGING_RATE_UNIT, form.getChargingRateUnit().value())
                   .set(CHARGING_PROFILE.MIN_CHARGING_RATE, form.getMinChargingRate())
                   .where(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(form.getChargingProfilePk()))
                   .execute();

                // -------------------------------------------------------------------------
                // the form contains all period information for this schedule. instead of
                // computing a delta about what to insert/update, we can simply delete everything
                // for this profile and re-insert.
                // -------------------------------------------------------------------------

                ctx.delete(CHARGING_SCHEDULE_PERIOD)
                   .where(CHARGING_SCHEDULE_PERIOD.CHARGING_PROFILE_PK.eq(form.getChargingProfilePk()))
                   .execute();

                insertPeriods(ctx, form);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to update the charging profile with id '%s'",
                        form.getChargingProfilePk(), e);
            }
        });
    }

    @Override
    public void delete(int chargingProfilePk) {
        checkProfileUsage(chargingProfilePk);

        ctx.delete(CHARGING_PROFILE)
           .where(CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(chargingProfilePk))
           .execute();
    }

    private void checkProfileUsage(int chargingProfilePk) {
        List<Record1<String>> r = ctx.select(CONNECTOR.CHARGE_BOX_ID)
                                     .from(CONNECTOR_CHARGING_PROFILE)
                                     .join(CONNECTOR)
                                     .on(CONNECTOR.CONNECTOR_PK.eq(CONNECTOR_CHARGING_PROFILE.CONNECTOR_PK))
                                     .where(CONNECTOR_CHARGING_PROFILE.CHARGING_PROFILE_PK.eq(chargingProfilePk))
                                     .fetch();
        if (!r.isEmpty()) {
            throw new SteveException("Cannot modify this charging profile, since the following stations are still using it: %s", r);
        }
    }

    private static void insertPeriods(DSLContext ctx, ChargingProfileForm form) {
        if (CollectionUtils.isEmpty(form.getSchedulePeriodMap())) {
            return;
        }

        List<ChargingSchedulePeriodRecord> r = form.getSchedulePeriodMap()
                                                   .values()
                                                   .stream()
                                                   .map(k -> ctx.newRecord(CHARGING_SCHEDULE_PERIOD)
                                                                .setChargingProfilePk(form.getChargingProfilePk())
                                                                .setStartPeriodInSeconds(k.getStartPeriodInSeconds())
                                                                .setPowerLimitInAmperes(k.getPowerLimitInAmperes())
                                                                .setNumberPhases(k.getNumberPhases()))
                                                   .collect(Collectors.toList());

        ctx.batchInsert(r).execute();
    }
}
