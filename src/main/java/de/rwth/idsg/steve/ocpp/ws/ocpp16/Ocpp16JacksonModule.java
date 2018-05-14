package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumMixin;
import de.rwth.idsg.steve.ocpp.ws.custom.EnumProcessor;

import java.util.Arrays;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 27.04.2015
 */
public class Ocpp16JacksonModule extends SimpleModule {

    public Ocpp16JacksonModule() {
        super("Ocpp16JacksonModule", new Version(0, 0, 1, null, "de.rwth.idsg", "steve"));
    }

    @Override
    public void setupModule(SetupContext sc) {
        super.setupModule(sc);

        EnumProcessor.apply(
                Arrays.asList(
                        ocpp.cs._2015._10.ObjectFactory.class.getPackage().getName(),
                        ocpp.cp._2015._10.ObjectFactory.class.getPackage().getName()
                ),
                clazz -> sc.setMixInAnnotations(clazz, EnumMixin.class)
        );
    }
}
