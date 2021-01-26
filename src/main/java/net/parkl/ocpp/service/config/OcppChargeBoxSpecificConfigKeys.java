package net.parkl.ocpp.service.config;

public class OcppChargeBoxSpecificConfigKeys {
    /**
     * Töltés indítás utáni timeout be van-e kapcsolva a töltőfejen?  (pl. Alfen kábeles töltő)
     */
    public static final String KEY_START_TIMEOUT_ENABLED="start.timeout.enabled";
    /**
     * Töltés indítás utáni timeout másodpercben
     */
    public static final String KEY_START_TIMEOUT_SECS="start.timeout.secs";

    /**
     * Töltés indítás utáni preparing timeout be van-e kapcsolva. Olyan charge boxokra érdemes bekapcsolni, ahol 1 perc után preparing állapotba marad a töltő
     * pl Ecotap töltő
     */
    public static final String KEY_PREPARING_TIMEOUT_ENABLED="preparing.timeout.enabled";

    /**
     * Töltés indítás preparing utáni timeout másodpercben
     */
    public static final String KEY_PREPARING_TIMEOUT_SECS="preparing.timeout.secs";

    /**
     * Igaz, ha a charge box esetében a transaction stop value partial értéket tartalmaz
     * (a mérőóra nem küld abszolút állást, pl. Schneider)
     */
    public static final String KEY_TRANSACTION_PARTIAL_ENABLED = "transaction.partial.enabled";
    /**
     * Töltés indításkor megvárja-e a tranzakció létrehozáskor az {@link net.parkl.ocpp.entities.OcppChargingProcess} rekord létrejöttét.<br>
     * Olyan töltőknél érdemes bekapcsolni, amelyek túl gyorsan reagálnak StartTransaction üzenettel a RemoteStartTransactionre (pl. Elinta 20ms)
     */
    public static final String KEY_WAITING_FOR_CHARGING_PROCESS_ENABLED = "waiting.for.charging.process.enabled";

    /**
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
