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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupDiagnostics {

    private final Environment environment;
    private final DataSource dataSource;
    private final SecurityProfileConfiguration securityConfig;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${context.path:steve}")
    private String contextPath;

    @Value("${steve.gateway.enabled:false}")
    private boolean gatewayEnabled;

    @Value("${ocpp.v20.enabled:false}")
    private boolean ocpp20Enabled;

    @PostConstruct
    public void earlyDiagnostics() {
        log.info("=".repeat(80));
        log.info("SteVe Startup - Early Diagnostics (PostConstruct)");
        log.info("=".repeat(80));
        log.info("StartupDiagnostics bean created successfully");
        log.info("Java Version: {}", System.getProperty("java.version"));
        log.info("Working Directory: {}", System.getProperty("user.dir"));
        log.info("Active Profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        log.info("Starting database connection test...");

        try (Connection conn = dataSource.getConnection()) {
            log.info("Database connection: SUCCESS");
            DatabaseMetaData metaData = conn.getMetaData();
            log.info("Database: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
        } catch (Exception e) {
            log.error("Database connection: FAILED - {}", e.getMessage());
            log.error("This will prevent application startup!");
        }

        log.info("PostConstruct diagnostics complete. Waiting for full application startup...");
        log.info("=".repeat(80));
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        log.info("=".repeat(80));
        log.info("ApplicationStartedEvent received - Spring context is ready");
        log.info("=".repeat(80));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=".repeat(80));
        log.info("SteVe Application Startup Diagnostics");
        log.info("=".repeat(80));

        printSystemInfo();
        printDatabaseInfo();
        printFlywayMigrationStatus();
        printConfigurationInfo();
        printSecurityInfo();
        printGatewayInfo();
        printNetworkInfo();
        printFileSystemInfo();
        printStartupSummary();

        log.info("=".repeat(80));
    }

    private void printSystemInfo() {
        log.info("");
        log.info("SYSTEM INFORMATION:");
        log.info("  Java Version: {}", System.getProperty("java.version"));
        log.info("  Java Vendor: {}", System.getProperty("java.vendor"));
        log.info("  Java Home: {}", System.getProperty("java.home"));
        log.info("  OS Name: {}", System.getProperty("os.name"));
        log.info("  OS Version: {}", System.getProperty("os.version"));
        log.info("  OS Architecture: {}", System.getProperty("os.arch"));
        log.info("  Available Processors: {}", Runtime.getRuntime().availableProcessors());
        log.info("  Max Memory: {} MB", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        log.info("  Total Memory: {} MB", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        log.info("  Free Memory: {} MB", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        log.info("  Working Directory: {}", System.getProperty("user.dir"));
        log.info("  Active Profiles: {}", Arrays.toString(environment.getActiveProfiles()));
    }

    private void printDatabaseInfo() {
        log.info("");
        log.info("DATABASE CONNECTION:");
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            log.info("  Database Product: {}", metaData.getDatabaseProductName());
            log.info("  Database Version: {}", metaData.getDatabaseProductVersion());
            log.info("  JDBC Driver: {}", metaData.getDriverName());
            log.info("  JDBC Driver Version: {}", metaData.getDriverVersion());
            log.info("  Connection URL: {}", metaData.getURL());
            log.info("  Connection Valid: YES");

            log.info("  Database Schemas:");
            try (ResultSet schemas = metaData.getSchemas()) {
                while (schemas.next()) {
                    String schemaName = schemas.getString("TABLE_SCHEM");
                    if (schemaName.equals("stevedb") || schemaName.equals(conn.getSchema())) {
                        log.info("    - {} (ACTIVE)", schemaName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("  ERROR: Failed to connect to database: {}", e.getMessage());
            log.error("  This is a CRITICAL error. Application will not function properly.");
        }
    }

    private void printFlywayMigrationStatus() {
        log.info("");
        log.info("FLYWAY MIGRATION STATUS:");
        try (Connection conn = dataSource.getConnection()) {
            String schemaHistoryTable = "flyway_schema_history";
            String legacyTable = "schema_version";

            DatabaseMetaData metaData = conn.getMetaData();
            boolean hasFlywayTable = false;
            boolean hasLegacyTable = false;

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if (schemaHistoryTable.equalsIgnoreCase(tableName)) {
                        hasFlywayTable = true;
                    } else if (legacyTable.equalsIgnoreCase(tableName)) {
                        hasLegacyTable = true;
                    }
                }
            }

            String activeTable = hasFlywayTable ? schemaHistoryTable : (hasLegacyTable ? legacyTable : null);

            if (activeTable == null) {
                log.error("  ERROR: No Flyway schema history table found!");
                log.error("  Expected: {} or {}", schemaHistoryTable, legacyTable);
                log.error("  Database may not be initialized. Run migrations first.");
                return;
            }

            log.info("  Schema History Table: {}", activeTable);

            String query = String.format(
                "SELECT version, description, type, installed_on, success " +
                "FROM %s ORDER BY installed_rank", activeTable
            );

            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(query)) {

                int totalMigrations = 0;
                int successfulMigrations = 0;
                int failedMigrations = 0;
                String latestVersion = null;
                String latestDescription = null;

                log.info("");
                log.info("  Migration History:");

                while (rs.next()) {
                    totalMigrations++;
                    String version = rs.getString("version");
                    String description = rs.getString("description");
                    boolean success = rs.getBoolean("success");
                    String installedOn = rs.getString("installed_on");

                    if (success) {
                        successfulMigrations++;
                        log.info("    [OK] {} - {} ({})", version, description, installedOn);
                        latestVersion = version;
                        latestDescription = description;
                    } else {
                        failedMigrations++;
                        log.error("    [FAILED] {} - {} ({})", version, description, installedOn);
                    }
                }

                log.info("");
                log.info("  Total Migrations: {}", totalMigrations);
                log.info("  Successful: {}", successfulMigrations);
                log.info("  Failed: {}", failedMigrations);

                if (latestVersion != null) {
                    log.info("  Current Version: {} ({})", latestVersion, latestDescription);
                } else {
                    log.warn("  WARNING: No successful migrations found");
                }

                if (failedMigrations > 0) {
                    log.error("");
                    log.error("  CRITICAL: {} failed migration(s) detected!", failedMigrations);
                    log.error("  Run 'mvn flyway:repair' to fix migration issues.");
                    log.error("  Application may not function correctly.");
                }
            }

        } catch (Exception e) {
            log.error("  ERROR: Failed to check migration status: {}", e.getMessage());
            log.error("  This may indicate database connection issues or missing tables.");
        }
    }

    private void printConfigurationInfo() {
        log.info("");
        log.info("APPLICATION CONFIGURATION:");
        log.info("  Context Path: /{}", contextPath);
        log.info("  Server Port: {}", serverPort);
        log.info("  OCPP 1.x Enabled: YES");
        log.info("  OCPP 2.0 Enabled: {}", ocpp20Enabled ? "YES" : "NO");
        log.info("  Gateway Enabled: {}", gatewayEnabled ? "YES" : "NO");

        String webApiKey = environment.getProperty("webapi.key");
        String webApiValue = environment.getProperty("webapi.value");
        if (webApiKey != null && webApiValue != null && !webApiValue.isEmpty()) {
            log.info("  Web API: ENABLED (Key: {})", webApiKey);
        } else {
            log.info("  Web API: DISABLED");
        }
    }

    private void printSecurityInfo() {
        log.info("");
        log.info("SECURITY CONFIGURATION:");
        log.info("  Security Profile: {}", securityConfig.getSecurityProfile());
        log.info("  TLS Enabled: {}", securityConfig.isTlsEnabled());

        if (securityConfig.isTlsEnabled()) {
            log.info("  Keystore Path: {}", securityConfig.getKeystorePath());
            File keystoreFile = new File(securityConfig.getKeystorePath());
            if (keystoreFile.exists()) {
                log.info("  Keystore File: EXISTS (Size: {} bytes)", keystoreFile.length());
                log.info("  Keystore Readable: {}", keystoreFile.canRead() ? "YES" : "NO");
            } else {
                log.error("  Keystore File: NOT FOUND - TLS will not work!");
            }

            log.info("  Keystore Type: {}", securityConfig.getKeystoreType());
            log.info("  Truststore Path: {}", securityConfig.getTruststorePath());

            if (!securityConfig.getTruststorePath().isEmpty()) {
                File truststoreFile = new File(securityConfig.getTruststorePath());
                if (truststoreFile.exists()) {
                    log.info("  Truststore File: EXISTS (Size: {} bytes)", truststoreFile.length());
                    log.info("  Truststore Readable: {}", truststoreFile.canRead() ? "YES" : "NO");
                } else {
                    log.error("  Truststore File: NOT FOUND - Client auth will not work!");
                }
            }

            log.info("  Client Auth Required: {}", securityConfig.isClientAuthRequired());
            log.info("  TLS Protocols: {}", String.join(", ", securityConfig.getTlsProtocols()));
            log.info("  Certificate Validity (years): {}", securityConfig.getCertificateValidityYears());
        }
    }

    private void printGatewayInfo() {
        if (gatewayEnabled) {
            log.info("");
            log.info("GATEWAY CONFIGURATION:");
            log.info("  Gateway Enabled: YES");

            boolean ocpiEnabled = environment.getProperty("steve.gateway.ocpi.enabled", Boolean.class, false);
            boolean oicpEnabled = environment.getProperty("steve.gateway.oicp.enabled", Boolean.class, false);

            log.info("  OCPI Enabled: {}", ocpiEnabled ? "YES" : "NO");
            if (ocpiEnabled) {
                log.info("    OCPI Version: {}", environment.getProperty("steve.gateway.ocpi.version", "NOT SET"));
                log.info("    Country Code: {}", environment.getProperty("steve.gateway.ocpi.country-code", "NOT SET"));
                log.info("    Party ID: {}", environment.getProperty("steve.gateway.ocpi.party-id", "NOT SET"));
            }

            log.info("  OICP Enabled: {}", oicpEnabled ? "YES" : "NO");
            if (oicpEnabled) {
                log.info("    OICP Version: {}", environment.getProperty("steve.gateway.oicp.version", "NOT SET"));
                log.info("    Provider ID: {}", environment.getProperty("steve.gateway.oicp.provider-id", "NOT SET"));
            }
        }
    }

    private void printNetworkInfo() {
        log.info("");
        log.info("NETWORK CONFIGURATION:");
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            log.info("  Hostname: {}", localhost.getHostName());
            log.info("  IP Address: {}", localhost.getHostAddress());

            String serverHost = environment.getProperty("server.host", "0.0.0.0");
            log.info("  Server Bind Address: {}", serverHost);
            log.info("  Server Port: {}", serverPort);

            log.info("");
            log.info("  Access URLs:");
            if ("0.0.0.0".equals(serverHost) || "::".equals(serverHost)) {
                log.info("    Local:    http://localhost:{}/{}", serverPort, contextPath);
                log.info("    Network:  http://{}:{}/{}", localhost.getHostAddress(), serverPort, contextPath);
            } else {
                log.info("    URL:      http://{}:{}/{}", serverHost, serverPort, contextPath);
            }

            log.info("");
            log.info("  OCPP Endpoints:");
            log.info("    SOAP/XML:     http://{}:{}/{}/services/CentralSystemService",
                localhost.getHostAddress(), serverPort, contextPath);
            log.info("    WebSocket:    ws://{}:{}/{}/websocket/CentralSystemService/{{chargePointId}}",
                localhost.getHostAddress(), serverPort, contextPath);

        } catch (Exception e) {
            log.error("  ERROR: Failed to retrieve network information: {}", e.getMessage());
        }
    }

    private void printFileSystemInfo() {
        log.info("");
        log.info("FILESYSTEM:");

        String userDir = System.getProperty("user.dir");
        File workDir = new File(userDir);
        log.info("  Working Directory: {}", userDir);
        log.info("  Working Directory Writable: {}", workDir.canWrite() ? "YES" : "NO");

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        log.info("  Temp Directory: {}", tmpDir.getAbsolutePath());
        log.info("  Temp Directory Writable: {}", tmpDir.canWrite() ? "YES" : "NO");

        String logFile = environment.getProperty("logging.file.name");
        if (logFile != null) {
            File logFileObj = new File(logFile);
            log.info("  Log File: {}", logFileObj.getAbsolutePath());
            log.info("  Log File Writable: {}", logFileObj.getParentFile().canWrite() ? "YES" : "NO");
        }
    }

    private void printStartupSummary() {
        log.info("");
        log.info("STARTUP SUMMARY:");

        boolean hasErrors = false;
        boolean hasWarnings = false;

        try (Connection conn = dataSource.getConnection()) {
            String activeTable = null;
            DatabaseMetaData metaData = conn.getMetaData();

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if ("flyway_schema_history".equalsIgnoreCase(tableName)) {
                        activeTable = "flyway_schema_history";
                        break;
                    } else if ("schema_version".equalsIgnoreCase(tableName)) {
                        activeTable = "schema_version";
                    }
                }
            }

            if (activeTable != null) {
                String query = String.format("SELECT COUNT(*) as failed FROM %s WHERE success = false", activeTable);
                try (var stmt = conn.createStatement();
                     var rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        int failedCount = rs.getInt("failed");
                        if (failedCount > 0) {
                            log.error("  [CRITICAL] {} failed migration(s) detected", failedCount);
                            hasErrors = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("  [CRITICAL] Database connection FAILED: {}", e.getMessage());
            hasErrors = true;
        }

        if (securityConfig.isTlsEnabled()) {
            File keystoreFile = new File(securityConfig.getKeystorePath());
            if (!keystoreFile.exists()) {
                log.error("  [CRITICAL] Keystore file not found - TLS will not work");
                hasErrors = true;
            }
        }

        log.info("");
        if (hasErrors) {
            log.error("  STATUS: APPLICATION STARTED WITH ERRORS");
            log.error("  Some features may not work correctly. Check logs above for details.");
        } else if (hasWarnings) {
            log.warn("  STATUS: APPLICATION STARTED WITH WARNINGS");
            log.warn("  Application is functional but some issues were detected.");
        } else {
            log.info("  STATUS: APPLICATION STARTED SUCCESSFULLY");
            log.info("  All systems operational.");
        }
    }
}