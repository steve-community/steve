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

import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OcppTagFormMapper {

    public static OcppTagForm toForm(OcppTagActivityRecord record) {
        OcppTagForm form = new OcppTagForm();
        form.setOcppTagPk(record.getOcppTagPk());
        form.setIdTag(record.getIdTag());

        DateTime expiryDate = record.getExpiryDate();
        if (expiryDate != null) {
            form.setExpiration(expiryDate.toLocalDateTime());
        }

        form.setMaxActiveTransactionCount(record.getMaxActiveTransactionCount());
        form.setNote(record.getNote());

        String parentIdTag = record.getParentIdTag();
        if (parentIdTag == null) {
            parentIdTag = ControllerHelper.EMPTY_OPTION;
        }
        form.setParentIdTag(parentIdTag);

        return form;
    }
}
