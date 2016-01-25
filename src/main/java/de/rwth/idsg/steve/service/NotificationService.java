package de.rwth.idsg.steve.service;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 22.01.2016
 */
public interface NotificationService {
    void ocppStationBooted(String chargeBoxId, boolean isRegistered);
    void ocppStationStatusFailure(String chargeBoxId, int connectorId, String errorCode);
}
