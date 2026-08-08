/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.ocpp.model.ConnectorFormat;
import de.rwth.idsg.steve.ocpp.model.ConnectorType;
import de.rwth.idsg.steve.ocpp.model.PowerType;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.web.dto.ChargePointDeviceModelForm;
import de.rwth.idsg.steve.web.dto.ChargePointDeviceModelForm.EvseConnectorForm;
import de.rwth.idsg.steve.web.dto.ChargePointFormForUpdate;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import jooq.steve.db.tables.records.EvseConnectorRecord;
import jooq.steve.db.tables.records.EvseRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ocpp.cs._2015._10.RegistrationStatus;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChargePointDetailsMapper {

    public static ChargePointFormForUpdate mapToForm(ChargePoint.Details cp) {
        ChargeBoxRecord chargeBox = cp.getChargeBox();

        ChargePointFormForUpdate form = new ChargePointFormForUpdate();
        form.setChargeBoxPk(chargeBox.getChargeBoxPk());
        form.setChargeBoxId(chargeBox.getChargeBoxId());
        form.setNote(chargeBox.getNote());
        form.setDescription(chargeBox.getDescription());
        form.setInsertConnectorStatusAfterTransactionMsg(chargeBox.getInsertConnectorStatusAfterTransactionMsg());
        form.setAdminAddress(chargeBox.getAdminAddress());
        form.setRegistrationStatus(RegistrationStatus.fromValue(chargeBox.getRegistrationStatus()));
        form.setAddress(AddressMapper.recordToDto(cp.getAddress()));

        form.setSecurityProfile(OcppSecurityProfile.fromValue(chargeBox.getSecurityProfile()));
        form.setAuthPassword(null); // make sure that the pwd from record does not escape
        form.setHasAuthPassword(!StringUtils.isEmpty(chargeBox.getAuthPassword()));

        form.setDeviceModelForm(createDeviceModelForm(cp));
        return form;
    }

    private static ChargePointDeviceModelForm createDeviceModelForm(ChargePoint.Details cp) {
        var evses = cp.getEvses()
            .stream()
            .map(evseRecord -> {
                var evseConnectorRecords = cp.getEvseConnectorsByEvsePk().get(evseRecord.getEvsePk());
                return toEvseForm(evseRecord, evseConnectorRecords);
            }).toList();

        var deviceModelForm = new ChargePointDeviceModelForm();
        deviceModelForm.setEvses(evses);
        return deviceModelForm;
    }

    private static ChargePointDeviceModelForm.EvseForm toEvseForm(EvseRecord evseRecord,
                                                                  @Nullable List<EvseConnectorRecord> connectorRecords) {
        List<EvseConnectorForm> connectors = (connectorRecords == null)
            ? Collections.emptyList()
            : connectorRecords.stream().map(ChargePointDetailsMapper::toEvseConnectorForm).toList();

        var form = new ChargePointDeviceModelForm.EvseForm();
        form.setEvsePk(evseRecord.getEvsePk());
        form.setEvseId(evseRecord.getEvseId());
        form.setTopologySource(evseRecord.getTopologySource());
        form.setEvseIdExternal(evseRecord.getEvseIdExternal());
        form.setConnectors(connectors);
        return form;
    }

    private static EvseConnectorForm toEvseConnectorForm(EvseConnectorRecord rec) {
        var form = new EvseConnectorForm();
        form.setEvseConnectorPk(rec.getEvseConnectorPk());
        form.setConnectorId(rec.getConnectorId());
        form.setConnectorType(ConnectorType.fromNullable(rec.getConnectorType()));
        form.setConnectorFormat(ConnectorFormat.fromNullable(rec.getConnectorFormat()));
        form.setPowerType(PowerType.fromNullable(rec.getPowerType()));
        form.setMaxVoltage(rec.getMaxVoltage());
        form.setMaxAmperage(rec.getMaxAmperage());
        form.setMaxElectricPower(rec.getMaxElectricPower());
        return form;
    }

}
