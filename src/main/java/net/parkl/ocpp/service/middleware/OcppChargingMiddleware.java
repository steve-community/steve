package net.parkl.ocpp.service.middleware;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.RequestResult;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStopTransactionParams;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.*;
import net.parkl.ocpp.module.esp.EmobilityServiceProvider;
import net.parkl.ocpp.module.esp.model.*;
import net.parkl.ocpp.service.*;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.cs.ConnectorMeterValueData;
import net.parkl.ocpp.service.cs.ConnectorMeterValueService;
import net.parkl.ocpp.service.cs.EnergyImportLoader;
import net.parkl.ocpp.service.cs.TransactionService;
import net.parkl.ocpp.service.cs.status.ESPMeterValuesParser;
import net.parkl.ocpp.service.middleware.receiver.AsyncMessageReceiverLocator;
import ocpp.cs._2015._10.MeterValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Float.parseFloat;
import static java.util.Collections.singletonList;
import static net.parkl.ocpp.module.esp.ESPErrorCodes.*;
import static net.parkl.ocpp.service.OcppConstants.*;
import static ocpp.cp._2012._06.RemoteStartStopStatus.ACCEPTED;

@Service
@Slf4j
public class OcppChargingMiddleware extends AbstractOcppMiddleware {
    @Autowired
    private AdvancedChargeBoxConfiguration config;
    @Autowired
    private OcppConsumptionHelper consumptionHelper;
    @Autowired
    private ConnectorMeterValueService connectorMeterValueService;

    @Autowired
    private ChargingProcessService chargingProcessService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private EnergyImportLoader energyImportLoader;

    @Autowired
    private EmobilityServiceProvider emobilityServiceProvider;
    @Autowired
    private RemoteStartService remoteStartService;
    @Autowired
    private AsyncMessageReceiverLocator asyncMessageReceiverLocator;
    @Autowired
    private ESPMeterValuesParser meterValuesParser;

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
                chargingProcessService.findOpenChargingProcessWithoutTransaction(id.getChargeBoxId(), id.getConnectorId());
        if (existing != null) {
            log.error("Charging process open: {}", existing.getOcppChargingProcessId());
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_OCCUPIED).build();
        }

        String idTag = req.getRfidTag();
        if (idTag == null) {
            log.error("No ID tag not found");
            return ESPChargingStartResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
        }

        if (config.isIdTagMax10Characters(c.getChargeBoxId())) {
            log.info("Charge box (id = {}) uses ID tag with max length of 10, shortening ID tag...", c.getChargeBoxId());
            idTag = idTag.substring(0, 9);
        }

        if (config.isUsingIntegratedTag(c.getChargeBoxId())) {
            log.info("Using integrated  ID tag for charge box (id = {}) instead of user's unique tag", c.getChargeBoxId());
            idTag = getAvailableIntegrationIdTag(chargeBox);
        }

        remoteStartService.remoteStartRequested(c.getChargeBoxId(), id.getConnectorId(),
                idTag);

        RemoteStartTransactionParams p = new RemoteStartTransactionParams();
        p.setIdTag(idTag);
        p.setChargePointSelectList(singletonList(c));
        p.setConnectorId(id.getConnectorId());
        int taskId = sendRemoteStartTransaction(p, chargeBox.getOcppProtocol());


        RequestResult result = waitForResult(req.getChargeBoxId(), taskId);

        ESPChargingStartResult.ESPChargingStartResultBuilder resultBuilder = ESPChargingStartResult.builder();
        if (result != null) {
            if (result.getResponse() != null) {
                if (result.getResponse().equals(ACCEPTED.value())) {
                    log.info("Proxy transaction accepted: {}", id.getChargeBoxId());
                    OcppChargingProcess process = chargingProcessService.createChargingProcess(id.getChargeBoxId(),
                            id.getConnectorId(),
                            idTag,
                            req.getLicensePlate(),
                            req.getLimitKwh(),
                            req.getLimitMin());
                    log.info("Charging process created: {}", process.getOcppChargingProcessId());
                    resultBuilder
                            .externalChargingProcessId(String.valueOf(process.getOcppChargingProcessId()));


                } else {
                    log.info("Proxy transaction rejected: {}", id.getChargeBoxId());
                    resultBuilder.errorCode(ERROR_CODE_CHARGER_ERROR);
                }
            } else if (result.getErrorMessage() != null) {
                log.info("Proxy transaction error ({}): {}", result.getErrorMessage(), id.getChargeBoxId());
                resultBuilder.errorCode(ERROR_CODE_CHARGER_ERROR);

            } else {
                log.info("Proxy start transaction unknown error: {}", id.getChargeBoxId());
                resultBuilder.errorCode(ERROR_CODE_CHARGER_ERROR);

            }
        } else {
            log.info("Proxy start transaction timeout: {}", id.getChargeBoxId());
            resultBuilder.errorCode(ERROR_CODE_CHARGER_OFFLINE);

        }

        return resultBuilder.build();

    }

    private String getAvailableIntegrationIdTag(OcppChargeBox cb) {
        List<OcppChargingProcess> processes = chargingProcessService.getActiveProcessesByChargeBox(cb.getChargeBoxId());
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

    private ChargerIdentity toIdentity(String chargeBoxId, String chargerId) {
        ChargerIdentity id = new ChargerIdentity();
        id.setChargeBoxId(chargeBoxId);
        id.setConnectorId(Integer.parseInt(chargerId));
        return id;
    }

    public ESPChargingResult stopCharging(ESPChargingUserStopRequest req) {
        log.info("Stopping charging: {} (timeout={})...", req.getExternalChargeId(),req.getStopResponseTimeout());
        if (req.isStopOnlyWhenCableRemoved()) {
            log.info("Stop only when cable removed: {}", req.getExternalChargeId());
            chargingProcessService.updateStopOnlyWhenCableRemoved(req.getExternalChargeId(), true);
        }

        ESPChargingResult result = doStopCharging(req.getExternalChargeId());
        Boolean stoppedWithoutTransaction = result.getStoppedWithoutTransaction();
        if (!req.isStopOnlyWhenCableRemoved() &&
                (stoppedWithoutTransaction == null || !stoppedWithoutTransaction)) {
            ESPChargingData chargingData = asyncMessageReceiverLocator.get().receiveAsyncStopData(
                    req.getExternalChargeId(), req.getStopResponseTimeout());
            if (chargingData != null) {
                log.info("Consumption async data arrived for {}: startValue={}, stopValue={}",
                        req.getExternalChargeId(), chargingData.getStartValue(),
                        chargingData.getStopValue());
                result.setChargingData(chargingData);
            } else {
                log.error("Waiting for async consumption data expired: {}", req.getExternalChargeId());
            }

        }
        return result;

    }

    private ESPChargingResult doStopCharging(String ocppChargingProcessId) {
        OcppChargingProcess ocppChargingProcess = chargingProcessService.findOcppChargingProcess(ocppChargingProcessId);
        if (ocppChargingProcess == null) {
            log.info("Invalid charge id: {}", ocppChargingProcessId);
            return ESPChargingResult.builder().errorCode(ERROR_CODE_INVALID_EXTERNAL_CHARGE_ID).build();

        }

        if (ocppChargingProcess.getTransactionStart() != null) {
            Transaction transaction = transactionService
                    .findTransaction(ocppChargingProcess.getTransactionStart().getTransactionPk())
                    .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " +
                            ocppChargingProcess.getTransactionStart().getTransactionPk()));

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
        if (ocppChargingProcess.getTransactionStart() != null) {
            chargeBox = chargeBoxRepo
                    .findByChargeBoxId(ocppChargingProcess.getTransactionStart().getConnector().getChargeBoxId());
            if (chargeBox == null) {
                log.error("Invalid charge box id: {}",
                        ocppChargingProcess.getTransactionStart().getConnector().getChargeBoxId());
                return ESPChargingResult.builder().errorCode(ERROR_CODE_INVALID_CHARGER_ID).build();
            }

            chargePointSelect = getChargePoint(chargeBox.getChargeBoxId(), chargeBox.getOcppProtocol());
            if (chargePointSelect == null) {
                log.error("Invalid charge point id: {}",
                        ocppChargingProcess.getTransactionStart().getConnector().getChargeBoxId());
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            }
        }


        OcppChargingProcess chargingProcess = chargingProcessService.stopRequested(ocppChargingProcess.getOcppChargingProcessId());


        if (chargingProcess.getTransactionStart() != null) {
            log.info("Stopping charging transaction: {}...", chargingProcess.getTransactionStart().getTransactionPk());

            RemoteStopTransactionParams params = new RemoteStopTransactionParams();
            params.setChargePointSelectList(singletonList(chargePointSelect));
            params.setTransactionId(chargingProcess.getTransactionStart().getTransactionPk());
            int taskId = sendRemoteStopTransaction(params, chargeBox != null ? chargeBox.getOcppProtocol() : null);

            RequestResult result = waitForResult(chargingProcess.getTransactionStart().getConnector().getChargeBoxId(), taskId);

            return processRemoteStopResult(ocppChargingProcessId, chargingProcess.getTransactionStart().getTransactionPk(), result);


        } else {
            //Charging process started without transaction
            log.info("Stopping charging without transaction: {}...", chargingProcess.getOcppChargingProcessId());
            OcppChargingProcess process = chargingProcessService.stopChargingProcess(chargingProcess.getOcppChargingProcessId());

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
                    chargingProcessService.stopChargingProcess(externalChargeId);
                    return ESPChargingResult.builder().stoppedWithoutTransaction(false).build();
                } else {
                    log.info("Proxy transaction stop rejected: {}", transactionId);
                    chargingProcessService.stopRequestCancelled(externalChargeId);
                    return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
                }
            } else if (result.getErrorMessage() != null) {
                log.info("Proxy transaction error ({}): {}", result.getErrorMessage(), transactionId);
                chargingProcessService.stopRequestCancelled(externalChargeId);
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            } else {
                log.info("Proxy stop transaction unknown error: {}", transactionId);
                chargingProcessService.stopRequestCancelled(externalChargeId);
                return ESPChargingResult.builder().errorCode(ERROR_CODE_CHARGER_ERROR).build();
            }
        } else {
            log.error("No response arrived from charger for stop transaction: {}", transactionId);
            chargingProcessService.stopRequestCancelled(externalChargeId);
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

    public ESPChargingStatusResult getStatus(String externalChargeId) {
        log.info("Status request: {}...", externalChargeId);

        OcppChargingProcess ocppChargingProcess = chargingProcessService.findOcppChargingProcess(externalChargeId);
        if (ocppChargingProcess == null) {
            log.info("Invalid charge id: {}", externalChargeId);
            return ESPChargingStatusResult.builder().errorCode(ERROR_CODE_INVALID_EXTERNAL_CHARGE_ID).build();

        }

       Transaction transaction = null;
        if (ocppChargingProcess.getTransactionStart() != null) {
            transaction = transactionService
                    .findTransaction(ocppChargingProcess.getTransactionStart().getTransactionPk())
                    .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " +
                            ocppChargingProcess.getTransactionStart().getTransactionPk()));

            if (transaction.getStopTimestamp() != null) {
                log.info("Charging already stopped: {}", ocppChargingProcess.getOcppChargingProcessId());
                return ESPChargingStatusResult.builder().errorCode(ERROR_CODE_CHARGING_ALREADY_STOPPED).build();
            }
        }

        ESPChargingStatusResult ret = ESPChargingStatusResult.builder()
                .status(ESPChargingStatus.builder().build())
                .chargingData(ESPChargingData.builder().build())
                .build();

        ret.getChargingData().setStart(ocppChargingProcess.getStartDate());
        if (transaction != null) {
            PowerValue pw = getPowerValue(ocppChargingProcess.getTransactionStart().getTransactionPk());
            ConnectorMeterValueData activePower =
                    connectorMeterValueService.getLastConnectorMeterValueByTransactionAndMeasurand(ocppChargingProcess.getTransactionStart(),
                            MEASURAND_POWER_ACTIVE_IMPORT);
            if (activePower != null) {
                ret.getStatus().setThroughputPower(OcppConsumptionHelper.getKwValue(parseFloat(activePower.getValue()),
                        activePower.getUnit()));
            }

            ConnectorMeterValueData soc =
                    connectorMeterValueService.getLastConnectorMeterValueByTransactionAndMeasurand(ocppChargingProcess.getTransactionStart(),
                            MEASURAND_SOC);
            if (soc != null && StringUtils.hasLength(soc.getValue())) {
                ret.getStatus().setSoc(parseFloat(soc.getValue()));
            }

            ret.getChargingData().setTotalPower(OcppConsumptionHelper.getKwhValue(pw.getValue(), pw.getUnit()));
            ret.getChargingData().setStartValue(consumptionHelper.getStartValue(transaction));
            ret.getChargingData().setStopValue(consumptionHelper.getStopValue(transaction));
        }
        return ret;
    }

    public PowerValue getPowerValue(int transactionPk) {
        AbstractTransactionEnergyImport energyImport = energyImportLoader.loadEnergyImport(transactionPk);
        float diff = 0;
        String diffUnit = null;

        if (energyImport != null) {
            diffUnit = energyImport.getUnit();
            if (diffUnit == null) {
                diffUnit = UNIT_WH;
            }
            diff = energyImport.getEndValue()
                    - energyImport.getStartValue();
        }

        return new PowerValue(diff, diffUnit);

    }


    public void stopChargingExternal(OcppChargingProcess process, String reason) {
        if (process == null || reason == null) {
            throw new IllegalArgumentException("OcppChargingProcess or reason was null");
        }
        log.info("Stopping charging process from OCPP proxy: {}...", process.getOcppChargingProcessId());

        ESPChargingData.ESPChargingDataBuilder espChargingDataBuilder = ESPChargingData.builder().
                start(process.getStartDate()).
                end(process.getEndDate());

        if (!reason.equals(REASON_VEHICLE_NOT_CONNECTED)) {
            int transactionPk = process.getTransactionStart().getTransactionPk();
            Transaction transaction = transactionService.findTransaction(transactionPk)
                    .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + transactionPk));

            espChargingDataBuilder
                    .totalPower(consumptionHelper.getTotalPower(transaction))
                    .startValue(consumptionHelper.getStartValue(transaction))
                    .stopValue(consumptionHelper.getStopValue(transaction));
        }


        ESPChargingStopRequest req = ESPChargingStopRequest.builder().
                externalChargeId(process.getOcppChargingProcessId()).
                eventCode(reason).
                chargingData(espChargingDataBuilder.build()).build();

        emobilityServiceProvider.stopChargingExternal(req);

        if (stopListener != null) {
            stopListener.chargingStopped(process, req.getChargingData(), reason);
        }
    }

    public void updateConsumption(OcppChargingProcess process, String startValue, String stopValue) {
        if (process == null) {
            throw new IllegalArgumentException("OcppChargingProcess was null");
        }
        log.info("Updating charging process consumption from OCPP proxy: {}...", process.getOcppChargingProcessId());

        int transactionPk = process.getTransactionStart().getTransactionPk();

        Transaction transaction =
                transactionService.findTransaction(transactionPk)
                        .orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + transactionPk));

        log.info("transaction in consumption update: {}", transaction);

        float calculatedTotalPower = consumptionHelper.getTotalPower(startValue, stopValue);
        Float calculatedStartValue = consumptionHelper.getStartValue(transaction);
        Float calculatedStopValue = consumptionHelper.getStopValue(transaction);

        if (process.isStopOnlyWhenCableRemoved()) {
            ESPChargingConsumptionRequest req = ESPChargingConsumptionRequest.builder().
                    externalChargeId(process.getOcppChargingProcessId()).
                    start(process.getStartDate()).
                    end(process.getEndDate()).
                    totalPower(calculatedTotalPower).
                    startValue(calculatedStartValue).
                    stopValue(calculatedStopValue).
                    build();

            emobilityServiceProvider.updateChargingConsumptionExternal(req);

        } else {
            ESPChargingData req = ESPChargingData.builder().
                    start(process.getStartDate()).
                    end(process.getEndDate()).
                    totalPower(calculatedTotalPower).
                    startValue(calculatedStartValue).
                    stopValue(calculatedStopValue).
                    build();

            asyncMessageReceiverLocator.get().updateChargingConsumption(
                    process.getOcppChargingProcessId(), req);

            if (consumptionListener != null) {
                consumptionListener.consumptionUpdated(process.getOcppChargingProcessId(), req);
            }
        }


    }

    public boolean isConnectorCharging(String chargeBoxId, int connectorId) {
        return chargingProcessService.findOpenChargingProcess(chargeBoxId, connectorId) != null;
    }

    public void registerConsumptionListener(OcppConsumptionListener l) {
        this.consumptionListener = l;
    }

    public void registerStopListener(OcppStopListener l) {
        this.stopListener = l;
    }

    public void stopChargingWithLimit(String chargingProcessId, float totalPower) {
        log.info("Stopping charging process with limit: {}...", chargingProcessId);
        try {
            ESPChargingResult res = doStopCharging(chargingProcessId);
            if (res.getErrorCode() == null) {
                OcppChargingProcess process = chargingProcessService.findOcppChargingProcess(chargingProcessId);

                Transaction transaction = transactionService.findTransaction(process.getTransactionStart().getTransactionPk()).
                        orElseThrow(() -> new IllegalStateException("Invalid transaction id: " + process.getTransactionStart().getTransactionPk()));

                log.info("Successully stopped charging process with limit, notifying server: {}...", process.getOcppChargingProcessId());
                float consumption = consumptionHelper.getTotalPower(transaction);

                ESPChargingData data = ESPChargingData.builder()
                        .start(process.getStartDate())
                        .end(process.getEndDate())
                        .totalPower(consumption == 0f ? totalPower : consumption)
                        .startValue(consumptionHelper.getStartValue(transaction))
                        .stopValue(consumptionHelper.getStopValue(transaction))
                        .build();
                ESPChargingStopRequest req = ESPChargingStopRequest.builder()
                        .chargingData(data)
                        .externalChargeId(chargingProcessId)
                        .eventCode(REASON_LIMIT_EXCEEDED).
                        build();

                emobilityServiceProvider.stopChargingExternal(req);
            } else {
                log.error("Failed to stop charging with limit {}: {}", res.getErrorCode(), chargingProcessId);
            }
        } catch (Exception ex) {
            log.error("Failed to stop charging with limit: " + chargingProcessId, ex);
        }
    }

    public void stopChargingWithPreparingTimeout(String chargingProcessId) {
        log.info("Stopping charging process with preparing limit: {}...", chargingProcessId);
        try {
            ESPChargingResult res = doStopCharging(chargingProcessId);
            if (res.getErrorCode() == null) {
                OcppChargingProcess process = chargingProcessService.findOcppChargingProcess(chargingProcessId);

                log.info("Successfully stopped charging process with timeout, notifying server: {}...",
                        process.getOcppChargingProcessId());
                ESPChargingData data = ESPChargingData.builder()
                        .start(process.getStartDate())
                        .end(process.getEndDate())
                        .build();
                ESPChargingStopRequest req = ESPChargingStopRequest.builder()
                        .chargingData(data)
                        .externalChargeId(chargingProcessId)
                        .eventCode(REASON_VEHICLE_NOT_CONNECTED)
                        .build();

                emobilityServiceProvider.stopChargingExternal(req);
            } else {
                log.error("Failed to stop charging with timeout {}: {}", res.getErrorCode(), chargingProcessId);
            }
        } catch (Exception ex) {
            log.error("Failed to stop charging with timeout: " + chargingProcessId, ex);
        }
    }

    public boolean checkRfidTag(String rfidTag, String chargeBoxId) {
        log.info("Check RFID tag validity on backend, RFID = {}", rfidTag);
        try {
            return emobilityServiceProvider.checkRfidTag(rfidTag, chargeBoxId);
        } catch (Exception ex) {
            log.error("Failed to check RFID tag validity on backend, RFID = {}", rfidTag, ex);
            return false;
        }
    }


    public void updateMeterValues(TransactionStart transactionStart, List<MeterValue> meterValues) {
        OcppChargingProcess process = chargingProcessService.findByTransactionId(transactionStart.getTransactionPk());
        if (process == null) {
            log.warn("No charging process found for transaction: {}", transactionStart.getTransactionPk());
            return;
        }

        ESPMeterValues values = meterValuesParser.parseMeterValues(transactionStart, meterValues);
        log.info("Updating meter values for {}: {}", process.getOcppChargingProcessId(),
                values);
        emobilityServiceProvider.updateChargingMeterValues(process.getOcppChargingProcessId(), values);

    }
}
