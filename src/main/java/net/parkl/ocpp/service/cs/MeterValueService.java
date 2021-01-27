package net.parkl.ocpp.service.cs;

import ocpp.cs._2015._10.MeterValue;

import java.util.List;

public interface MeterValueService {


    void insertMeterValues(String chargeBoxIdentity, List<MeterValue> meterValue, int connectorId,
                           Integer transactionId);

    void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> transactionData,
                           int transactionId);

}
