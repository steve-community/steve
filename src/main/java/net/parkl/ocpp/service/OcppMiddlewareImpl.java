package net.parkl.ocpp.service;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.service.ChargePointHelperService;
import de.rwth.idsg.steve.service.IChargePointService12_Client;
import de.rwth.idsg.steve.service.IChargePointService15_Client;
import de.rwth.idsg.steve.service.IChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.*;
import net.parkl.ocpp.repositories.ConnectorRepository;
import net.parkl.ocpp.repositories.ConnectorStatusRepository;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.cs.ChargePointService;
import net.parkl.ocpp.service.cs.OcppIdTagService;
import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.util.AsyncWaiter;
import ocpp.cp._2012._06.AvailabilityStatus;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;

import static de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType.INOPERATIVE;
import static de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType.OPERATIVE;
import static java.lang.Float.parseFloat;
import static java.util.Collections.singletonList;
import static net.parkl.ocpp.module.esp.ESPErrorCodes.*;
import static net.parkl.ocpp.service.OcppConstants.*;
import static ocpp.cp._2012._06.RemoteStartStopStatus.ACCEPTED;

;

/**
 * Middleware component between the e-mobility service provider (ESP) backend and the SteVe Pluggable library.<br>
 *
 * @author andor
 */
@Component
@Slf4j
public class OcppMiddlewareImpl implements OcppMiddleware {


    private static final long WAIT_MS = 100;

    @Autowired
    private TaskStore requestTaskStore;
    @Autowired
    @Qualifier("ChargePointService15_Client")
    private IChargePointService15_Client client15;
    @Autowired
    @Qualifier("ChargePointService12_Client")
    private IChargePointService12_Client client12;
    @Autowired
    @Qualifier("ChargePointService16_Client")
    private IChargePointService16_Client client16;
    @Autowired
    private OcppChargeBoxRepository chargeBoxRepo;
    @Autowired
    private ConnectorRepository connectorRepo;
    @Autowired
    private ConnectorStatusRepository connectorStatusRepo;
    @Autowired
    private ChargePointHelperService chargePointHelperService;
    @Autowired
    private ChargePointService chargePointService;
    @Autowired
    private OcppProxyService proxyService;
    @Autowired
    private OcppIdTagService idTagService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AdvancedChargeBoxConfiguration config;
    @Autowired
    private EmobilityServiceProvider emobilityServiceProvider;
    @Autowired
    private OcppConsumptionHelper consumptionHelper;
    private OcppConsumptionListener consumptionListener;
    private OcppStopListener stopListener;


    public ESPChargingStartResult startCharging(ESPChargingStartRequest req) {
        log.info("Starting charging: {}-{} (licensePlate={})...",
                req.getChargeBoxId(),
                req.getChargerId(),
                req.getLicensePlate());
        if (StringUtils.isEmpty(req.getChargeBoxId())) {
            log.error("Charger box id not specified");
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();

        }
        if (StringUtils.isEmpty(req.getChargerId())) {
            log.error("Charger id not specified");
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();

        }
        ChargerIdentity id = toIdentity(req.getChargeBoxId(), req.getChargerId());


        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(id.getChargeBoxId());
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", id.getChargeBoxId());
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();

        }


        ChargePointSelect c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", id.getChargeBoxId());
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();

        }

        OcppChargingProcess existing =
                proxyService.findOpenChargingProcessWithoutTransaction(id.getChargeBoxId(), id.getConnectorId());
        if (existing != null) {
            log.error("Charging process open: {}", existing.getOcppChargingProcessId());
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_OCCUPIED).build();
        }

        String idTag = getAvailableIntegrationIdTag(chargeBox);
        if (idTag == null) {
            log.error("No ID tag not found");
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
        }

        OcppTag tag = idTagService.getRecord(idTag);
        if (tag == null) {
            log.error("ID tag not found: {}", idTag);
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
        }

        RemoteStartTransactionParams p = new RemoteStartTransactionParams();
        p.setIdTag(idTag);
        p.setChargePointSelectList(singletonList(c));
        p.setConnectorId(id.getConnectorId());
        int taskId = sendRemoteStartTransaction(p, chargeBox.getOcppProtocol());


        RequestResult result = waitForResult(req.getChargeBoxId(), taskId);

        if (result != null) {
            if (result.getResponse() != null) {
                if (result.getResponse().equals(ACCEPTED.value())) {
                    log.info("Proxy transaction accepted: {}", id.getChargeBoxId());
                    OcppChargingProcess process = proxyService.createChargingProcess(id.getChargeBoxId(),
                            id.getConnectorId(),
                            idTag,
                            req.getLicensePlate(),
                            req.getLimitKwh(),
                            req.getLimitMin());
                    log.info("Charging process created: {}", process.getOcppChargingProcessId());
                    return ESPChargingStartResult.builder()
                            .externalChargingProcessId(String.valueOf(process.getOcppChargingProcessId())).build();


                } else {
                    log.info("Proxy transaction rejected: {}", id.getChargeBoxId());
                    return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
                }
            } else if (result.getErrorMessage() != null) {
                log.info("Proxy transaction error ({}): {}", result.getErrorMessage(), id.getChargeBoxId());
                return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();

            } else {
                log.info("Proxy start transaction unknown error: {}", id.getChargeBoxId());
                return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();

            }
        } else {
            log.info("Proxy start transaction timeout: {}", id.getChargeBoxId());
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_OFFLINE).build();

        }


    }


    private String getAvailableIntegrationIdTag(OcppChargeBox cb) {
        List<OcppChargingProcess> processes = proxyService.getActiveProcessesByChargeBox(cb.getChargeBoxId());
        Set<String> idTagsUsed = new HashSet<>();
        for (OcppChargingProcess t : processes) {
            if (t.getOcppTag() != null) {
                idTagsUsed.add(t.getOcppTag().toLowerCase());
            }
        }

        List<String> tags = config.getIntegrationIdTags();
        for (String tag : tags) {
            if (!idTagsUsed.contains(tag.toLowerCase())) {
                log.info("Available id tag found: {}", tag);
                return tag;
            }
        }
        log.error("No available id tag found");
        return null;
    }

    private int sendRemoteStartTransaction(RemoteStartTransactionParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);
        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.remoteStartTransaction(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.remoteStartTransaction(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.remoteStartTransaction(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    @SuppressWarnings("rawtypes")
    private RequestResult getResponse(int taskId, String chargeBoxId) {
        CommunicationTask task = requestTaskStore.get(taskId);
        RequestResult result = (RequestResult) task.getResultMap().get(chargeBoxId);
        if (result != null && (result.getResponse() != null || result.getErrorMessage() != null)) {
            return result;
        }
        return null;
    }

    public ESPChargingResult stopCharging(ESPChargingUserStopRequest req) {
        log.info("Stopping charging: {}...", req.getExternalChargeId());
        return doStopCharging(req.getExternalChargeId());

    }

    private ESPChargingResult doStopCharging(String ocppChargingProcessId) {
        OcppChargingProcess ocppChargingProcess = proxyService.findOcppChargingProcess(ocppChargingProcessId);
        if (ocppChargingProcess == null) {
            log.info("Invalid charge id: {}", ocppChargingProcessId);
            return ESPChargingResult.builder().errorCode(ERROR_CODE_INVALID_EXTERNAL_CHARGE_ID).build();

        }


        if (ocppChargingProcess.getTransaction() != null) {
            Transaction transaction = transactionService
                    .findTransaction(ocppChargingProcess.getTransaction().getTransactionPk())
                    .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " +
                            ocppChargingProcess.getTransaction().getTransactionPk()));

            if (transaction.getStopTimestamp() != null) {
                log.info("Charging already stopped: {}", ocppChargingProcessId);
                ESPChargingData data =
                        ESPChargingData.builder()
                                .startValue(consumptionHelper.getStartValue(transaction))
                                .stopValue(consumptionHelper.getStopValue(transaction))
                                .totalPower(consumptionHelper.getTotalPower(transaction))
                                .start(ocppChargingProcess.getStartDate()).end(ocppChargingProcess.getEndDate()).build();
                return ESPChargingResult.builder()
                        .errorCode(ERROR_CODE_CHARGING_ALREADY_STOPPED).chargingData(data).build();
            }
        }

        ChargePointSelect chargePointSelect = null;
        OcppChargeBox chargeBox = null;
        if (ocppChargingProcess.getTransaction() != null) {
            chargeBox = chargeBoxRepo
                    .findByChargeBoxId(ocppChargingProcess.getTransaction().getConnector().getChargeBoxId());
            if (chargeBox == null) {
                log.error("Invalid charge box id: {}",
                        ocppChargingProcess.getTransaction().getConnector().getChargeBoxId());
                return ESPChargingResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();
            }

            chargePointSelect = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
            if (chargePointSelect == null) {
                log.error("Invalid charge point id: {}",
                        ocppChargingProcess.getTransaction().getConnector().getChargeBoxId());
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            }
        }


        OcppChargingProcess stopRequested = proxyService.stopRequested(ocppChargingProcess.getOcppChargingProcessId());


        if (stopRequested.getTransaction() != null) {
            log.info("Stopping charging transaction: {}...", stopRequested.getTransaction().getTransactionPk());

            RemoteStopTransactionParams params = new RemoteStopTransactionParams();
            params.setChargePointSelectList(singletonList(chargePointSelect));
            params.setTransactionId(stopRequested.getTransaction().getTransactionPk());
            int taskId = sendRemoteStopTransaction(params, chargeBox != null ? chargeBox.getOcppProtocol() : null);

            RequestResult result = waitForResult(stopRequested.getTransaction().getConnector().getChargeBoxId(), taskId);

            return processRemoteStopResult(ocppChargingProcessId, stopRequested.getTransaction().getTransactionPk(), result);


        } else {
            //Charging process started without transaction
            log.info("Stopping charging without transaction: {}...", stopRequested.getOcppChargingProcessId());
            OcppChargingProcess process = proxyService.stopChargingProcess(stopRequested.getOcppChargingProcessId());

            ESPChargingData data = ESPChargingData.builder().start(process.getStartDate()).end(process.getEndDate()).build();
            ESPChargingResult res = ESPChargingResult.builder().chargingData(data).stoppedWithoutTransaction(true).build();

            if (stopListener != null) {
                stopListener.chargingStopped(process, res.getChargingData(), null);
            }
            return res;
        }
    }

    private ESPChargingResult processRemoteStopResult(String externalChargeId, int transactionId,
                                                      RequestResult result) {
        if (result != null) {
            if (result.getResponse() != null) {
                if (result.getResponse().equals(ACCEPTED.value())) {
                    log.info("Proxy transaction stop accepted: {}", transactionId);
                    proxyService.stopChargingProcess(externalChargeId);
                    return ESPChargingResult.builder().stoppedWithoutTransaction(false).build();
                } else {
                    log.info("Proxy transaction stop rejected: {}", transactionId);
                    proxyService.stopRequestCancelled(externalChargeId);
                    return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
                }
            } else if (result.getErrorMessage() != null) {
                log.info("Proxy transaction error ({}): {}", result.getErrorMessage(), transactionId);
                proxyService.stopRequestCancelled(externalChargeId);
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            } else {
                log.info("Proxy stop transaction unknown error: {}", transactionId);
                proxyService.stopRequestCancelled(externalChargeId);
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            }
        } else {
            log.error("No response arrived from charger for stop transaction: {}", transactionId);
            proxyService.stopRequestCancelled(externalChargeId);
            return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_OFFLINE).build();
        }
    }


    private int sendRemoteStopTransaction(RemoteStopTransactionParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.remoteStopTransaction(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.remoteStopTransaction(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.remoteStopTransaction(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    private int sendGetConfiguration(GetConfigurationParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.getConfiguration(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.getConfiguration(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    @SuppressWarnings("unused")
    private int sendGetLocalListVersion(MultipleChargePointSelect params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.getLocalListVersion(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.getLocalListVersion(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    @SuppressWarnings("unused")
    private int sendLocalList(SendLocalListParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.sendLocalList(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.sendLocalList(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    private int sendChangeConfiguration(ChangeConfigurationParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_15_SOAP:
            case V_15_JSON:
                return client15.changeConfiguration(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.changeConfiguration(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    private ChargerIdentity toIdentity(String chargeBoxId, String chargerId) {
        ChargerIdentity id = new ChargerIdentity();
        id.setChargeBoxId(chargeBoxId);
        id.setConnectorId(Integer.parseInt(chargerId));
        return id;
    }


    private ChargePointSelect getChargePoint(String chargeBoxId, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);
        List<ChargePointSelect> chargePoints;
        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_12);
                break;
            case V_15_SOAP:
            case V_15_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_15);
                break;
            case V_16_SOAP:
            case V_16_JSON:
                chargePoints = chargePointHelperService.getChargePoints(OcppVersion.V_16);
                break;
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }

        for (ChargePointSelect c : chargePoints) {
            if (c.getChargeBoxId().equals(chargeBoxId)) {
                return c;
            }
        }
        return null;
    }

    public ESPChargingStatusResult getStatus(String externalChargeId) {
        log.info("Status request: {}...", externalChargeId);

        OcppChargingProcess ocppChargingProcess = proxyService.findOcppChargingProcess(externalChargeId);
        if (ocppChargingProcess == null) {
            log.info("Invalid charge id: {}", externalChargeId);
            return ESPChargingStatusResult.builder().errorCode(ERROR_CODE_INVALID_EXTERNAL_CHARGE_ID).build();

        }

        Transaction transaction = null;
        if (ocppChargingProcess.getTransaction() != null) {
            transaction = transactionService
                    .findTransaction(ocppChargingProcess.getTransaction().getTransactionPk())
                    .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " +
                            ocppChargingProcess.getTransaction().getTransactionPk()));

            if (transaction.getStopTimestamp() != null) {
                log.info("Charging already stopped: {}", externalChargeId);
                return ESPChargingStatusResult.builder().errorCode(ERROR_CODE_CHARGING_ALREADY_STOPPED).build();
            }
        }

        ESPChargingStatusResult ret = ESPChargingStatusResult.builder()
                .status(ESPChargingStatus.builder().build())
                .chargingData(ESPChargingData.builder().build())
                .build();

        ret.getChargingData().setStart(ocppChargingProcess.getStartDate());
        if (transaction != null) {
            PowerValue pw = getPowerValue(ocppChargingProcess.getTransaction());
            ConnectorMeterValue activePower =
                    proxyService.getLastConnectorMeterValueByTransactionAndMeasurand(ocppChargingProcess.getTransaction(),
                            MEASURAND_POWER_ACTIVE_IMPORT);
            if (activePower != null) {
                ret.getStatus().setThroughputPower(OcppConsumptionHelper.getKwValue(parseFloat(activePower.getValue()),
                        activePower.getUnit()));
            }

            ret.getChargingData().setTotalPower(OcppConsumptionHelper.getKwhValue(pw.getValue(), pw.getUnit()));
            ret.getChargingData().setStartValue(consumptionHelper.getStartValue(transaction));
            ret.getChargingData().setStopValue(consumptionHelper.getStopValue(transaction));
        }
        return ret;
    }

    public PowerValue getPowerValue(TransactionStart transaction) {
        List<ConnectorMeterValue> connectorMeterValues =
                proxyService.getConnectorMeterValueByTransactionAndMeasurand(transaction, MEASURAND_ENERGY_ACTIVE_IMPORT);
        float diff = 0;
        String diffUnit = null;
        if (!connectorMeterValues.isEmpty()) {

            if (connectorMeterValues.size() > 1) {
                diffUnit = connectorMeterValues.get(0).getUnit();
                diff = parseFloat(connectorMeterValues.get(0).getValue())
                        - parseFloat(connectorMeterValues.get(connectorMeterValues.size() - 1).getValue());
            }
        } else {
            //handle Mennekes type chargers (no measurand, no unit)
            connectorMeterValues = proxyService.getConnectorMeterValueByTransactionAndMeasurand(transaction, null);
            if (connectorMeterValues.size() > 1) {
                diffUnit = connectorMeterValues.get(0).getUnit();
                if (diffUnit == null) {
                    diffUnit = UNIT_WH;
                }
                diff = parseFloat(connectorMeterValues.get(0).getValue())
                        - parseFloat(connectorMeterValues.get(connectorMeterValues.size() - 1).getValue());
            }
        }
        return new PowerValue(diff, diffUnit);

    }

    @Override
    public List<ESPChargeBoxConfiguration> getChargeBoxConfiguration(String chargeBoxId) {
        log.info("Configuration request: {}...", chargeBoxId);
        ChargePointSelect c;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }

        GetConfigurationParams params = new GetConfigurationParams();
        params.setChargePointSelectList(Arrays.asList(c));
        int taskId = sendGetConfiguration(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);

        return processGetConfigurationResult(chargeBoxId, result);

    }

    private RequestResult waitForResult(String chargeBoxId, int taskId) {
        AsyncWaiter<RequestResult> waiter = new AsyncWaiter<>(10000);
        waiter.setDelayMs(WAIT_MS);
        waiter.setIntervalMs(WAIT_MS);
        RequestResult result = waiter.waitFor(new Callable<RequestResult>() {
            @Override
            public RequestResult call() throws Exception {
                return getResponse(taskId, chargeBoxId);
            }
        });
        return result;
    }

    private List<ESPChargeBoxConfiguration> processGetConfigurationResult(String chargeBoxId,
                                                                          RequestResult result) {
        if (result != null) {
            if (result.getResponse() != null) {
                return parseConfList(result.getResponse());
            } else if (result.getErrorMessage() != null) {
                throw new IllegalStateException(result.getErrorMessage());
            } else {
                log.info("Get configuration unknown error: {}", chargeBoxId);
                throw new IllegalStateException("Unknown error: " + chargeBoxId);

            }
        } else {
            log.info("Get configuration no response error: {}", chargeBoxId);
            throw new IllegalStateException("No response from charge box: " + chargeBoxId);
        }
    }

    private void processGenericResult(String type, String chargeBoxId,
                                      RequestResult result) {
        if (result != null) {
            if (result.getResponse() != null) {
                if (result.getResponse().equals(AvailabilityStatus.ACCEPTED.value())) {
                    log.info("{} accepted: {}", type, chargeBoxId);

                } else {
                    log.info("{} rejected: {}", type, chargeBoxId);
                    throw new IllegalStateException(type + " rejected: " + chargeBoxId);
                }
            } else if (result.getErrorMessage() != null) {
                throw new IllegalStateException(result.getErrorMessage());
            } else {
                log.info("{} unknown error: {}", type, chargeBoxId);
                throw new IllegalStateException("Unknown error: " + chargeBoxId);

            }
        } else {
            log.info("{} no response error: {}", type, chargeBoxId);
            throw new IllegalStateException("No response from charge box: " + chargeBoxId);
        }
    }


    private List<ESPChargeBoxConfiguration> parseConfList(String response) {
        log.info("Parsing configuration: {}...", response);
        List<ESPChargeBoxConfiguration> ret = new ArrayList<>();
        String[] split = response.split("<br>");
        for (String line : split) {
            log.info("Parsing config line: {}...", line);
            if (line.startsWith("<b>Unknown keys")) {
                log.info("Unknown keys reached, exiting...");
                break;
            }
            if (!StringUtils.isEmpty(line.trim()) && !line.startsWith("<b>Known keys")) {
                boolean readOnly = false;
                if (line.endsWith(" (read-only)")) {
                    line = line.replace(" (read-only)", "");
                    readOnly = true;
                }
                String[] keyVal = line.split("\\:");

                ESPChargeBoxConfiguration c = ESPChargeBoxConfiguration.builder().
                        key(keyVal[0].trim()).value(keyVal[1].trim()).readOnly(readOnly).build();
                log.info("Configuration parsed: {}={} (read-only={})", c.getKey(), c.getValue(), c.isReadOnly());
                ret.add(c);
            }
        }
        return ret;
    }

    @Override
    public List<ESPChargeBoxConfiguration> changeChargeBoxConfiguration(String chargeBoxId, String key,
                                                                        String value) {
        log.info("Configuration change request for {}: {}={}...", chargeBoxId, key, value);
        ChargePointSelect c = null;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }


        ChangeConfigurationParams params = new ChangeConfigurationParams();
        params.setChargePointSelectList(Arrays.asList(c));
        params.setConfKey(key);
        params.setValue(value);
        int taskId = sendChangeConfiguration(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Change configuration", chargeBoxId, result);

        return getChargeBoxConfiguration(chargeBoxId);
    }

    @Override
    public void registerChargeBox(String chargeBoxId) {
        log.info("Register charge box request: {}...", chargeBoxId);

        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox != null) {
            log.error("Charge box already exists: {}", chargeBoxId);
            throw new IllegalArgumentException("Charge box already exists: " + chargeBoxId);
        }

        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxId(chargeBoxId);
        form.setAddress(new Address());
        form.setRegistrationStatus(RegistrationStatus.ACCEPTED.value());
        chargePointService.addChargePoint(form);
    }

    @Override
    public void unregisterChargeBox(String chargeBoxId) {
        log.info("Unregister charge box request: {}...", chargeBoxId);

        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        chargePointService.deleteChargePoint(chargeBox.getChargeBoxPk());
    }

    @Override
    public void changeAvailability(String chargeBoxId, String chargerId, boolean available) {
        log.info("Availability change request for {}-{}: {}...", chargeBoxId, chargerId, available);
        ChargePointSelect c;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }

        ChangeAvailabilityParams params = new ChangeAvailabilityParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConnectorId(Integer.parseInt(chargerId));
        params.setAvailType(available ? OPERATIVE : INOPERATIVE);
        int taskId = sendChangeAvailability(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Change availability", chargeBoxId, result);
    }

    private int sendChangeAvailability(ChangeAvailabilityParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.changeAvailability(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.changeAvailability(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.changeAvailability(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }


    @Override
    public void unlockConnector(String chargeBoxId, String chargerId) {
        log.info("Unlock connector request for {}-{}...", chargeBoxId, chargerId);
        ChargePointSelect c;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }

        UnlockConnectorParams params = new UnlockConnectorParams();
        params.setChargePointSelectList(singletonList(c));
        params.setConnectorId(Integer.parseInt(chargerId));
        int taskId = sendUnlockConnector(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Unlock connector", chargeBoxId, result);
    }

    private int sendUnlockConnector(UnlockConnectorParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.unlockConnector(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.unlockConnector(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.unlockConnector(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }

    @Override
    public void resetChargeBox(String chargeBoxId, boolean soft) {
        log.info("Reset request for {} (soft={}}...", chargeBoxId, soft);
        ChargePointSelect c = null;
        OcppChargeBox chargeBox = chargeBoxRepo.findByChargeBoxId(chargeBoxId);
        if (chargeBox == null) {
            log.error("Invalid charge box id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);
        }

        c = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
        if (c == null) {
            log.error("Invalid charge point id: {}", chargeBoxId);
            throw new IllegalArgumentException("Invalid charge box ID: " + chargeBoxId);

        }

        ResetParams params = new ResetParams();
        params.setChargePointSelectList(singletonList(c));
        params.setResetType(soft ? ResetType.SOFT : ResetType.HARD);
        int taskId = sendResetRequest(params, chargeBox.getOcppProtocol());

        RequestResult result = waitForResult(chargeBoxId, taskId);
        processGenericResult("Reset request", chargeBoxId, result);
    }

    private int sendResetRequest(ResetParams params, String protocol) {
        OcppProtocol ocppProtocol = OcppProtocol.fromCompositeValue(protocol);

        switch (ocppProtocol) {
            case V_12_SOAP:
            case V_12_JSON:
                return client12.reset(params);
            case V_15_SOAP:
            case V_15_JSON:
                return client15.reset(params);
            case V_16_SOAP:
            case V_16_JSON:
                return client16.reset(params);
            default:
                throw new IllegalStateException("OCPP protocol not supported: " + ocppProtocol);
        }
    }


    @Override
    public void stopChargingExternal(OcppChargingProcess process, String reason) {
        if (process == null) {
            throw new IllegalArgumentException("OcppChargingProcess was null");
        }
        log.info("Stopping charging process from OCPP proxy: {}...", process.getOcppChargingProcessId());

        Transaction transaction = transactionService.findTransaction(process.getTransaction().getTransactionPk()).
                orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + process.getTransaction().getTransactionPk()));


        ESPChargingData data = ESPChargingData.builder().
                start(process.getStartDate()).
                end(process.getEndDate()).
                totalPower(consumptionHelper.getTotalPower(transaction)).
                startValue(consumptionHelper.getStartValue(transaction)).
                stopValue(consumptionHelper.getStartValue(transaction)).build();

        ESPChargingStopRequest req = ESPChargingStopRequest.builder().
                externalChargeId(process.getOcppChargingProcessId()).
                eventCode(reason).
                chargingData(data).build();

        emobilityServiceProvider.stopChargingExternal(req);

        if (stopListener != null) {
            stopListener.chargingStopped(process, req.getChargingData(), reason);
        }
    }

    @Override
    public void updateConsumption(OcppChargingProcess process, String startValue, String stopValue) {
        if (process == null) {
            throw new IllegalArgumentException("OcppChargingProcess was null");
        }
        log.info("Updating charging process consumption from OCPP proxy: {}...", process.getOcppChargingProcessId());

        Transaction transaction = transactionService.findTransaction(process.getTransaction().getTransactionPk()).
                orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + process.getTransaction().getTransactionPk()));

        ESPChargingConsumptionRequest req = ESPChargingConsumptionRequest.builder().
                externalChargeId(process.getOcppChargingProcessId()).
                start(process.getStartDate()).
                end(process.getEndDate()).
                totalPower(consumptionHelper.getTotalPower(transaction)).
                startValue(consumptionHelper.getStartValue(transaction)).
                stopValue(consumptionHelper.getStartValue(transaction)).
                build();

        emobilityServiceProvider.updateChargingConsumptionExternal(req);

        if (consumptionListener != null) {
            consumptionListener.consumptionUpdated(req);
        }
    }

    @Override
    public boolean isConnectorCharging(String chargeBoxId, int connectorId) {
        return proxyService.findOpenChargingProcess(chargeBoxId, connectorId) != null;
    }

    public void registerConsumptionListener(OcppConsumptionListener l) {
        this.consumptionListener = l;
    }

    public void registerStopListener(OcppStopListener l) {
        this.stopListener = l;
    }


    @Override
    public ESPChargerStatusResult getChargerStatuses() {
        log.info("Querying all connector statuses...");
        List<Connector> connectors = connectorRepo.findAllByOrderByConnectorPkAsc();
        ESPChargerStatusResult ret = ESPChargerStatusResult.builder().status(new ArrayList<>()).build();


        for (Connector connector : connectors) {
            ESPChargerStatus dto = ESPChargerStatus.builder().
                    externalChargerId(String.format("%s_%d", connector.getChargeBoxId(), connector.getConnectorId())).build();

            ConnectorStatus status = connectorStatusRepo.findFirstByConnectorOrderByStatusTimestampDesc(connector);
            if (status != null) {
                switch (status.getStatus()) {
                    case "Available":
                        dto.setState(ESPChargerState.Free);
                        break;
                    case "Charging":
                    case "Preparing":
                    case "Finishing":
                        dto.setState(ESPChargerState.Occupied);
                        break;
                    default:
                        dto.setState(ESPChargerState.Error);
                        break;
                }
            }
            ret.getStatus().add(dto);
        }
        return ret;
    }

    @Override
    public ESPChargerState getChargerStatus(String chargeBoxId, int connectorId) {
        Connector connector = connectorRepo.findByChargeBoxIdAndConnectorId(chargeBoxId, connectorId);

        if (connector == null) {
            throw new IllegalArgumentException("OcppConnector was null");
        }

        ConnectorStatus connectorStatus = connectorStatusRepo.findFirstByConnectorOrderByStatusTimestampDesc(connector);

        if (connectorStatus != null) {
            switch (connectorStatus.getStatus()) {
                case "Available":
                    return ESPChargerState.Free;
                case "Charging":
                case "Preparing":
                case "Finishing":
                    return ESPChargerState.Occupied;
                default:
                    return ESPChargerState.Error;
            }
        }

        return null;
    }


    @Override
    public void stopChargingWithLimit(String chargingProcessId, float totalPower) {
        log.info("Stopping charging process with limit: {}...", chargingProcessId);
        try {
            ESPChargingResult res = doStopCharging(chargingProcessId);
            if (res.getErrorCode() == null) {
                OcppChargingProcess process = proxyService.findOcppChargingProcess(chargingProcessId);

                Transaction transaction = transactionService.findTransaction(process.getTransaction().getTransactionPk()).
                        orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + process.getTransaction().getTransactionPk()));

                log.info("Successully stopped charging process with limit, notifying server: {}...", process.getOcppChargingProcessId());
                float consumption = consumptionHelper.getTotalPower(transaction);

                ESPChargingData data = ESPChargingData.builder().
                        start(process.getStartDate()).
                        end(process.getEndDate()).
                        totalPower(consumption == 0f ? totalPower : consumption).
                        startValue(consumptionHelper.getStartValue(transaction)).
                        stopValue(consumptionHelper.getStartValue(transaction)).build();
                ESPChargingStopRequest req = ESPChargingStopRequest.builder().
                        chargingData(data).
                        eventCode(REASON_LIMIT_EXCEEDED).
                        build();

                emobilityServiceProvider.stopChargingExternal(req);
            } else {
                log.error("Failed to stop charging with limit {}: {}", res.getErrorCode(), chargingProcessId);
            }
        } catch (Exception ex) {
            log.error("Failed to stop charging with limit: " + chargingProcessId, ex);
        }
    }


    @Override
    public void stopChargingWithPreparingTimeout(String chargingProcessId) {
        log.info("Stopping charging process with preparing limit: {}...", chargingProcessId);
        try {
            ESPChargingResult res = doStopCharging(chargingProcessId);
            if (res.getErrorCode() == null) {
                OcppChargingProcess process = proxyService.findOcppChargingProcess(chargingProcessId);

                Transaction transaction = transactionService.findTransaction(process.getTransaction().getTransactionPk()).
                        orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + process.getTransaction().getTransactionPk()));

                log.info("Successully stopped charging process wiht timeout, notifying server: {}...", process.getOcppChargingProcessId());
                ESPChargingData data = ESPChargingData.builder().
                        start(process.getStartDate()).
                        end(process.getEndDate()).
                        totalPower(consumptionHelper.getTotalPower(transaction)).
                        startValue(consumptionHelper.getStartValue(transaction)).
                        stopValue(consumptionHelper.getStartValue(transaction)).
                        build();
                ESPChargingStopRequest req = ESPChargingStopRequest.builder().
                        chargingData(data).
                        eventCode(REASON_VEHICLE_NOT_CONNECTED).
                        build();

                emobilityServiceProvider.stopChargingExternal(req);
            } else {
                log.error("Failed to stop charging with timeout {}: {}", res.getErrorCode(), chargingProcessId);
            }
        } catch (Exception ex) {
            log.error("Failed to stop charging with timeout: " + chargingProcessId, ex);
        }
    }

    @Override
    public void sendHeartBeatOfflineAlert(String chargeBoxId) {
        log.info("Sending heart beat offline alert, chargeBoxId: {}", chargeBoxId);
        try {
            emobilityServiceProvider.sendHeartBeatOfflineAlert(chargeBoxId);
        } catch (Exception ex) {
            log.error("Failed to end heart beat offline alert, chargeBoxId:{} ", chargeBoxId, ex);
        }
    }

    @Override
    public void notifyAboutRfidStart(ESPRfidChargingStartRequest startRequest) {
        log.info("Notify about RFID start, RFID = {}, charger = {}/{}",
                startRequest.getRfidTag(), startRequest.getChargeBoxId(), startRequest.getConnectorId());
        try {
            emobilityServiceProvider.notifyAboutRfidStart(startRequest);
        } catch (Exception ex) {
            log.error("Failed to notify about RFID start, RFID = {}, charger = {}/{}",
                    startRequest.getRfidTag(), startRequest.getChargeBoxId(), startRequest.getConnectorId(), ex);
        }
    }

    @Override
    public boolean checkRfidTag(String rfidTag, String chargeBoxId) {
        log.info("Check RFID tag validity on backend, RFID = {}", rfidTag);
        try {
            return emobilityServiceProvider.checkRfidTag(rfidTag, chargeBoxId);
        } catch (Exception ex) {
            log.error("Failed to check RFID tag validity on backend, RFID = {}", rfidTag, ex);
            return false;
        }
    }


}
