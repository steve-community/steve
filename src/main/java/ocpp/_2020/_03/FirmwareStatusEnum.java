
package ocpp._2020._03;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * This contains the progress status of the firmware installation.
 * 
 * 
 */
@Generated("jsonschema2pojo")
public enum FirmwareStatusEnum {

    DOWNLOADED("Downloaded"),
    DOWNLOAD_FAILED("DownloadFailed"),
    DOWNLOADING("Downloading"),
    DOWNLOAD_SCHEDULED("DownloadScheduled"),
    DOWNLOAD_PAUSED("DownloadPaused"),
    IDLE("Idle"),
    INSTALLATION_FAILED("InstallationFailed"),
    INSTALLING("Installing"),
    INSTALLED("Installed"),
    INSTALL_REBOOTING("InstallRebooting"),
    INSTALL_SCHEDULED("InstallScheduled"),
    INSTALL_VERIFICATION_FAILED("InstallVerificationFailed"),
    INVALID_SIGNATURE("InvalidSignature"),
    SIGNATURE_VERIFIED("SignatureVerified");
    private final String value;
    private final static Map<String, FirmwareStatusEnum> CONSTANTS = new HashMap<String, FirmwareStatusEnum>();

    static {
        for (FirmwareStatusEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    FirmwareStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static FirmwareStatusEnum fromValue(String value) {
        FirmwareStatusEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
