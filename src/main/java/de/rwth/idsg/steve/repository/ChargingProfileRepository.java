package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ChargingProfileQueryForm;
import ocpp.cp._2015._10.ChargingProfilePurposeType;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.11.2018
 */
public interface ChargingProfileRepository {

    // -------------------------------------------------------------------------
    // OCPP operations
    // -------------------------------------------------------------------------

    void setProfile(int chargingProfilePk, String chargeBoxId, int connectorId);

    void clearProfile(int chargingProfilePk, String chargeBoxId);

    void clearProfile(@NotNull String chargeBoxId,
                      @Nullable Integer connectorId,
                      @Nullable ChargingProfilePurposeType purpose,
                      @Nullable Integer stackLevel);

    // -------------------------------------------------------------------------
    // CRUD stuff
    // -------------------------------------------------------------------------

    List<ChargingProfile.BasicInfo> getBasicInfo();

    List<ChargingProfile.Overview> getOverview(ChargingProfileQueryForm form);

    ChargingProfile.Details getDetails(int chargingProfilePk);

    int add(ChargingProfileForm form);

    void update(ChargingProfileForm form);

    void delete(int chargingProfilePk);
}
