package de.rwth.idsg.steve.web.dto.ocpp15;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@Setter
public class GetConfigurationParams extends MultipleChargePointSelect {

    private List<ConfigurationKeyEnum> confKeyList;

    public List<ConfigurationKeyEnum> getConfKeyList() {
        if (confKeyList == null) {
            confKeyList = new ArrayList<>();
        }
        return confKeyList;
    }
}
