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
package de.rwth.idsg.steve.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "steve.gateway.ocpi.currency-conversion", name = "enabled", havingValue = "true")
public class CurrencyConversionService {

    private final String apiUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, ExchangeRateCache> rateCache = new ConcurrentHashMap<>();

    private static final long CACHE_TTL_MS = 3600000;

    public CurrencyConversionService(
        @Value("${steve.gateway.ocpi.currency-conversion.api-url:https://api.exchangerate-api.com/v4/latest/}") String apiUrl,
        @Value("${steve.gateway.ocpi.currency-conversion.api-key:}") String apiKey,
        ObjectMapper objectMapper
    ) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        try {
            BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("Currency conversion failed from {} to {}, returning original amount", fromCurrency, toCurrency, e);
            return amount;
        }
    }

    private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws IOException, InterruptedException {
        String cacheKey = fromCurrency + "_" + toCurrency;
        ExchangeRateCache cached = rateCache.get(cacheKey);

        if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
            log.debug("Using cached exchange rate for {} to {}: {}", fromCurrency, toCurrency, cached.rate);
            return cached.rate;
        }

        log.debug("Fetching exchange rate from API for {} to {}", fromCurrency, toCurrency);
        BigDecimal rate = fetchExchangeRate(fromCurrency, toCurrency);
        rateCache.put(cacheKey, new ExchangeRateCache(rate, System.currentTimeMillis()));
        return rate;
    }

    private BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency) throws IOException, InterruptedException {
        String url = apiUrl + fromCurrency;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .GET();

        if (apiKey != null && !apiKey.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + apiKey);
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Currency API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode ratesNode = root.get("rates");

        if (ratesNode == null || !ratesNode.has(toCurrency)) {
            throw new IOException("Currency " + toCurrency + " not found in API response");
        }

        return new BigDecimal(ratesNode.get(toCurrency).asText());
    }

    private static class ExchangeRateCache {
        final BigDecimal rate;
        final long timestamp;

        ExchangeRateCache(BigDecimal rate, long timestamp) {
            this.rate = rate;
            this.timestamp = timestamp;
        }
    }
}