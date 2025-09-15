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
package de.rwth.idsg.steve.jooq.mapper;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toInstant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargePointMapper {

    public static ChargePoint.Details fromRecord(ChargeBoxRecord r, @Nullable Address address) {
        r.getAdminAddress();
        var builder = ChargePoint.Details.builder()
                .chargeBoxPk(r.getChargeBoxPk())
                .chargeBoxId(r.getChargeBoxId())
                .endpointAddress(r.getEndpointAddress())
                .ocppProtocol(r.getOcppProtocol())
                .chargePointVendor(r.getChargePointVendor())
                .chargePointModel(r.getChargePointModel())
                .chargePointSerialNumber(r.getChargePointSerialNumber())
                .chargeBoxSerialNumber(r.getChargeBoxSerialNumber())
                .fwVersion(r.getFwVersion())
                .fwUpdateTimestamp(toInstant(r.getFwUpdateTimestamp()))
                .iccid(r.getIccid())
                .imsi(r.getImsi())
                .meterType(r.getMeterType())
                .meterSerialNumber(r.getMeterSerialNumber())
                .diagnosticsStatus(r.getDiagnosticsStatus())
                .diagnosticsTimestamp(toInstant(r.getDiagnosticsTimestamp()))
                .lastHeartbeatTimestamp(toInstant(r.getLastHeartbeatTimestamp()))
                .description(r.getDescription())
                .locationLatitude(r.getLocationLatitude())
                .locationLongitude(r.getLocationLongitude())
                .note(r.getNote())
                .adminAddress(r.getAdminAddress())
                .insertConnectorStatusAfterTransactionMsg(r.getInsertConnectorStatusAfterTransactionMsg())
                .registrationStatus(r.getRegistrationStatus());

        if (address != null) {
            builder.street(address.getStreet())
                    .houseNumber(address.getHouseNumber())
                    .zipCode(address.getZipCode())
                    .city(address.getCity())
                    .country(address.getCountry());
        }

        return builder.build();
    }
}
