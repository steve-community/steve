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

import java.time.Instant;

/**
 * Common interface for all events sent via webhook to VoltStartEV backend.
 */
public interface SteveWebhookEvent {
    String getChargeBoxId();
    Integer getConnectorId();
    Instant getEventTimestamp();  // OCPP timestamp from charger
    Instant getReceivedAt();      // When SteVe received the message
}
