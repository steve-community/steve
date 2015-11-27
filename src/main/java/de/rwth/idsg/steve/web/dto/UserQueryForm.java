package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 26.11.2015
 */
@Getter
@Setter
public class UserQueryForm {

    private Integer userPk;

    // Free text input
    private String ocppIdTag;
    private String name;
    private String email;

    public boolean isSetUserPk() {
        return userPk != null;
    }

    public boolean isSetOcppIdTag() {
        return ocppIdTag != null;
    }

    public boolean isSetName() {
        return name != null;
    }

    public boolean isSetEmail() {
        return email != null;
    }
}
