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
package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.SecurityEvent;
import de.rwth.idsg.steve.repository.dto.Certificate;
import de.rwth.idsg.steve.repository.dto.LogFile;
import de.rwth.idsg.steve.repository.dto.FirmwareUpdate;
import de.rwth.idsg.steve.web.dto.ocpp.GetLogParams;
import org.joda.time.DateTime;

import java.util.List;

public interface SecurityRepository {

    void insertSecurityEvent(String chargeBoxId, String eventType, DateTime timestamp, String techInfo);

    void insertLogUploadStatus(String chargeBoxIdentity, Integer requestId, String status, DateTime timestamp);

    void insertFirmwareUpdateStatus(String chargeBoxIdentity, Integer requestId, String value, DateTime timestamp);

    int insertNewLogUploadJob(GetLogParams params);

    List<SecurityEvent> getSecurityEvents(String chargeBoxId, Integer limit);

    int insertCertificate(String chargeBoxId, String certificateType, String certificateData,
                          String serialNumber, String issuerName, String subjectName,
                          DateTime validFrom, DateTime validTo, String signatureAlgorithm, Integer keySize);

    void updateCertificateStatus(int certificateId, String status);

    List<Certificate> getInstalledCertificates(String chargeBoxId, String certificateType);

    void deleteCertificate(int certificateId);

    Certificate getCertificateBySerialNumber(String serialNumber);

    int insertLogFile(String chargeBoxId, String logType, Integer requestId, String filePath);

    void updateLogFileStatus(int logFileId, String uploadStatus, Long bytesUploaded);

    LogFile getLogFile(int logFileId);

    int insertFirmwareUpdate(String chargeBoxId, String firmwareLocation, String firmwareSignature,
                             String signingCertificate, DateTime retrieveDate, DateTime installDate);

    void updateFirmwareUpdateStatus(int firmwareUpdateId, String status);

    FirmwareUpdate getCurrentFirmwareUpdate(String chargeBoxId);
}
