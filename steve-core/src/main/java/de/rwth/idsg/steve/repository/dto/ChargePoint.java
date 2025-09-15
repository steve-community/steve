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
package de.rwth.idsg.steve.repository.dto;

import com.neovisionaries.i18n.CountryCode;
import de.rwth.idsg.steve.web.dto.Address;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 */
public final class ChargePoint {

    @Getter
    @Builder
    public static final class Overview {
        private final int chargeBoxPk;
        private final String chargeBoxId, description, ocppProtocol, lastHeartbeatTimestamp;
        private final Instant lastHeartbeatTimestampDT;
    }

    @Getter
    @Builder
    public static final class Details {
        private final Integer chargeBoxPk;
        private final String chargeBoxId;
        private final @Nullable String endpointAddress;
        private final String ocppProtocol;
        private final @Nullable String chargePointVendor;
        private final @Nullable String chargePointModel;
        private final @Nullable String chargePointSerialNumber;
        private final @Nullable String chargeBoxSerialNumber;
        private final @Nullable String fwVersion;
        private final @Nullable Instant fwUpdateTimestamp;
        private final @Nullable String iccid;
        private final @Nullable String imsi;
        private final @Nullable String meterType;
        private final @Nullable String meterSerialNumber;
        private final @Nullable String diagnosticsStatus;
        private final @Nullable Instant diagnosticsTimestamp;
        private final @Nullable Instant lastHeartbeatTimestamp;
        private final String description;
        private final BigDecimal locationLatitude;
        private final BigDecimal locationLongitude;
        private final String note;
        private final String adminAddress;
        private final boolean insertConnectorStatusAfterTransactionMsg;
        private final String registrationStatus;
        private final String street;
        private final String houseNumber;
        private final String zipCode;
        private final String city;
        private final CountryCode country;

        public Address getAddress() {
            var address = new Address();
            address.setStreet(street);
            address.setHouseNumber(houseNumber);
            address.setZipCode(zipCode);
            address.setCity(city);
            address.setCountry(country);
            return address;
        }
    }
}
