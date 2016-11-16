package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.AddressRepository;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectQuery;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;
import static jooq.steve.db.tables.User.USER;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired private DSLContext ctx;
    @Autowired private AddressRepository addressRepository;

    @Override
    public List<User.Overview> getOverview(UserQueryForm form) {
        return getOverviewInternal(form)
                .map(r -> User.Overview.builder()
                                       .userPk(r.value1())
                                       .ocppTagPk(r.value2())
                                       .ocppIdTag(r.value3())
                                       .name(r.value4() + " " + r.value5())
                                       .phone(r.value6())
                                       .email(r.value7())
                                       .build()
                );
    }

    @Override
    public User.Details getDetails(int userPk) {

        // -------------------------------------------------------------------------
        // 1. user table
        // -------------------------------------------------------------------------

        UserRecord ur = ctx.selectFrom(USER)
                           .where(USER.USER_PK.equal(userPk))
                           .fetchOne();

        if (ur == null) {
            throw new SteveException("There is no user with id '%s'", userPk);
        }

        // -------------------------------------------------------------------------
        // 2. address table
        // -------------------------------------------------------------------------

        AddressRecord ar = addressRepository.get(ctx, ur.getAddressPk());

        // -------------------------------------------------------------------------
        // 3. ocpp_tag table
        // -------------------------------------------------------------------------

        String ocppIdTag = null;
        if (ur.getOcppTagPk() != null) {
            Record1<String> record = ctx.select(OCPP_TAG.ID_TAG)
                                        .from(OCPP_TAG)
                                        .where(OCPP_TAG.OCPP_TAG_PK.eq(ur.getOcppTagPk()))
                                        .fetchOne();

            if (record != null) {
                ocppIdTag = record.value1();
            }
        }

        return User.Details.builder()
                           .userRecord(ur)
                           .address(ar)
                           .ocppIdTag(Optional.ofNullable(ocppIdTag))
                           .build();
    }

    @Override
    public void add(UserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                addInternal(ctx, form, addressId);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to add the user", e);
            }
        });
    }

    @Override
    public void update(UserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                Integer addressId = addressRepository.updateOrInsert(ctx, form.getAddress());
                updateInternal(ctx, form, addressId);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to update the user", e);
            }
        });
    }

    @Override
    public void delete(int userPk) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                addressRepository.delete(ctx, selectAddressId(userPk));
                deleteInternal(ctx, userPk);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to delete the user", e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Result<Record7<Integer, Integer, String, String, String, String, String>> getOverviewInternal(UserQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(USER);
        selectQuery.addJoin(OCPP_TAG, JoinType.LEFT_OUTER_JOIN, USER.OCPP_TAG_PK.eq(OCPP_TAG.OCPP_TAG_PK));
        selectQuery.addSelect(
                USER.USER_PK,
                USER.OCPP_TAG_PK,
                OCPP_TAG.ID_TAG,
                USER.FIRST_NAME,
                USER.LAST_NAME,
                USER.PHONE,
                USER.E_MAIL
        );

        if (form.isSetUserPk()) {
            selectQuery.addConditions(USER.USER_PK.eq(form.getUserPk()));
        }

        if (form.isSetOcppIdTag()) {
            selectQuery.addConditions(includes(OCPP_TAG.ID_TAG, form.getOcppIdTag()));
        }

        if (form.isSetEmail()) {
            selectQuery.addConditions(includes(USER.E_MAIL, form.getEmail()));
        }

        if (form.isSetName()) {

            // Concatenate the two columns and search within the resulting representation
            // for flexibility, since the user can search by first or last name, or both.
            Field<String> joinedField = DSL.concat(USER.FIRST_NAME, USER.LAST_NAME);

            // Find a matching sequence anywhere within the concatenated representation
            selectQuery.addConditions(includes(joinedField, form.getName()));
        }

        return selectQuery.fetch();
    }

    private SelectConditionStep<Record1<Integer>> selectAddressId(int userPk) {
        return ctx.select(USER.ADDRESS_PK)
                  .from(USER)
                  .where(USER.USER_PK.eq(userPk));
    }

    private SelectConditionStep<Record1<Integer>> selectOcppTagPk(String ocppIdTag) {
        return ctx.select(OCPP_TAG.OCPP_TAG_PK)
                  .from(OCPP_TAG)
                  .where(OCPP_TAG.ID_TAG.eq(ocppIdTag));
    }

    private void addInternal(DSLContext ctx, UserForm form, Integer addressPk) {
        int count = ctx.insertInto(USER)
                       .set(USER.FIRST_NAME, form.getFirstName())
                       .set(USER.LAST_NAME, form.getLastName())
                       .set(USER.BIRTH_DAY, form.getBirthDay())
                       .set(USER.SEX, form.getSex().getDatabaseValue())
                       .set(USER.PHONE, form.getPhone())
                       .set(USER.E_MAIL, form.getEMail())
                       .set(USER.NOTE, form.getNote())
                       .set(USER.ADDRESS_PK, addressPk)
                       .set(USER.OCPP_TAG_PK, selectOcppTagPk(form.getOcppIdTag()))
                       .execute();

        if (count != 1) {
            throw new SteveException("Failed to insert the user");
        }
    }

    private void updateInternal(DSLContext ctx, UserForm form, Integer addressPk) {
        ctx.update(USER)
           .set(USER.FIRST_NAME, form.getFirstName())
           .set(USER.LAST_NAME, form.getLastName())
           .set(USER.BIRTH_DAY, form.getBirthDay())
           .set(USER.SEX, form.getSex().getDatabaseValue())
           .set(USER.PHONE, form.getPhone())
           .set(USER.E_MAIL, form.getEMail())
           .set(USER.NOTE, form.getNote())
           .set(USER.ADDRESS_PK, addressPk)
           .set(USER.OCPP_TAG_PK, selectOcppTagPk(form.getOcppIdTag()))
           .where(USER.USER_PK.eq(form.getUserPk()))
           .execute();
    }

    private void deleteInternal(DSLContext ctx, int userPk) {
        ctx.delete(USER)
           .where(USER.USER_PK.equal(userPk))
           .execute();
    }
}
