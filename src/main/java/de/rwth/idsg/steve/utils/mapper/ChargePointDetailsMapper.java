/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargePointDetailsMapper {

    public static ChargePointForm mapToForm(ChargePoint.Details cp) {
        ChargeBoxRecord chargeBox = cp.getChargeBox();

        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxPk(chargeBox.getChargeBoxPk());
        form.setChargeBoxId(chargeBox.getChargeBoxId());
        form.setNote(chargeBox.getNote());
        form.setDescription(chargeBox.getDescription());
        form.setLocationLatitude(chargeBox.getLocationLatitude());
        form.setLocationLongitude(chargeBox.getLocationLongitude());
        form.setInsertConnectorStatusAfterTransactionMsg(chargeBox.getInsertConnectorStatusAfterTransactionMsg());
        form.setAdminAddress(chargeBox.getAdminAddress());
        form.setRegistrationStatus(chargeBox.getRegistrationStatus());
        form.setAddress(AddressMapper.recordToDto(cp.getAddress()));

        return form;
    }

}
