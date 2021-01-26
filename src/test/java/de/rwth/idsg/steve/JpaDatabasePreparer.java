package de.rwth.idsg.steve;


import de.rwth.idsg.steve.repository.dto.*;



import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaDatabasePreparer {

    public void prepare() {

    }
    public String getRegisteredOcppTag() {
        return null;
    }

    public String getRegisteredChargeBoxId() {
        return null;
    }

    public String getRegisteredChargeBoxId2() {
        return null;
    }

    public List<ConnectorStatus> getChargePointConnectorStatus() {
        return null;
    }

    public List<net.parkl.ocpp.entities.Transaction> getTransactionRecords() {
        return null;
    }

    public TransactionDetails getDetails(int transactionPk) {
        return null;
    }

    public ChargePoint.Details getCBDetails(String registeredChargeBoxId) {
        return null;
    }

    public int makeReservation(int usedConnectorID) {
        return 0;
    }

    public List<Transaction> getTransactions() {
        return null;
    }

    public net.parkl.ocpp.entities.OcppTag getOcppTagRecord(String registeredOcppTag) {
        return null;
    }

    public OcppTag.Overview getOcppTag(String registeredOcppTag) {
        return null;
    }

    public List<Reservation> getReservations() {
        return null;
    }
}
