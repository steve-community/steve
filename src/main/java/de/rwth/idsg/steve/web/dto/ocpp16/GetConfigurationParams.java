/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author david
 */

@Getter
@Setter
public class GetConfigurationParams extends MultipleChargePointSelect
{
    private List<ConfigurationKeyEnum> confKeyList;
    
    public boolean isSetConfKeyList()
    {
        return confKeyList != null && !confKeyList.isEmpty();
    }
}
