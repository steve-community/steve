package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumProcessor;
import de.rwth.idsg.steve.ocpp.ws.custom.MeterValue15Mixin;
import ocpp.cs._2012._06.MeterValuesRequest;

import java.util.Arrays;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public class Ocpp15JacksonModule extends SimpleModule {

    public Ocpp15JacksonModule() {
        super("Ocpp15JacksonModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(Module.SetupContext sc) {
        sc.setMixInAnnotations(MeterValuesRequest.class, MeterValue15Mixin.class);

        EnumProcessor.apply(
                Arrays.asList("ocpp.cp._2012._06", "ocpp.cs._2012._06"),
                clazz -> sc.setMixInAnnotations(clazz, EnumMixin.class)
        );
    }
}
