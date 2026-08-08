/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams.ConfigurationKeyType;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import ocpp.cp._2015._10.ChangeConfigurationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChangeConfigurationTaskTest {

    @Test
    public void testCpoName() {
        ChangeConfigurationParams params = new ChangeConfigurationParams();
        params.setKeyType(ConfigurationKeyType.PREDEFINED);
        params.setConfKey(ConfigurationKeyEnum.CpoName.name());
        params.setValue("SteVe-CPO");

        ChangeConfigurationTask task = new ChangeConfigurationTask(params, null);
        ChangeConfigurationRequest request = task.getOcpp16Request();

        Assertions.assertEquals(ConfigurationKeyEnum.CpoName.name(), request.getKey());
        Assertions.assertEquals(params.getValue(), request.getValue());
    }

    /**
     * https://openchargealliance.org/wp-content/uploads/2025/12/CompliancyTestTool-TestCaseDocument.pdf
     */
    @Test
    public void testAuthorizationKey() {
        String plainText = "OCA_OCTT_admin_test";
        String hex = "4F43415F4F4354545F61646D696E5F74657374";

        ChangeConfigurationParams params = new ChangeConfigurationParams();
        params.setKeyType(ConfigurationKeyType.PREDEFINED);
        params.setConfKey(ConfigurationKeyEnum.AuthorizationKey.name());
        params.setValue(plainText);

        ChangeConfigurationTask task = new ChangeConfigurationTask(params, null);
        ChangeConfigurationRequest request = task.getOcpp16Request();

        Assertions.assertEquals(ConfigurationKeyEnum.AuthorizationKey.name(), request.getKey());
        Assertions.assertTrue(hex.equalsIgnoreCase(request.getValue()));
    }
}
