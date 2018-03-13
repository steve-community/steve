package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.MeterValue15Mixin;
import ocpp.cp._2012._06.AvailabilityStatus;
import ocpp.cp._2012._06.AvailabilityType;
import ocpp.cp._2012._06.CancelReservationStatus;
import ocpp.cp._2012._06.ClearCacheStatus;
import ocpp.cp._2012._06.ConfigurationStatus;
import ocpp.cp._2012._06.RemoteStartStopStatus;
import ocpp.cp._2012._06.ReservationStatus;
import ocpp.cp._2012._06.ResetStatus;
import ocpp.cp._2012._06.ResetType;
import ocpp.cp._2012._06.UnlockStatus;
import ocpp.cp._2012._06.UpdateStatus;
import ocpp.cp._2012._06.UpdateType;
import ocpp.cs._2012._06.ChargePointErrorCode;
import ocpp.cs._2012._06.ChargePointStatus;
import ocpp.cs._2012._06.DiagnosticsStatus;
import ocpp.cs._2012._06.FirmwareStatus;
import ocpp.cs._2012._06.Location;
import ocpp.cs._2012._06.Measurand;
import ocpp.cs._2012._06.MeterValuesRequest;
import ocpp.cs._2012._06.ReadingContext;
import ocpp.cs._2012._06.RegistrationStatus;
import ocpp.cs._2012._06.UnitOfMeasure;
import ocpp.cs._2012._06.ValueFormat;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public class Ocpp15JacksonModule extends SimpleModule {

    public Ocpp15JacksonModule() {
        super("Ocpp15JacksonModule", new Version(0, 0 , 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(Module.SetupContext sc) {
        sc.setMixInAnnotations(MeterValuesRequest.class, MeterValue15Mixin.class);

        // Enums from CP
        sc.setMixInAnnotations(ocpp.cp._2012._06.AuthorizationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(AvailabilityStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(AvailabilityType.class, EnumMixin.class);
        sc.setMixInAnnotations(CancelReservationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ClearCacheStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ConfigurationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ocpp.cp._2012._06.DataTransferStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(RemoteStartStopStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ReservationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetType.class, EnumMixin.class);
        sc.setMixInAnnotations(UnlockStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UpdateStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UpdateType.class, EnumMixin.class);

        // Enums from CS
        sc.setMixInAnnotations(ocpp.cs._2012._06.AuthorizationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointErrorCode.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ocpp.cs._2012._06.DataTransferStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(DiagnosticsStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(FirmwareStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(Location.class, EnumMixin.class);
        sc.setMixInAnnotations(Measurand.class, EnumMixin.class);
        sc.setMixInAnnotations(ReadingContext.class, EnumMixin.class);
        sc.setMixInAnnotations(RegistrationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UnitOfMeasure.class, EnumMixin.class);
        sc.setMixInAnnotations(ValueFormat.class, EnumMixin.class);
    }
}
