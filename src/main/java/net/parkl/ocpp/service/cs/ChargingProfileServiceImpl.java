package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.repository.dto.ChargingProfileAssignment;
import de.rwth.idsg.steve.utils.DateTimeConverter;
import de.rwth.idsg.steve.utils.DateTimeUtils;
import de.rwth.idsg.steve.web.dto.ChargingProfileAssignmentQueryForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.repositories.*;
import net.parkl.stevep.util.ListTransform;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChargingProfileServiceImpl implements ChargingProfileService {
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private OcppChargingProfileRepository chargingProfileRepository;
    @Autowired
    private ConnectorChargingProfileRepository connectorChargingProfileRepository;

    @Autowired
    private ChargingSchedulePeriodRepository chargingSchedulePeriodRepository;

    @Autowired
    private OcppChargeBoxRepository chargeBoxRepository;
    // -------------------------------------------------------------------------
    // OCPP operations
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void setProfile(int chargingProfilePk, String chargeBoxId, int connectorId) {
        OcppChargingProfile profile = chargingProfileRepository.findById(chargingProfilePk).
                orElseThrow(() -> new IllegalArgumentException("Invalid charging profile PK: "+chargingProfilePk));

        Connector connector = connectorRepository.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (connector==null) {
            throw new IllegalArgumentException(String.format("Invalid charge box and charger id: %s-%d",
                    chargeBoxId, connectorId));
        }

        ConnectorChargingProfile p=new ConnectorChargingProfile();
        p.setChargingProfile(profile);
        p.setConnector(connector);
        connectorChargingProfileRepository.save(p);

    }

    @Override
    @Transactional
    public void clearProfile(int chargingProfilePk, String chargeBoxId) {
        OcppChargingProfile profile = chargingProfileRepository.findById(chargingProfilePk).
                orElseThrow(() -> new IllegalArgumentException("Invalid charging profile PK: "+chargingProfilePk));

        List<Connector> connectors = connectorRepository.findByChargeBoxId(chargeBoxId);


        if (!connectors.isEmpty()) {
            connectorChargingProfileRepository.deleteByChargingProfileAndConnectors(profile, connectors);
        }

    }

    @Override
    @Transactional
    public void clearProfile(@NotNull String chargeBoxId,
                             @Nullable Integer connectorId,
                             @Nullable ChargingProfilePurposeType purpose,
                             @Nullable Integer stackLevel) {

        // -------------------------------------------------------------------------
        // Connector select
        // -------------------------------------------------------------------------
        List<Connector> connectors = null;
        if (connectorId == null) {
            connectors = connectorRepository.findByChargeBoxId(chargeBoxId);
        } else {
            connectors = Arrays.asList(connectorRepository.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId));
        }


        // -------------------------------------------------------------------------
        // Profile select
        // -------------------------------------------------------------------------

        List<OcppChargingProfile> profileCondition;

        if (purpose == null && stackLevel == null) {
            profileCondition = null;
        } else {
            profileCondition = chargingProfileRepository.findByPurposeOrNullAndStackLevelOrNull(purpose!=null?purpose.value():null, stackLevel);
        }

        // -------------------------------------------------------------------------
        // Delete execution
        // -------------------------------------------------------------------------
        if (profileCondition==null) {
            connectorChargingProfileRepository.deleteByConnectors(connectors);
        } else {
            connectorChargingProfileRepository.deleteByChargingProfilesAndConnectors(profileCondition, connectors);
        }

    }

    // -------------------------------------------------------------------------
    // CRUD stuff
    // -------------------------------------------------------------------------

    @Override
    public List<ChargingProfileAssignment> getAssignments(ChargingProfileAssignmentQueryForm query) {
        List<ConnectorChargingProfile> connectorChargingProfiles = connectorChargingProfileRepository.search(query.getChargeBoxId(), query.getChargingProfilePk(),
                query.getChargingProfileDescription()!=null?("%"+query.getChargingProfileDescription()+"%"):null);

        List<String> chargeBoxIds = connectorChargingProfiles.stream().map(k -> k.getConnector().getChargeBoxId()).
                collect(Collectors.toList());
        List<OcppChargeBox> chargeBoxes = chargeBoxRepository.findByChargeBoxIdIn(chargeBoxIds);
        Map<String, OcppChargeBox> chargeBoxMap = ListTransform.transformToMap(chargeBoxes, c -> c.getChargeBoxId());
        return connectorChargingProfiles.stream().
                map(k -> ChargingProfileAssignment.builder()
                        .chargeBoxPk(chargeBoxMap.get(k.getConnector().getChargeBoxId()).getChargeBoxPk())
                        .chargeBoxId(k.getConnector().getChargeBoxId())
                        .connectorId(k.getConnector().getConnectorId())
                        .chargingProfilePk(k.getChargingProfile().getChargingProfilePk())
                        .chargingProfileDescription(k.getChargingProfile().getDescription())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public List<ChargingProfile.BasicInfo> getBasicInfo() {
        Iterable<OcppChargingProfile> all = chargingProfileRepository.findAll();
        List<ChargingProfile.BasicInfo> ret=new ArrayList<>();
        for (OcppChargingProfile p : all) {
            ret.add(new ChargingProfile.BasicInfo(p.getChargingProfilePk(), p.getDescription()));
        }
        return ret;
    }

    @Override
    public List<ChargingProfile.Overview> getOverview(ChargingProfileQueryForm form) {

        List<OcppChargingProfile> profiles = chargingProfileRepository.search(
                form.getChargingProfilePk(), form.getStackLevel(),
                form.getDescription()!=null?"%"+form.getDescription()+"%":null,
                form.getProfilePurpose()!=null?form.getProfilePurpose().value():null,
                form.getProfileKind()!=null?form.getProfileKind().value():null,
                form.getRecurrencyKind()!=null?form.getRecurrencyKind().value():null,
                form.getValidFrom()!=null? DateTimeConverter.toDate(form.getValidFrom().toDateTime()) :null,
                form.getValidTo()!=null ? DateTimeConverter.toDate(form.getValidTo().toDateTime()):null);

        return profiles.stream().
                map(r -> ChargingProfile.Overview.builder()
                        .chargingProfilePk(r.getChargingProfilePk())
                        .stackLevel(r.getStackLevel())
                        .description(r.getDescription())
                        .profilePurpose(r.getChargingProfilePurpose())
                        .profileKind(r.getChargingProfileKind())
                        .recurrencyKind(r.getRecurrencyKind())
                        .validFrom(r.getValidFrom()!=null?DateTimeConverter.from(r.getValidFrom()):null)
                        .validTo(r.getValidTo()!=null?DateTimeConverter.from(r.getValidTo()):null)
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public ChargingProfile.Details getDetails(int chargingProfilePk) {
        OcppChargingProfile profile = chargingProfileRepository.findById(chargingProfilePk).
                orElseThrow(() -> new IllegalArgumentException("Invalid charging profile PK: "+chargingProfilePk));


        List<ChargingSchedulePeriod> periods = chargingSchedulePeriodRepository.findByChargingProfile(profile);

        return new ChargingProfile.Details(profile, periods);
    }

    @Override
    @Transactional
    public int add(ChargingProfileForm form) {
        OcppChargingProfile profile = new OcppChargingProfile();
        fillChargingProfile(profile, form);

        OcppChargingProfile saved = chargingProfileRepository.save(profile);

        form.setChargingProfilePk(saved.getChargingProfilePk());

        List<ChargingSchedulePeriod> periods = getPeriodsToInsert(saved, form);
        if (!periods.isEmpty()) {
            chargingSchedulePeriodRepository.saveAll(periods);
        }

        return saved.getChargingProfilePk();


    }

    private void fillChargingProfile(OcppChargingProfile profile, ChargingProfileForm form) {
        profile.setDescription(form.getDescription());
        profile.setNote(form.getNote());
        profile.setStackLevel(form.getStackLevel());
        profile.setChargingProfilePurpose(form.getChargingProfilePurpose().value());
        profile.setChargingProfileKind(form.getChargingProfileKind().value());
        profile.setRecurrencyKind(form.getRecurrencyKind() == null ? null : form.getRecurrencyKind().value());
        profile.setValidFrom(DateTimeUtils.toDateTime(form.getValidFrom()).toDate());
        profile.setValidTo(DateTimeUtils.toDateTime(form.getValidTo()).toDate());
        profile.setDurationInSeconds(form.getDurationInSeconds());
        profile.setStartSchedule(DateTimeUtils.toDateTime(form.getStartSchedule()).toDate());
        profile.setChargingRateUnit(form.getChargingRateUnit().value());
        profile.setMinChargingRate(form.getMinChargingRate());
    }

    @Override
    @Transactional
    public void update(ChargingProfileForm form) {
        OcppChargingProfile profile = chargingProfileRepository.findById(form.getChargingProfilePk()).
                orElseThrow(() -> new IllegalArgumentException("Invalid charging profile PK: "+form.getChargingProfilePk()));

        checkProfileUsage(form.getChargingProfilePk());

        fillChargingProfile(profile, form);

        OcppChargingProfile saved = chargingProfileRepository.save(profile);

        // -------------------------------------------------------------------------
        // the form contains all period information for this schedule. instead of
        // computing a delta about what to insert/update, we can simply delete everything
        // for this profile and re-insert.
        // -------------------------------------------------------------------------
        chargingSchedulePeriodRepository.deleteByChargingProfile(saved);

        List<ChargingSchedulePeriod> periods = getPeriodsToInsert(saved, form);
        if (!periods.isEmpty()) {
            chargingSchedulePeriodRepository.saveAll(periods);
        }

    }

    @Override
    @Transactional
    public void delete(int chargingProfilePk) {
        checkProfileUsage(chargingProfilePk);

        chargingProfileRepository.deleteById(chargingProfilePk);
    }

    private void checkProfileUsage(int chargingProfilePk) {
        OcppChargingProfile profile = chargingProfileRepository.findById(chargingProfilePk).
                orElseThrow(() -> new IllegalArgumentException("Invalid charging profile PK: "+chargingProfilePk));

        List<String> r = connectorChargingProfileRepository.findChargeBoxIdsByChargingProfile(profile);
        if (!r.isEmpty()) {
            throw new SteveException("Cannot modify this charging profile, since the following stations are still using it: %s", r);
        }
    }

    private List<ChargingSchedulePeriod> getPeriodsToInsert(OcppChargingProfile profile, ChargingProfileForm form) {
        if (CollectionUtils.isEmpty(form.getSchedulePeriodMap())) {
            return new ArrayList<>();
        }

        return form.getSchedulePeriodMap()
                .values()
                .stream()
                .map(k -> toChargingSchedulePeriod(profile, k))
                .collect(Collectors.toList());

    }

    private ChargingSchedulePeriod toChargingSchedulePeriod(OcppChargingProfile profile, ChargingProfileForm.SchedulePeriod k) {
        ChargingSchedulePeriod p=new ChargingSchedulePeriod();
        p.setChargingProfile(profile);
        p.setStartPeriodInSeconds(k.getStartPeriodInSeconds());
        p.setPowerLimitInAmperes(k.getPowerLimitInAmperes());
        p.setNumberPhases(k.getNumberPhases());
        return p;
    }

}
