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

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 29.10.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChargePointService {

    private final ChargePointRepository chargePointRepository;

    public Optional<String> getRegistrationStatus(String chargeBoxId) {
        return chargePointRepository.getRegistrationStatus(chargeBoxId);
    }

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

}
