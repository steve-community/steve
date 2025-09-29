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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.ocpp20.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.Ocpp20Authorization.OCPP20_AUTHORIZATION;
import static jooq.steve.db.tables.Ocpp20BootNotification.OCPP20_BOOT_NOTIFICATION;
import static jooq.steve.db.tables.Ocpp20Transaction.OCPP20_TRANSACTION;
import static jooq.steve.db.tables.Ocpp20TransactionEvent.OCPP20_TRANSACTION_EVENT;
import static jooq.steve.db.tables.Ocpp20Variable.OCPP20_VARIABLE;
import static jooq.steve.db.tables.Ocpp20VariableAttribute.OCPP20_VARIABLE_ATTRIBUTE;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
public class Ocpp20RepositoryImpl implements Ocpp20Repository {

    private final DSLContext ctx;
    @Qualifier("ocpp20ObjectMapper")
    private final ObjectMapper objectMapper;

    private Integer getChargeBoxPk(String chargeBoxId) {
        return ctx.select(CHARGE_BOX.CHARGE_BOX_PK)
            .from(CHARGE_BOX)
            .where(CHARGE_BOX.CHARGE_BOX_ID.eq(chargeBoxId))
            .fetchOne(CHARGE_BOX.CHARGE_BOX_PK);
    }

    private DateTime toDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return new DateTime(offsetDateTime.toInstant().toEpochMilli());
    }

    @Override
    @Transactional
    public void insertBootNotification(String chargeBoxId, BootNotificationRequest request, BootNotificationResponse response) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.warn("ChargeBox '{}' not found in database, skipping BootNotification persistence", chargeBoxId);
            return;
        }

        ctx.insertInto(OCPP20_BOOT_NOTIFICATION)
            .set(OCPP20_BOOT_NOTIFICATION.CHARGE_BOX_PK, chargeBoxPk)
            .set(OCPP20_BOOT_NOTIFICATION.CHARGING_STATION_VENDOR,
                request.getChargingStation() != null ? request.getChargingStation().getVendorName() : "Unknown")
            .set(OCPP20_BOOT_NOTIFICATION.CHARGING_STATION_MODEL,
                request.getChargingStation() != null ? request.getChargingStation().getModel() : "Unknown")
            .set(OCPP20_BOOT_NOTIFICATION.CHARGING_STATION_SERIAL_NUMBER,
                request.getChargingStation() != null ? request.getChargingStation().getSerialNumber() : null)
            .set(OCPP20_BOOT_NOTIFICATION.FIRMWARE_VERSION,
                request.getChargingStation() != null ? request.getChargingStation().getFirmwareVersion() : null)
            .set(OCPP20_BOOT_NOTIFICATION.BOOT_REASON,
                request.getReason() != null ? request.getReason().value() : "Unknown")
            .set(OCPP20_BOOT_NOTIFICATION.STATUS,
                response.getStatus() != null ? response.getStatus().value() : "Rejected")
            .set(OCPP20_BOOT_NOTIFICATION.RESPONSE_TIME,
                response.getCurrentTime() != null ? toDateTime(response.getCurrentTime()) : DateTime.now())
            .set(OCPP20_BOOT_NOTIFICATION.INTERVAL_SECONDS, response.getInterval())
            .execute();

        log.debug("Stored BootNotification for '{}': reason={}, status={}", chargeBoxId, request.getReason(), response.getStatus());
    }

    @Override
    @Transactional
    public void insertTransaction(String chargeBoxId, String transactionId, TransactionEventRequest request) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.warn("ChargeBox '{}' not found, skipping transaction persistence", chargeBoxId);
            return;
        }

        Integer evseId = (request.getEvse() != null && request.getEvse().getId() != null) ? request.getEvse().getId() : 0;
        Integer connectorId = (request.getEvse() != null && request.getEvse().getConnectorId() != null) ? request.getEvse().getConnectorId() : null;

        ctx.insertInto(OCPP20_TRANSACTION)
            .set(OCPP20_TRANSACTION.CHARGE_BOX_PK, chargeBoxPk)
            .set(OCPP20_TRANSACTION.TRANSACTION_ID, transactionId)
            .set(OCPP20_TRANSACTION.EVSE_ID, evseId)
            .set(OCPP20_TRANSACTION.CONNECTOR_ID, connectorId)
            .set(OCPP20_TRANSACTION.ID_TOKEN, request.getIdToken() != null ? request.getIdToken().getIdToken() : null)
            .set(OCPP20_TRANSACTION.ID_TOKEN_TYPE, request.getIdToken() != null && request.getIdToken().getType() != null ?
                request.getIdToken().getType().value() : null)
            .set(OCPP20_TRANSACTION.REMOTE_START_ID, request.getTransactionInfo() != null ?
                request.getTransactionInfo().getRemoteStartId() : null)
            .set(OCPP20_TRANSACTION.STARTED_AT, request.getTimestamp() != null ?
                toDateTime(request.getTimestamp()) : DateTime.now())
            .execute();

        log.debug("Stored Transaction for '{}': transactionId={}", chargeBoxId, transactionId);
    }

    @Override
    @Transactional
    public void updateTransaction(String transactionId, TransactionEventRequest request) {
        ctx.update(OCPP20_TRANSACTION)
            .set(OCPP20_TRANSACTION.STOPPED_AT, request.getTimestamp() != null ?
                toDateTime(request.getTimestamp()) : DateTime.now())
            .set(OCPP20_TRANSACTION.STOPPED_REASON, request.getTransactionInfo() != null &&
                request.getTransactionInfo().getStoppedReason() != null ?
                request.getTransactionInfo().getStoppedReason().value() : null)
            .where(OCPP20_TRANSACTION.TRANSACTION_ID.eq(transactionId))
            .execute();

        log.debug("Updated Transaction: transactionId={}", transactionId);
    }

    @Override
    @Transactional
    public void insertTransactionEvent(String chargeBoxId, String transactionId, TransactionEventRequest request) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.warn("ChargeBox '{}' not found, skipping transaction event persistence", chargeBoxId);
            return;
        }

        Integer transactionPk = ctx.select(OCPP20_TRANSACTION.TRANSACTION_PK)
            .from(OCPP20_TRANSACTION)
            .where(OCPP20_TRANSACTION.CHARGE_BOX_PK.eq(chargeBoxPk))
            .and(OCPP20_TRANSACTION.TRANSACTION_ID.eq(transactionId))
            .fetchOne(OCPP20_TRANSACTION.TRANSACTION_PK);

        if (transactionPk == null) {
            log.warn("Transaction '{}' not found for ChargeBox '{}', skipping event persistence", transactionId, chargeBoxId);
            return;
        }

        ctx.insertInto(OCPP20_TRANSACTION_EVENT)
            .set(OCPP20_TRANSACTION_EVENT.TRANSACTION_PK, transactionPk)
            .set(OCPP20_TRANSACTION_EVENT.CHARGE_BOX_PK, chargeBoxPk)
            .set(OCPP20_TRANSACTION_EVENT.TRANSACTION_ID, transactionId)
            .set(OCPP20_TRANSACTION_EVENT.EVENT_TYPE, request.getEventType() != null ? request.getEventType().value() : null)
            .set(OCPP20_TRANSACTION_EVENT.TRIGGER_REASON, request.getTriggerReason() != null ? request.getTriggerReason().value() : null)
            .set(OCPP20_TRANSACTION_EVENT.SEQ_NO, request.getSeqNo())
            .set(OCPP20_TRANSACTION_EVENT.TIMESTAMP, request.getTimestamp() != null ?
                toDateTime(request.getTimestamp()) : DateTime.now())
            .execute();

        log.debug("Stored TransactionEvent for '{}': transactionId={}, eventType={}",
            chargeBoxId, transactionId, request.getEventType());
    }

    @Override
    @Transactional
    public void insertAuthorization(String chargeBoxId, AuthorizeRequest request, AuthorizeResponse response) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.warn("ChargeBox '{}' not found, skipping authorization persistence", chargeBoxId);
            return;
        }

        ctx.insertInto(OCPP20_AUTHORIZATION)
                .set(OCPP20_AUTHORIZATION.CHARGE_BOX_PK, chargeBoxPk)
                .set(OCPP20_AUTHORIZATION.ID_TOKEN, request.getIdToken() != null ? request.getIdToken().getIdToken() : "Unknown")
                .set(OCPP20_AUTHORIZATION.ID_TOKEN_TYPE, request.getIdToken() != null && request.getIdToken().getType() != null ?
                    request.getIdToken().getType().value() : "Unknown")
                .set(OCPP20_AUTHORIZATION.STATUS, response.getIdTokenInfo() != null && response.getIdTokenInfo().getStatus() != null ?
                    response.getIdTokenInfo().getStatus().value() : "Unknown")
                .execute();

            log.debug("Stored Authorization for '{}': idToken={}, status={}",
                chargeBoxId,
                request.getIdToken() != null ? request.getIdToken().getIdToken() : null,
                response.getIdTokenInfo() != null ? response.getIdTokenInfo().getStatus() : null);
    }

    @Override
    @Transactional
    public void upsertVariable(String chargeBoxId, String componentName, String variableName, String value) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            log.warn("ChargeBox '{}' not found, skipping variable persistence", chargeBoxId);
            return;
        }

        Integer variablePk = ctx.select(OCPP20_VARIABLE.VARIABLE_PK)
            .from(OCPP20_VARIABLE)
            .where(OCPP20_VARIABLE.CHARGE_BOX_PK.eq(chargeBoxPk))
            .and(OCPP20_VARIABLE.COMPONENT_NAME.eq(componentName))
            .and(OCPP20_VARIABLE.VARIABLE_NAME.eq(variableName))
            .fetchOne(OCPP20_VARIABLE.VARIABLE_PK);

        if (variablePk == null) {
            variablePk = ctx.insertInto(OCPP20_VARIABLE)
                .set(OCPP20_VARIABLE.CHARGE_BOX_PK, chargeBoxPk)
                .set(OCPP20_VARIABLE.COMPONENT_NAME, componentName)
                .set(OCPP20_VARIABLE.VARIABLE_NAME, variableName)
                .returning(OCPP20_VARIABLE.VARIABLE_PK)
                .fetchOne()
                .getVariablePk();
        }

        ctx.insertInto(OCPP20_VARIABLE_ATTRIBUTE)
            .set(OCPP20_VARIABLE_ATTRIBUTE.VARIABLE_PK, variablePk)
            .set(OCPP20_VARIABLE_ATTRIBUTE.VALUE, value)
            .execute();

        log.debug("Upserted Variable for '{}': component={}, variable={}, value={}",
            chargeBoxId, componentName, variableName, value);
    }

    @Override
    public String getTransactionByRemoteId(String chargeBoxId, String remoteStartId) {
        Integer chargeBoxPk = getChargeBoxPk(chargeBoxId);
        if (chargeBoxPk == null) {
            return null;
        }

        try {
            Integer remoteStartIdInt = Integer.parseInt(remoteStartId);
            return ctx.select(OCPP20_TRANSACTION.TRANSACTION_ID)
                .from(OCPP20_TRANSACTION)
                .where(OCPP20_TRANSACTION.CHARGE_BOX_PK.eq(chargeBoxPk))
                .and(OCPP20_TRANSACTION.REMOTE_START_ID.eq(remoteStartIdInt))
                .and(OCPP20_TRANSACTION.STOPPED_AT.isNull())
                .fetchOne(OCPP20_TRANSACTION.TRANSACTION_ID);
        } catch (NumberFormatException e) {
            log.warn("Invalid remoteStartId format: {}", remoteStartId);
            return null;
        }
    }
}