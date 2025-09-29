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
package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.DataTransferRequest;
import de.rwth.idsg.steve.ocpp20.model.DataTransferResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class DataTransferTask extends Ocpp20Task<DataTransferRequest, DataTransferResponse> {

    private final String vendorId;
    private final String messageId;
    private final String data;

    public DataTransferTask(List<String> chargeBoxIds, String vendorId, String messageId, String data) {
        super("DataTransfer", chargeBoxIds);
        this.vendorId = vendorId;
        this.messageId = messageId;
        this.data = data;
    }

    @Override
    public DataTransferRequest createRequest() {
        DataTransferRequest request = new DataTransferRequest();
        request.setVendorId(vendorId);

        if (messageId != null && !messageId.isEmpty()) {
            request.setMessageId(messageId);
        }

        if (data != null && !data.isEmpty()) {
            request.setData(data);
        }

        return request;
    }

    @Override
    public Class<DataTransferResponse> getResponseClass() {
        return DataTransferResponse.class;
    }
}