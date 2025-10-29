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

import com.google.common.util.concurrent.Striped;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointService {

    private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);
    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    private final ChargePointRepository chargePointRepository;
    private final SteveProperties steveProperties;

    public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> inStatusFilter, List<String> chargeBoxIdFilter) {
        return chargePointRepository.getChargePointSelect(protocol, inStatusFilter, chargeBoxIdFilter);
    }

    public List<String> getChargeBoxIds() {
        return chargePointRepository.getChargeBoxIds();
    }

    public Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList) {
        return chargePointRepository.getChargeBoxIdPkPair(chargeBoxIdList);
    }

    public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
        return chargePointRepository.getOverview(form);
    }

    public ChargePoint.Details getDetails(int chargeBoxPk) {
        return chargePointRepository.getDetails(chargeBoxPk);
    }

    public List<ConnectorStatus> getChargePointConnectorStatus() {
        return getChargePointConnectorStatus(null);
    }

    public List<ConnectorStatus> getChargePointConnectorStatus(@Nullable ConnectorStatusForm form) {
        return  chargePointRepository.getChargePointConnectorStatus(form);
    }

    public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
        return chargePointRepository.getNonZeroConnectorIds(chargeBoxId);
    }

    public void addChargePointList(List<String> chargeBoxIdList) {
        chargePointRepository.addChargePointList(chargeBoxIdList);
    }

    public int addChargePoint(ChargePointForm form) {
        return chargePointRepository.addChargePoint(form);
    }

    public void updateChargePoint(ChargePointForm form) {
        chargePointRepository.updateChargePoint(form);
    }

    public void deleteChargePoint(int chargeBoxPk) {
        chargePointRepository.deleteChargePoint(chargeBoxPk);
    }

    // -------------------------------------------------------------------------
    // Unknown
    // -------------------------------------------------------------------------

    public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
        return unknownChargePointService.getObjects();
    }

    public void removeUnknown(List<String> chargeBoxIdList) {
        unknownChargePointService.removeAll(chargeBoxIdList);
    }

    // -------------------------------------------------------------------------
    // Registration status
    // -------------------------------------------------------------------------

    public Optional<RegistrationStatus> getRegistrationStatus(String chargeBoxId) {
        Lock l = isRegisteredLocks.get(chargeBoxId);
        l.lock();
        try {
            Optional<RegistrationStatus> status = getRegistrationStatusInternal(chargeBoxId);
            if (status.isEmpty()) {
                unknownChargePointService.processNewUnidentified(chargeBoxId);
            }
            return status;
        } finally {
            l.unlock();
        }
    }

    private Optional<RegistrationStatus> getRegistrationStatusInternal(String chargeBoxId) {
        // 1. exit if already registered
        Optional<String> status = chargePointRepository.getRegistrationStatus(chargeBoxId);
        if (status.isPresent()) {
            try {
                return Optional.ofNullable(RegistrationStatus.fromValue(status.get()));
            } catch (Exception e) {
                // in cases where the database entry (string) is altered, and therefore cannot be converted to enum
                log.error("Exception happened", e);
                return Optional.empty();
            }
        }

        // 2. ok, this chargeBoxId is unknown. exit if auto-register is disabled
        if (!steveProperties.getOcpp().isAutoRegisterUnknownStations()) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            this.addChargePointList(Collections.singletonList(chargeBoxId));
            log.warn("Auto-registered unknown chargebox '{}'", chargeBoxId);
            return Optional.of(RegistrationStatus.ACCEPTED); // default db value is accepted
        } catch (Exception e) {
            log.error("Failed to auto-register unknown chargebox '{}'", chargeBoxId, e);
            return Optional.empty();
        }
    }

}
