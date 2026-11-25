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
package de.rwth.idsg.steve.web.api;


import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.web.validation.SecurityProfileValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ValidationException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.joda.JodaModule;
import tools.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;

public class AbstractControllerTest {

    JsonMapper objectMapper;

    AbstractControllerTest() {
        // Match the production mapper: the JAXB module maps wire values like
        // "Accepted" onto the generated OCPP enum constants.
        this.objectMapper = JsonMapper.builder()
            .addModule(new JodaModule())
            .addModule(new JakartaXmlBindAnnotationModule())
            .build();
    }

    /**
     * Builds a validator that can instantiate constraint validators which are
     * Spring-constructed in production. Standalone MockMvc has no bean context,
     * so e.g. SecurityProfileValidator needs its ChargePointService passed in.
     */
    protected static LocalValidatorFactoryBean validatorWithChargePointService(ChargePointService service) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == SecurityProfileValidator.class) {
                    return key.cast(new SecurityProfileValidator(service));
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new ValidationException("Cannot instantiate " + key, e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
                // no-op
            }
        });
        factory.afterPropertiesSet();
        return factory;
    }
}
