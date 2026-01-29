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
package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * https://stackoverflow.com/a/65557714
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.09.2022
 */
@Configuration
public class ApiDocsConfiguration {

    public static final String ConfigurationKeyEnum_Read_Keys = "ConfigurationKeyEnum_Read_Keys";
    public static final String ConfigurationKeyEnum_Write_Keys = "ConfigurationKeyEnum_Write_Keys";

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
        // Sort schemas/DTOs alphabetically
        System.setProperty("springdoc.writer-with-order-by-keys", "true");
    }

    @Bean
    public OpenAPI apiDocs(SteveProperties steveProperties) {
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
                .version(steveProperties.getVersion())
            )
            // https://stackoverflow.com/a/68185254
            .servers(List.of(new Server().url("/").description("Default Server URL")))
            // define a security schema
            .components(new Components().addSecuritySchemes(securityName, securityScheme))
            // and activate it for all endpoints
            .addSecurityItem(new SecurityRequirement().addList(securityName));
    }

    /**
     * 1. Some OCPP operations' API payloads (e.g. {@link de.rwth.idsg.steve.web.dto.ocpp.ResetParams} are directly
     * referencing OCPP enums (e.g. {@link ocpp.cp._2015._10.ResetType} that come with XML annotations. In order
     * for Swagger/OpenAPI properly derive the correct enum values, we need to add XML binding introspector.
     *
     * 2. OCPP operations Get- and Change-Configuration allow different sets of keys. Not all keys are readable and
     * writable. In order for OpenAPI spec to be correct in that regard, we specify them here.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public OpenApiCustomizer openApiCustomizer(ObjectMapperProvider objectMapperProvider) {
        {
            var mapper = objectMapperProvider.jsonMapper();
            var existing = mapper.getSerializationConfig().getAnnotationIntrospector();
            var xmlBindAnnotationIntrospector = new JakartaXmlBindAnnotationIntrospector(mapper.getTypeFactory());
            mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(existing, xmlBindAnnotationIntrospector));
        }

        return openApi -> {
            Components components = openApi.getComponents();

            {
                var schema = new StringSchema();
                schema.setEnum(new ArrayList<>(ConfigurationKeyEnum.READ));
                components.addSchemas(ConfigurationKeyEnum_Read_Keys, schema);
            }

            {
                var schema = new StringSchema();
                schema.setEnum(new ArrayList<>(ConfigurationKeyEnum.WRITE));
                components.addSchemas(ConfigurationKeyEnum_Write_Keys, schema);
            }
        };
    }
}
