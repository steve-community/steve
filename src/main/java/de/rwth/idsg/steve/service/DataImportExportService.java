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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.DataImportExportRepository;
import de.rwth.idsg.steve.web.dto.DataExportForm.ExportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Named;
import org.jooq.Table;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static jooq.steve.db.Tables.ADDRESS;
import static jooq.steve.db.Tables.CERTIFICATE;
import static jooq.steve.db.Tables.CHARGE_BOX;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_INSTALLED;
import static jooq.steve.db.Tables.CHARGE_BOX_CERTIFICATE_SIGNED;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_FIRMWARE_UPDATE_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_EVENT;
import static jooq.steve.db.Tables.CHARGE_BOX_LOG_UPLOAD_JOB;
import static jooq.steve.db.Tables.CHARGE_BOX_SECURITY_EVENT;
import static jooq.steve.db.Tables.CHARGING_PROFILE;
import static jooq.steve.db.Tables.CHARGING_SCHEDULE_PERIOD;
import static jooq.steve.db.Tables.CONNECTOR;
import static jooq.steve.db.Tables.CONNECTOR_CHARGING_PROFILE;
import static jooq.steve.db.Tables.CONNECTOR_METER_VALUE;
import static jooq.steve.db.Tables.CONNECTOR_STATUS;
import static jooq.steve.db.Tables.OCPP_TAG;
import static jooq.steve.db.Tables.RESERVATION;
import static jooq.steve.db.Tables.SETTINGS;
import static jooq.steve.db.Tables.TRANSACTION_START;
import static jooq.steve.db.Tables.TRANSACTION_STOP;
import static jooq.steve.db.Tables.TRANSACTION_STOP_FAILED;
import static jooq.steve.db.Tables.USER;
import static jooq.steve.db.Tables.USER_OCPP_TAG;
import static jooq.steve.db.Tables.WEB_USER;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 20.11.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportExportService {

    private static final List<Table<?>> MASTER_DATA_TABLES = List.of(
        ADDRESS,
        CERTIFICATE,
        CHARGE_BOX,
        CHARGE_BOX_CERTIFICATE_SIGNED,
        CHARGING_PROFILE,
        CHARGING_SCHEDULE_PERIOD,
        CONNECTOR,
        CONNECTOR_CHARGING_PROFILE,
        OCPP_TAG,
        SETTINGS,
        USER,
        USER_OCPP_TAG,
        WEB_USER
    );

    private static final List<Table<?>> HISTORICAL_DATA_TABLES = List.of(
        CHARGE_BOX_CERTIFICATE_INSTALLED,
        CHARGE_BOX_FIRMWARE_UPDATE_EVENT,
        CHARGE_BOX_FIRMWARE_UPDATE_JOB,
        CHARGE_BOX_LOG_UPLOAD_EVENT,
        CHARGE_BOX_LOG_UPLOAD_JOB,
        CHARGE_BOX_SECURITY_EVENT,
        CONNECTOR_METER_VALUE,
        CONNECTOR_STATUS,
        RESERVATION,
        TRANSACTION_START,
        TRANSACTION_STOP,
        TRANSACTION_STOP_FAILED
    );

    private static final List<Table<?>> ALL_TABLES = Stream.of(MASTER_DATA_TABLES, HISTORICAL_DATA_TABLES)
        .flatMap(Collection::stream)
        .toList();

    private final DataImportExportRepository dataImportExportRepository;

    public List<String> getMasterDataTableNames() {
        return MASTER_DATA_TABLES.stream().map(Named::getName).toList();
    }

    public void exportZip(HttpServletResponse response, ExportType exportType) throws IOException {
        String fileName = "data-export_" + System.currentTimeMillis() + ".zip";
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"%s\"".formatted(fileName);
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/zip");

        exportZip(response.getOutputStream(), exportType);
    }

    public void importZip(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new SteveException.BadRequest("File is empty");
        }

        String fileName = file.getOriginalFilename();

        if (StringUtils.isEmpty(fileName)) {
            throw new SteveException.BadRequest("File name is empty");
        }

        if (!fileName.endsWith(".zip")) {
            throw new SteveException.BadRequest("File must be a ZIP archive");
        }

        importZip(file.getInputStream());
    }

    private void exportZip(OutputStream out, ExportType exportType) throws IOException {
        long start = System.currentTimeMillis();

        try (ZipOutputStream zipOut = new ZipOutputStream(out);
             OutputStreamWriter writer = new OutputStreamWriter(zipOut, StandardCharsets.UTF_8)) {

            List<Table<?>> tables = getTables(exportType);
            for (Table<?> table : tables) {

                // Create a new entry in the ZIP for this CSV file
                ZipEntry zipEntry = new ZipEntry(table.getName() + ".csv");
                zipOut.putNextEntry(zipEntry);

                try {
                    dataImportExportRepository.exportCsv(writer, table);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    writer.flush();
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();

        } finally {
            long stop = System.currentTimeMillis();
            long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(stop - start);
            log.info("Data export finished in {} seconds.", durationSeconds);
        }
    }

    private void importZip(InputStream in) throws IOException {
        long start = System.currentTimeMillis();

        dataImportExportRepository.beforeImport();

        try (ZipInputStream zipIn = new ZipInputStream(in)) {
            ZipEntry entry;

            while ((entry = zipIn.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".csv")) {
                    try {
                        Table<?> table = findTable(entry.getName());

                        // Wrap the input stream to prevent jOOQ Loader from closing it
                        InputStream nonClosingStream = new FilterInputStream(zipIn) {
                            @Override
                            public void close() throws IOException {
                                // Don't close the underlying stream - just do nothing
                                // The ZipInputStream will manage its own lifecycle
                            }
                        };

                        dataImportExportRepository.importCsv(nonClosingStream, table);
                    } catch (RuntimeException ex) {
                        throw ex;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                zipIn.closeEntry();
            }
        } finally {
            dataImportExportRepository.afterImport();

            long stop = System.currentTimeMillis();
            long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(stop - start);
            log.info("Data import finished in {} seconds.", durationSeconds);
        }
    }

    private Table<?> findTable(String fileName) {
        String name = fileName;
        if (name.endsWith(".csv")) {
            name = name.substring(0, name.length() - 4);
        }
        String tableName = name.toLowerCase();

        List<Table<?>> tables = getTables(ExportType.AllData); // use the superset
        for (Table<?> table : tables) {
            if (table.getName().equals(tableName)) {
                return table;
            }
        }
        throw new RuntimeException("Database table for '" + fileName + "' not found");
    }

    private static List<Table<?>> getTables(ExportType exportType) {
        return exportType == ExportType.MasterData
            ? MASTER_DATA_TABLES
            : ALL_TABLES;
    }

}
