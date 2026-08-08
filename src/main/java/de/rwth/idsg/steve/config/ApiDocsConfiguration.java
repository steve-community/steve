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
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public static final String API_DOCS_PATH = "/manager/v3/api-docs";

    public static final String ConfigurationKeyEnum_Read_Keys = "ConfigurationKeyEnum_Read_Keys";
    public static final String ConfigurationKeyEnum_Write_Keys = "ConfigurationKeyEnum_Write_Keys";

    private static final String TITLE = "SteVe REST API Documentation";
    private static final String[] API_PACKAGES = new String[]{
        "de.rwth.idsg.steve.web.api"
    };

    static {
        // Set the path with prefix /manager to protect the documentation behind regular sign-in
        // Default is just /v3/api-docs
        System.setProperty("springdoc.api-docs.path", API_DOCS_PATH);
        // Same for swagger ui
        System.setProperty("springdoc.swagger-ui.path", "/manager/swagger-ui/index.html");
        // Do not expose internal (web UI-related old-school AJAX-type) APIs
        System.setProperty("springdoc.paths-to-exclude", "/manager/**");
        // Sort controllers alphabetically by their path
        System.setProperty("springdoc.swagger-ui.tagsSorter", "alpha");
        // Sort endpoints (within a controller) alphabetically by their path
        System.setProperty("springdoc.swagger-ui.operationsSorter", "alpha");
        // Sort schemas/DTOs alphabetically
        System.setProperty("springdoc.writer-with-order-by-keys", "true");
    }

    /**
     * Some OCPP operations' API payloads (e.g. {@link de.rwth.idsg.steve.web.dto.ocpp.ResetParams} are directly
     * referencing OCPP enums (e.g. {@link ocpp.cp._2015._10.ResetType} that come with XML annotations. In order
     * for Swagger/OpenAPI properly derive the correct enum values, we need to add XML binding introspector.
     */
    public ApiDocsConfiguration(ObjectMapperProvider objectMapperProvider) {
        var mapper = objectMapperProvider.jsonMapper();
        var existing = mapper.getSerializationConfig().getAnnotationIntrospector();
        var xmlBindAnnotationIntrospector = new JakartaXmlBindAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(existing, xmlBindAnnotationIntrospector));
    }

    @Bean
    public GroupedOpenApi steveApi(SteveProperties steveProperties) {
        return GroupedOpenApi.builder()
            .group("admin")
            .displayName("Admin")
            .packagesToScan(API_PACKAGES)
            .pathsToMatch("/api/**")
            .addOpenApiCustomizer(steveApiCustomizer(steveProperties))
            .build();
    }

    private static OpenApiCustomizer steveApiCustomizer(SteveProperties steveProperties) {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            addInfo(openApi, steveProperties);
            addAuthScheme(openApi);
            addEnumConfSchemas(openApi);

            // https://stackoverflow.com/a/68185254
            openApi.setServers(List.of(new Server().url("/").description("Default Server URL")));
        };
    }

    /**
     * OCPP operations Get- and Change-Configuration allow different sets of keys. Not all keys are readable and
     * writable. In order for OpenAPI spec to be correct in that regard, we specify them here.
     */
    private static void addEnumConfSchemas(OpenAPI openApi) {
        openApi.getComponents().addSchemas(
            ConfigurationKeyEnum_Read_Keys,
            new StringSchema()._enum(new ArrayList<>(ConfigurationKeyEnum.READ))
        );

        openApi.getComponents().addSchemas(
            ConfigurationKeyEnum_Write_Keys,
            new StringSchema()._enum(new ArrayList<>(ConfigurationKeyEnum.WRITE))
        );
    }

    private static void addInfo(OpenAPI openApi, SteveProperties steveProperties) {
        var info = new Info()
            .title(TITLE)
            .license(new License()
                .name("GPL-3.0")
                .url("https://github.com/steve-community/steve/blob/master/LICENSE.txt")
            )
            .version(steveProperties.getVersion());

        openApi.setInfo(info);
    }

    private static void addAuthScheme(OpenAPI openApi) {
        String securityName = "basicAuth";

        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .name(securityName);

        // define a security schema
        openApi.getComponents().addSecuritySchemes(securityName, securityScheme);
        // and activate it for all endpoints
        openApi.addSecurityItem(new SecurityRequirement().addList(securityName));
    }
}
