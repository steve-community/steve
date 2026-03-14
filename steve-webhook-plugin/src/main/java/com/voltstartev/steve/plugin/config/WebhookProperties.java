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
package com.voltstartev.steve.plugin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "voltstartev.webhook")
public class WebhookProperties {
    private String url = "http://localhost:3000/api/webhooks/steve";
    private String secret = "";  // Shared secret for HMAC signature
    private int connectTimeout = 3000;
    private int readTimeout = 5000;
    private int maxConnections = 50;
    private int maxConnectionsPerRoute = 20;
    private int retryMaxAttempts = 3;
    private long retryInitialDelayMs = 500;
    private boolean meterValuesSamplingEnabled = true;
    private int meterValuesMinEnergyDeltaWh = 100;  // Only send if energy changed by >100Wh
    private int meterValuesMinIntervalSeconds = 30;  // Or if >30s since last send
}
