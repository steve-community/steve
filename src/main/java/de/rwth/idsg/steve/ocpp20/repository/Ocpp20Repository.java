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
package de.rwth.idsg.steve.ocpp20.repository;

import de.rwth.idsg.steve.ocpp20.model.*;

public interface Ocpp20Repository {

    void insertBootNotification(String chargeBoxId, BootNotificationRequest request, BootNotificationResponse response);

    void insertTransaction(String chargeBoxId, String transactionId, TransactionEventRequest request);

    void updateTransaction(String transactionId, TransactionEventRequest request);

    void insertTransactionEvent(String chargeBoxId, String transactionId, TransactionEventRequest request);

    void insertAuthorization(String chargeBoxId, AuthorizeRequest request, AuthorizeResponse response);

    void upsertVariable(String chargeBoxId, String componentName, String variableName, String value);

    String getTransactionByRemoteId(String chargeBoxId, String remoteStartId);
}