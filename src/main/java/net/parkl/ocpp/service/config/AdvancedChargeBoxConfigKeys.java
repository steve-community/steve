package net.parkl.ocpp.service.config;

import net.parkl.ocpp.entities.OcppChargingProcess;

public class AdvancedChargeBoxConfigKeys {
    /**
     * Start timeout enabled for availability change
     */
    public static final String KEY_START_TIMEOUT_ENABLED = "start.timeout.enabled";

    /**
     * Start timeout in seconds
     */
    public static final String KEY_START_TIMEOUT_SECS = "start.timeout.secs";

    /**
     * Preparing timeout enabled
     */
    public static final String KEY_PREPARING_TIMEOUT_ENABLED = "preparing.timeout.enabled";

    /**
     * Preparing timeout in seconds
     */
    public static final String KEY_PREPARING_TIMEOUT_SECS = "preparing.timeout.secs";

    /**
     * Transaction stop value is partial
     */
    public static final String KEY_TRANSACTION_PARTIAL_ENABLED = "transaction.partial.enabled";
    /**
     * Waiting for {@link OcppChargingProcess} at RemoteStarTransaction
     */
    public static final String KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED = "waiting.for.charging.process.enabled";

    /**
     * Using id tags from {@link IntegratedIdTagProvider}
     */
    public static final String KEY_USING_INTEGRATED_IDTAG = "integrated.idtag";

    /**
     * Using id tag first 10 character
     */
    public static final String KEY_IDTAG_MAX10 = "idtag.max10";

    /**
     * Check reservation on StartTransaction
     */
    public static final String KEY_CHECK_RESERVATION = "check.reservation";


}
