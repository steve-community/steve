package de.rwth.idsg.steve;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 05.11.2015
 */
public enum ApplicationProfile {
    DEV,
    TEST,
    PROD;

    public static ApplicationProfile fromName(String v) {
        for (ApplicationProfile ap : ApplicationProfile.values()) {
            if (ap.name().equalsIgnoreCase(v)) {
                return ap;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public boolean isProd() {
        return this == PROD;
    }
}
