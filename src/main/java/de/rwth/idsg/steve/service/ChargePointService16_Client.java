package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ChargePointService12_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService15_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService16_Invoker;
import de.rwth.idsg.steve.ocpp.ChargePointService16_InvokerImpl;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.service.dto.EnhancedSetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import lombok.extern.slf4j.Slf4j;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
@Service
@Qualifier("ChargePointService16_Client")
public class ChargePointService16_Client extends ChargePointService15_Client {

    @Autowired private ChargePointService16_InvokerImpl invoker16;
    @Autowired private ChargingProfileRepository chargingProfileRepository;

    @Override
    protected OcppVersion getVersion() {
        return OcppVersion.V_16;
    }

    @Override
    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker16;
    }

    @Override
    protected ChargePointService15_Invoker getOcpp15Invoker() {
        return invoker16;
    }

    protected ChargePointService16_Invoker getOcpp16Invoker() {
        return invoker16;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    public int triggerMessage(TriggerMessageParams params) {
        TriggerMessageTask task = new TriggerMessageTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().triggerMessage(c, task));

        return taskStore.add(task);
    }

    public int setChargingProfile(SetChargingProfileParams params) {
        ChargingProfile.Details details = chargingProfileRepository.getDetails(params.getChargingProfilePk());

        checkAdditionalConstraints(params, details);

        EnhancedSetChargingProfileParams enhancedParams = new EnhancedSetChargingProfileParams(params, details);
        SetChargingProfileTask task = new SetChargingProfileTask(getVersion(), enhancedParams, chargingProfileRepository);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().setChargingProfile(c, task));

        return taskStore.add(task);
    }

    public int clearChargingProfile(ClearChargingProfileParams params) {
        ClearChargingProfileTask task = new ClearChargingProfileTask(getVersion(), params, chargingProfileRepository);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().clearChargingProfile(c, task));

        return taskStore.add(task);
    }

    public int getCompositeSchedule(GetCompositeScheduleParams params) {
        GetCompositeScheduleTask task = new GetCompositeScheduleTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().getCompositeSchedule(c, task));

        return taskStore.add(task);
    }

    /**
     * Do some additional checks defined by OCPP spec, which cannot be captured with javax.validation
     */
    private static void checkAdditionalConstraints(SetChargingProfileParams params, ChargingProfile.Details details) {
        ChargingProfilePurposeType purpose = ChargingProfilePurposeType.fromValue(details.getProfile().getChargingProfilePurpose());

        if (ChargingProfilePurposeType.CHARGE_POINT_MAX_PROFILE == purpose
                && params.getConnectorId() != null
                && params.getConnectorId() != 0) {
            throw new SteveException("ChargePointMaxProfile can only be set at Charge Point ConnectorId 0");
        }

        if (ChargingProfilePurposeType.TX_PROFILE == purpose
                && params.getConnectorId() != null
                && params.getConnectorId() < 1) {
            throw new SteveException("TxProfile should only be set at Charge Point ConnectorId > 0");
        }

    }
}
