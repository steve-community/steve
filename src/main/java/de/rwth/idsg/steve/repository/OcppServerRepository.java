package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface OcppServerRepository {

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
    boolean updateChargebox(UpdateChargeboxParams params);

    void updateEndpointAddress(String chargeBoxIdentity, String endpointAddress);
    void updateChargeboxFirmwareStatus(String chargeBoxIdentity, String firmwareStatus);
    void updateChargeboxDiagnosticsStatus(String chargeBoxIdentity, String status);
    void updateChargeboxHeartbeat(String chargeBoxIdentity, DateTime ts);

    void insertConnectorStatus(InsertConnectorStatusParams params);

    void insertMeterValues12(String chargeBoxIdentity, int connectorId,
                             List<ocpp.cs._2010._08.MeterValue> list);
    void insertMeterValues15(String chargeBoxIdentity, int connectorId,
                             List<ocpp.cs._2012._06.MeterValue> list, Integer transactionId);
    void insertMeterValues16(String chargeBoxIdentity, int connectorId,
                             List<ocpp.cs._2015._10.MeterValue> list, Integer transactionId);
    void insertMeterValuesOfTransaction(String chargeBoxIdentity, int transactionId,
                                        List<ocpp.cs._2012._06.MeterValue> list);
    void insertMeterValuesOfTransaction16(String chargeBoxIdentity, int transactionId,
                                        List<ocpp.cs._2015._10.MeterValue> list);
    Integer insertTransaction(InsertTransactionParams params);

    void updateTransaction(UpdateTransactionParams params);
}
