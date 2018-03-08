package de.rwth.idsg.steve.web.dto.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 08.03.2018
 */
@Getter
@Setter
public class ChangeConfigurationParams extends AbstractChangeConfigurationParams {

    private String confKey;

    @Override
    protected String getPredefinedKey() {
        return confKey;
    }
}
