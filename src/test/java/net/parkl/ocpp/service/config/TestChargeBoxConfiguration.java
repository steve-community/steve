package net.parkl.ocpp.service.config;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestChargeBoxConfiguration implements ChargeBoxConfiguration {
    @Override
    public boolean isStartTimeoutEnabled(String chargeBoxId) {
        return false;
    }

    @Override
    public int getStartTimeoutSecs(String chargeBoxId) {
        return 0;
    }

    @Override
    public boolean isPreparingTimeoutEnabled(String chargeBoxId) {
        return false;
    }

    @Override
    public int getPreparingTimeoutSecs(String chargeBoxId) {
        return 0;
    }

    @Override
    public boolean isTransactionPartialEnabled(String chargeBoxId) {
        return chargeBoxId.equals("partialConsumptionUpdateChargeBox");
    }



    @Override
    public boolean isStartTimeoutEnabledForAny() {
        return false;
    }

    @Override
    public boolean isPreparingTimeoutEnabledForAny() {
        return false;
    }

    @Override
    public boolean isUsingIntegratedTag(String chargeBoxId) {
        return chargeBoxId.equals("integratedIdTagChargeBox");
    }

    @Override
    public boolean checkReservationId(String chargeBoxId) {
        return false;
    }

    @Override
    public List<String> getChargeBoxesForAlert() {
        return null;
    }

    @Override
    public boolean skipHeartBeatConfig(String chargeBoxId) {
        return true;
    }

    @Override
    public boolean isIdTagMax10Characters(String chargeBoxId) {
        return false;
    }

    @Override
    public float getWebSocketBufferMultiplier(String chargeBoxId, float defaultMultiplier) {
        return defaultMultiplier;
    }

    @Override
    public boolean ignoreConnectorAvailableUntilStopTransaction(String chargeBoxId) {
        return false;
    }
}
