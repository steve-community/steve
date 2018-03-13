package de.rwth.idsg.steve.web.dto.ocpp15;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 02.01.2015
 */
@Setter
@Getter
public class GetConfigurationParams extends MultipleChargePointSelect {

    private List<ConfigurationKeyEnum> confKeyList;

    public boolean isSetConfKeyList() {
        return confKeyList != null && !confKeyList.isEmpty();
    }
}
