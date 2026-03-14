/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
 * Copyright (C) 2026 VoltStar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.voltstartev.steve.plugin.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStartedEvent implements SteveWebhookEvent {
    private String chargeBoxId;
    private Integer connectorId;
    private Integer transactionId;  // SteVe transaction_pk
    private String idTag;
    private Integer meterStart;  // Wh
    private Instant eventTimestamp;
    private Instant receivedAt;
    private Integer reservationId;
    
    @Override
    public String getChargeBoxId() { return chargeBoxId; }
    
    @Override
    public Integer getConnectorId() { return connectorId; }
    
    @Override
    public Instant getEventTimestamp() { return eventTimestamp; }
    
    @Override
    public Instant getReceivedAt() { return receivedAt; }
}
