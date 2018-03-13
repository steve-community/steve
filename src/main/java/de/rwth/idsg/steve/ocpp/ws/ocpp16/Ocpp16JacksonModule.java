/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.MeterValue16Mixin;
import ocpp.cp._2015._10.*;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.DiagnosticsStatus;
import ocpp.cs._2015._10.FirmwareStatus;
import ocpp.cs._2015._10.Location;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.ReadingContext;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

/**
 *
 * @author david
 */
public class Ocpp16JacksonModule extends SimpleModule {

    public Ocpp16JacksonModule() {
        super("Ocpp16JacksonModule", new Version(0, 0 , 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(Module.SetupContext sc) {
        sc.setMixInAnnotations(MeterValuesRequest.class, MeterValue16Mixin.class);

        // Enums from CP
        sc.setMixInAnnotations(ocpp.cp._2015._10.AuthorizationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(AvailabilityStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(AvailabilityType.class, EnumMixin.class);
        sc.setMixInAnnotations(CancelReservationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ClearCacheStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ConfigurationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ocpp.cp._2015._10.DataTransferStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(RemoteStartStopStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ReservationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ResetType.class, EnumMixin.class);
        sc.setMixInAnnotations(UnlockStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UpdateStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UpdateType.class, EnumMixin.class);

        // Enums from CS
        sc.setMixInAnnotations(ocpp.cs._2015._10.AuthorizationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointErrorCode.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargePointStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(ocpp.cs._2015._10.DataTransferStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(DiagnosticsStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(FirmwareStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(Location.class, EnumMixin.class);
        sc.setMixInAnnotations(Measurand.class, EnumMixin.class);
        sc.setMixInAnnotations(ReadingContext.class, EnumMixin.class);
        sc.setMixInAnnotations(RegistrationStatus.class, EnumMixin.class);
        sc.setMixInAnnotations(UnitOfMeasure.class, EnumMixin.class);
        sc.setMixInAnnotations(ValueFormat.class, EnumMixin.class);
        sc.setMixInAnnotations(MessageTrigger.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargingRateUnitType.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargingProfilePurposeType.class, EnumMixin.class);
        sc.setMixInAnnotations(ChargingProfileKindType.class, EnumMixin.class);
        sc.setMixInAnnotations(RecurrencyKindType.class, EnumMixin.class);
    }
}