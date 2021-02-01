package net.parkl.ocpp.service.cs;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.TransactionStartRepository;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
@Transactional
public class MeterValueService {

    private final ConnectorMeterValueRepository connectorMeterValueRepo;
    private final TransactionStartRepository transactionStartRepo;
    private final ConnectorRepository connectorRepo;

    @Autowired
    public MeterValueService(ConnectorMeterValueRepository connectorMeterValueRepo,
                                 TransactionStartRepository transactionStartRepo,
                                 ConnectorRepository connectorRepo) {
        this.connectorMeterValueRepo = connectorMeterValueRepo;
        this.transactionStartRepo = transactionStartRepo;
        this.connectorRepo = connectorRepo;
    }

    public void insertMeterValues(String chargeBoxIdentity, List<MeterValue> meterValue,
                                  int connectorId, Integer transactionId) {
        TransactionStart t=transactionStartRepo.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+transactionId));;

        Connector connector = connectorRepo.findById(connectorId).orElseThrow(() -> new IllegalArgumentException("Invalid connector id: "+connectorId));

        for (ocpp.cs._2015._10.MeterValue mv:meterValue) {
            for (SampledValue v:mv.getSampledValue()) {
                ConnectorMeterValue cmv=new ConnectorMeterValue();
                cmv.setConnector(connector);
                cmv.setTransaction(t);

                cmv.setValue(v.getValue());
                if (mv.getTimestamp()!=null) {
                    cmv.setValueTimestamp(mv.getTimestamp().toDate());
                }
                cmv.setReadingContext(v.isSetContext() ? v.getContext().value() : null);
                cmv.setFormat(v.isSetFormat() ? v.getFormat().value() : null);
                cmv.setMeasurand(v.isSetMeasurand() ? v.getMeasurand().value() : null);
                cmv.setLocation(v.isSetLocation() ? v.getLocation().value() : null);
                cmv.setUnit(v.isSetUnit() ? v.getUnit().value() : null);
                cmv.setPhase(v.isSetPhase() ? v.getPhase().value() : null);
                connectorMeterValueRepo.save(cmv);
            }

        }
    }


    public void insertMeterValues(String chargeBoxIdentity, List<ocpp.cs._2015._10.MeterValue> meterValue,
                                  int transactionId) {
        if (CollectionUtils.isEmpty(meterValue)) {
            return;
        }

        TransactionStart t=transactionStartRepo.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Invalid transaction id: "+transactionId));
        if (t==null) {
            throw new IllegalArgumentException("Invalid transaction id: "+transactionId);
        }


        for (ocpp.cs._2015._10.MeterValue mv:meterValue) {
            for (SampledValue v:mv.getSampledValue()) {
                ConnectorMeterValue cmv=new ConnectorMeterValue();
                cmv.setConnector(t.getConnector());
                cmv.setTransaction(t);

                cmv.setValue(v.getValue());
                if (mv.getTimestamp()!=null) {
                    cmv.setValueTimestamp(mv.getTimestamp().toDate());
                }
                cmv.setReadingContext(v.isSetContext() ? v.getContext().value() : null);
                cmv.setFormat(v.isSetFormat() ? v.getFormat().value() : null);
                cmv.setMeasurand(v.isSetMeasurand() ? v.getMeasurand().value() : null);
                cmv.setLocation(v.isSetLocation() ? v.getLocation().value() : null);
                cmv.setUnit(v.isSetUnit() ? v.getUnit().value() : null);
                cmv.setPhase(v.isSetPhase() ? v.getPhase().value() : null);
                connectorMeterValueRepo.save(cmv);
            }

        }
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
