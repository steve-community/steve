package de.rwth.idsg.steve.web.dto.ocpp12;

import de.rwth.idsg.steve.web.dto.common.AbstractChangeConfigurationParams;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 30.12.2014
 */
@Getter
@Setter
public class ChangeConfigurationParams extends AbstractChangeConfigurationParams {

    private ConfigurationKeyEnum confKey;

    @Override
    protected String getPredefinedKey() {
        return confKey.value();
    }
}
