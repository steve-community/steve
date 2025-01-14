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
import de.rwth.idsg.steve.SteveProdCondition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * https://stackoverflow.com/a/65557714
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.09.2022
 */
@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
@Import({SpringDocConfiguration.class,
    SpringDocWebMvcConfiguration.class,
    SwaggerConfig.class,
    SwaggerUiConfigProperties.class,
    SwaggerUiOAuthProperties.class,
    JacksonAutoConfiguration.class})
@Conditional(SteveProdCondition.class)
public class ApiDocsConfiguration {

    static {
        // Set the path with prefix /manager to protect the documentation behind regular sign-in
        // Default is just /v3/api-docs
        System.setProperty("springdoc.api-docs.path", "/manager/v3/api-docs");
        // Same for swagger ui
        System.setProperty("springdoc.swagger-ui.path", "/manager/swagger-ui/index.html");
        // We only want REST APIs here (de.rwth.idsg.steve.web.api package)
        System.setProperty("springdoc.paths-to-match", "/api/**");
        // Sort controllers alphabetically by their path
        System.setProperty("springdoc.swagger-ui.tagsSorter", "alpha");
        // Sort endpoints (within a controller) alphabetically by their path
        System.setProperty("springdoc.swagger-ui.operationsSorter", "alpha");
    }

    @Bean
    public OpenAPI apiDocs() {
        String title = "SteVe REST API Documentation";

        String securityName = "basicAuth";

        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .name(securityName);

        return new OpenAPI()
            .info(new Info()
                .title(title)
                .description(title)
                .license(new License()
                    .name("GPL-3.0")
                    .url("https://github.com/steve-community/steve/blob/master/LICENSE.txt")
                )
                .version(SteveConfiguration.CONFIG.getSteveVersion())
            )
            // define a security schema
            .components(new Components().addSecuritySchemes(securityName, securityScheme))
            // and activate it for all endpoints
            .addSecurityItem(new SecurityRequirement().addList(securityName));
    }
}
