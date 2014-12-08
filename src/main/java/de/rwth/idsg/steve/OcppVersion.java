package de.rwth.idsg.steve;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 01.12.2014
 */
public enum OcppVersion {
    V_12("1.2"),
    V_15("1.5");

    private String value;

    private OcppVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
