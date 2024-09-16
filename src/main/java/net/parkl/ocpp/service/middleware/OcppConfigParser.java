package net.parkl.ocpp.service.middleware;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.module.esp.model.ESPChargeBoxConfiguration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OcppConfigParser {
    public static List<ESPChargeBoxConfiguration> parseConfList(String response) {
        log.info("Parsing configuration: {}...", response);
        List<ESPChargeBoxConfiguration> ret = new ArrayList<>();
        String[] split = response.split("<br>");
        for (String line : split) {
            log.info("Parsing config line: {}...", line);
            if (line.startsWith("<b>Unknown keys")) {
                log.info("Unknown keys reached, exiting...");
                break;
            }
            if (!StringUtils.isEmpty(line.trim()) && !line.startsWith("<b>Known keys")) {
                boolean readOnly = false;
                if (line.endsWith(" (read-only)")) {
                    line = line.replace(" (read-only)", "");
                    readOnly = true;
                }
                int separatorIdx = line.indexOf(":");
                if (separatorIdx == -1) {
                    log.warn("Invalid configuration line: {}", line);
                    continue;
                }
                String key = line.substring(0, separatorIdx).trim();
                String value = line.substring(separatorIdx + 1).trim();

                ESPChargeBoxConfiguration c = ESPChargeBoxConfiguration.builder().
                        key(key).value(value).readOnly(readOnly).build();
                log.info("Configuration parsed: {}={} (read-only={})", c.getKey(), c.getValue(), c.isReadOnly());
                ret.add(c);
            }
        }
        return ret;
    }
}
