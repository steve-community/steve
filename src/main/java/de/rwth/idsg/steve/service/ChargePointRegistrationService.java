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
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.RegistrationStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointRegistrationService {

    private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);
    private final boolean autoRegisterUnknownStations = CONFIG.getOcpp().isAutoRegisterUnknownStations();
    private final Striped<Lock> isRegisteredLocks = Striped.lock(16);

    private final ChargePointRepository chargePointRepository;

    public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
        return unknownChargePointService.getObjects();
    }

    public void removeUnknown(List<String> chargeBoxIdList) {
        unknownChargePointService.removeAll(chargeBoxIdList);
    }

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
        if (!autoRegisterUnknownStations) {
            return Optional.empty();
        }

        // 3. chargeBoxId is unknown and auto-register is enabled. insert chargeBoxId
        try {
            chargePointRepository.addChargePointList(Collections.singletonList(chargeBoxId));
            log.warn("Auto-registered unknown chargebox '{}'", chargeBoxId);
            return Optional.of(RegistrationStatus.ACCEPTED); // default db value is accepted
        } catch (Exception e) {
            log.error("Failed to auto-register unknown chargebox '{}'", chargeBoxId, e);
            return Optional.empty();
        }
    }
}
