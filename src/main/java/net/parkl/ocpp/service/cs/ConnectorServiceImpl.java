package net.parkl.ocpp.service.cs;

import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.TransactionStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.Connector;
import net.parkl.ocpp.entities.ConnectorStatus;
import net.parkl.ocpp.entities.OcppChargingProcess;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.ConnectorStatusRepository;
import net.parkl.ocpp.repositories.OcppChargingProcessRepository;
import net.parkl.ocpp.service.OcppConstants;
import net.parkl.ocpp.service.OcppErrorTranslator;
import net.parkl.ocpp.service.OcppMiddleware;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class ConnectorServiceImpl implements ConnectorService {
    @Autowired
    private OcppErrorTranslator errorTranslator;
    @Autowired
    private OcppChargingProcessRepository chargingProcessRepo;
    @Autowired
    private ConnectorRepository connectorRepo;
    @Autowired
    private ConnectorStatusRepository connectorStatusRepo;
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor executor;

    @Autowired
    private OcppMiddleware ocppMiddleware;



    @Override
    @Transactional
    public void insertConnectorStatus(InsertConnectorStatusParams p) {
        Connector connector = createConnectorIfNotExists(p.getChargeBoxId(), p.getConnectorId());

        ConnectorStatus s=new ConnectorStatus();
        s.setConnector(connector);
        if (p.getTimestamp()!=null) {
            s.setStatusTimestamp(p.getTimestamp().toDate());
        }
        s.setStatus(p.getStatus());
        s.setErrorCode(p.getErrorCode());
        s.setErrorInfo(p.getErrorInfo());
        s.setVendorId(p.getVendorId());
        s.setVendorErrorCode(p.getVendorErrorCode());

        connectorStatusRepo.save(s);

        OcppChargingProcess savedProcess=null;
        if (s.getStatus().equals("Available")) {
            OcppChargingProcess process = chargingProcessRepo.findByConnectorAndTransactionStartIsNullAndEndDateIsNull(connector);
            if (process!=null) {
                log.info("Ending charging process on available connector status: {}",process.getOcppChargingProcessId());
                process.setEndDate(new Date());
                savedProcess=chargingProcessRepo.save(process);
            }
        } else if (s.getStatus().equals("Faulted") || s.getStatus().equals("Unavailable")) {
            OcppChargingProcess process = chargingProcessRepo.findByConnectorAndTransactionStartIsNullAndEndDateIsNull(connector);
            if (process!=null) {
                log.info("Saving connector status error to charging process: {} [error={}]...",process.getOcppChargingProcessId(),
                        s.getErrorCode());
                String error = errorTranslator.translateError(s.getErrorCode());
                if (error!=null) {
                    process.setErrorCode(error);
                    savedProcess = chargingProcessRepo.save(process);
                }
            }
        }
        log.debug("Stored a new connector status for {}/{}.", p.getChargeBoxId(), p.getConnectorId());

        if (savedProcess!=null) {
            final OcppChargingProcess pr=savedProcess;
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    log.info("Notifying Parkl about closing charging process: {}...",pr.getOcppChargingProcessId());
                    ocppMiddleware.stopChargingExternal(pr, pr.getErrorCode()!=null?pr.getErrorCode(): OcppConstants.REASON_VEHICLE_NOT_CONNECTED);
                }
            });
        }
    }

    @Override
    public Connector createConnectorIfNotExists(String chargeBoxId, int connectorId) {
        Connector c = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);
        if (c == null) {
            c = new Connector();
            c.setChargeBoxId(chargeBoxId);
            c.setConnectorId(connectorId);
            c = connectorRepo.save(c);
        }
        return c;
    }

    @Override
    public Optional<Connector> findById(int connectorId) {
        return connectorRepo.findById(connectorId);
    }

    @Override
    @Transactional
    public void createConnectorStatus(Connector connector, DateTime startTimestamp, TransactionStatusUpdate statusUpdate) {
        ConnectorStatus s = new ConnectorStatus();
        s.setConnector(connector);
        if (startTimestamp != null) {
            s.setStatusTimestamp(startTimestamp.toDate());
        }
        s.setStatus(statusUpdate.getStatus());
        s.setErrorCode(statusUpdate.getErrorCode());

        connectorStatusRepo.save(s);
    }
}
