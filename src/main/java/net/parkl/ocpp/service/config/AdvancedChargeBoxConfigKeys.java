package net.parkl.ocpp.service.config;

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
     * Waiting for {@link net.parkl.ocpp.entities.OcppChargingProcess} at RemoteStarTransaction
     */
    public static final String KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED = "waiting.for.charging.process.enabled";

    /**
     *
     * Eredeti parkl tagek hasznalata ahol nem lehet konfigolni a kulcsokat. pl regi mennekes
     */
    public static final String KEY_USING_INTEGRATED_IDTAG = "integrated.idtag";

    /**
     * Roviditett id tag hasznalata, pl DBT toltok
     */
    public static final String KEY_IDTAG_MAX10 = "idtag.max10";

    /**
     * Olyan toltokhoz amik kuldik a reservation id-t meg 0-kent es ne dobjunk ra exceptiont
     */
    public static final String KEY_CHECK_RESERVATION = "check.reservation";


}
