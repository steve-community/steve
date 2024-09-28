
package ocpp.cs._2010._08;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FirmwareStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="FirmwareStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Downloaded"/&gt;
 *     &lt;enumeration value="DownloadFailed"/&gt;
 *     &lt;enumeration value="InstallationFailed"/&gt;
 *     &lt;enumeration value="Installed"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FirmwareStatus")
@XmlEnum
public enum FirmwareStatus {

    @XmlEnumValue("Downloaded")
    DOWNLOADED("Downloaded"),
    @XmlEnumValue("DownloadFailed")
    DOWNLOAD_FAILED("DownloadFailed"),
    @XmlEnumValue("InstallationFailed")
    INSTALLATION_FAILED("InstallationFailed"),
    @XmlEnumValue("Installed")
    INSTALLED("Installed");
    private final String value;

    FirmwareStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FirmwareStatus fromValue(String v) {
        for (FirmwareStatus c: FirmwareStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
