package net.parkl.ocpp.service;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MeterValueService {

    private final ConnectorMeterValueRepository connectorMeterValueRepo;

    public MeterValueService(ConnectorMeterValueRepository connectorMeterValueRepo) {
        this.connectorMeterValueRepo = connectorMeterValueRepo;
    }

    public List<ConnectorMeterValue> getConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction,
                                                                                     String measurand) {
        return connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction,
                measurand);
    }

    public ConnectorMeterValue getLastConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction,
                                                                                   String measurand) {
        List<ConnectorMeterValue> list =
                connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction,
                        measurand);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
