/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.config;

import de.rwth.idsg.steve.SteveConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@Configuration
public class ValidationConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer valueConfigurer(SteveConfiguration config) {
        var configurer = new PropertySourcesPlaceholderConfigurer();

        var props = new Properties();
        var chargeBoxIdValidationRegex = config.getOcpp().getChargeBoxIdValidationRegex();
        if (chargeBoxIdValidationRegex != null) {
            props.put("charge-box-id.validation.regex", chargeBoxIdValidationRegex);
        }
        configurer.setProperties(props);

        return configurer;
    }
}
