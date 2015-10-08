package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import ocpp.cp._2010._08.AvailabilityStatus;
import ocpp.cp._2010._08.AvailabilityType;
import ocpp.cp._2010._08.ClearCacheStatus;
import ocpp.cp._2010._08.ConfigurationStatus;
import ocpp.cp._2010._08.RemoteStartStopStatus;
import ocpp.cp._2010._08.ResetStatus;
import ocpp.cp._2010._08.ResetType;
import ocpp.cp._2010._08.UnlockStatus;
import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.ChargePointErrorCode;
import ocpp.cs._2010._08.ChargePointStatus;
import ocpp.cs._2010._08.DiagnosticsStatus;
import ocpp.cs._2010._08.FirmwareStatus;
import ocpp.cs._2010._08.RegistrationStatus;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public class Ocpp12JacksonModule extends SimpleModule {

    public Ocpp12JacksonModule() {
        super("Ocpp12JacksonModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(SetupContext sc) {
        // Enums from CP
        sc.setMixInAnnotations(AvailabilityStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(AvailabilityType.class, EnumMixin.class);
        sc.setMixInAnnotations(ClearCacheStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ConfigurationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(RemoteStartStopStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetType.class, EnumMixin.class);
        sc.setMixInAnnotations(UnlockStatus.class, EnumMixin.class);

        // Enums from CS
        sc.setMixInAnnotations(AuthorizationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointErrorCode.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(DiagnosticsStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(FirmwareStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(RegistrationStatus.class, EnumMixin.class);
    }
}
