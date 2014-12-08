package de.rwth.idsg.steve.repository;

import com.google.common.base.Optional;
import de.rwth.idsg.steve.OcppVersion;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface OcppServiceRepository {

    /**
     * One DB call with two functions:
     *
     * 1. Update all fields with the exception of chargeBoxId, so that initially it is enough to register
     * a chargebox with its ID in DB. During boot, the chargebox provides missing information which might
     * be updated (for ex: firmware)
     *
     * 2. If the chargebox not registered => no chargeboxes to update => updated/returned row count = 0
     *
     */
    boolean updateChargebox(String endpoint_address, OcppVersion ocppVersion, String vendor, String model,
                            String pointSerial, String boxSerial, String fwVersion, String iccid, String imsi,
                            String meterType, String meterSerial, String chargeBoxIdentity, Timestamp now);

    void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String firmwareStatus);
    void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);
    void updateChargeboxHeartbeat(String chargeBoxIdentity, Timestamp ts);

    void insertConnectorStatus12(String chargeBoxIdentity, int connectorId, String status, Timestamp timestamp,
                                 String errorCode);
    void insertConnectorStatus15(String chargeBoxIdentity, int connectorId, String status, Timestamp timestamp,
                                 String errorCode, String errorInfo,
                                 String vendorId, String vendorErrorCode);

    void insertMeterValues12(String chargeBoxIdentity, int connectorId,
                             List<ocpp.cs._2010._08.MeterValue> list);
    void insertMeterValues15(String chargeBoxIdentity, int connectorId,
                             List<ocpp.cs._2012._06.MeterValue> list, Integer transactionId);
    void insertMeterValuesOfTransaction(String chargeBoxIdentity, int transactionId,
                                        List<ocpp.cs._2012._06.MeterValue> list);

    Optional<Integer> insertTransaction12(String chargeBoxIdentity, int connectorId, String idTag,
                                          Timestamp startTimestamp, String startMeterValue);

    Optional<Integer> insertTransaction15(String chargeBoxIdentity, int connectorId, String idTag,
                                          Timestamp startTimestamp, String startMeterValue,
                                          Integer reservationId);

    void updateTransaction(int transactionId, Timestamp stopTimestamp, String stopMeterValue);
}
