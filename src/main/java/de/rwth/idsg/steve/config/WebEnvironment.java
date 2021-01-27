package de.rwth.idsg.steve.config;

public class WebEnvironment {
    public static String getContextRoot() {
        String env = System.getenv("OCPP_CONTEXT_ROOT");
        if (env==null) {
            return "";
        }
        return env;
    }
}
