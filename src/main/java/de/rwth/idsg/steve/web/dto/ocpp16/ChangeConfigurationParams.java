/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwth.idsg.steve.web.dto.ocpp16;

import de.rwth.idsg.steve.web.dto.common.AbstractChangeConfigurationParams;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author david
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
