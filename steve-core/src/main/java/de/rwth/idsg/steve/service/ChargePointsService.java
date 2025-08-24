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

import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.web.api.exception.NotFoundException;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargePointsService {

    private final ChargePointRepository chargePointRepository;

    public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
        return chargePointRepository.getOverview(form);
    }

    public ChargePoint.Details getDetails(int chargeBoxPk) {
        return chargePointRepository.getDetails(chargeBoxPk).orElseThrow(
            () -> new NotFoundException("Charge Point with ID " + chargeBoxPk + " not found")
        );
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

    public List<String> getChargeBoxIds() {
        return chargePointRepository.getChargeBoxIds();
    }

    public void addChargePointList(List<String> chargeBoxIdList) {
        chargePointRepository.addChargePointList(chargeBoxIdList);
    }

    public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
        return chargePointRepository.getNonZeroConnectorIds(chargeBoxId);
    }
}
