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
package de.rwth.idsg.steve.gateway.repository.impl;

import de.rwth.idsg.steve.gateway.repository.GatewayPartnerRepository;
import de.rwth.idsg.steve.gateway.security.TokenEncryptionService;
import de.rwth.idsg.steve.web.dto.GatewayPartnerForm;
import jooq.steve.db.enums.GatewayPartnerProtocol;
import jooq.steve.db.enums.GatewayPartnerRole;
import jooq.steve.db.tables.records.GatewayPartnerRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jooq.steve.db.tables.GatewayPartner.GATEWAY_PARTNER;

@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
public class GatewayPartnerRepositoryImpl implements GatewayPartnerRepository {

    private final DSLContext ctx;
    private final TokenEncryptionService tokenEncryptionService;

    @Override
    public Optional<GatewayPartnerRecord> findByToken(String token) {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.TOKEN.eq(token))
            .fetchOptional();
    }

    @Override
    public List<GatewayPartnerRecord> findByProtocolAndEnabled(GatewayPartnerProtocol protocol, Boolean enabled) {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.PROTOCOL.eq(protocol))
            .and(GATEWAY_PARTNER.ENABLED.eq(enabled))
            .fetch();
    }

    @Override
    public Optional<GatewayPartnerRecord> findByProtocolAndPartyIdAndCountryCode(
        GatewayPartnerProtocol protocol,
        String partyId,
        String countryCode
    ) {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.PROTOCOL.eq(protocol))
            .and(GATEWAY_PARTNER.PARTY_ID.eq(partyId))
            .and(GATEWAY_PARTNER.COUNTRY_CODE.eq(countryCode))
            .fetchOptional();
    }

    @Override
    public List<GatewayPartnerRecord> findByEnabledTrue() {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.ENABLED.eq(true))
            .fetch();
    }

    @Override
    public List<GatewayPartnerRecord> getPartners() {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .orderBy(GATEWAY_PARTNER.NAME.asc())
            .fetch();
    }

    @Override
    public GatewayPartnerRecord getPartner(Integer id) {
        return ctx.selectFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.ID.eq(id))
            .fetchOne();
    }

    @Override
    public void addPartner(GatewayPartnerForm form) {
        String hashedToken = tokenEncryptionService.hashToken(form.getToken());

        ctx.insertInto(GATEWAY_PARTNER)
            .set(GATEWAY_PARTNER.NAME, form.getName())
            .set(GATEWAY_PARTNER.PROTOCOL, GatewayPartnerProtocol.valueOf(form.getProtocol()))
            .set(GATEWAY_PARTNER.PARTY_ID, form.getPartyId())
            .set(GATEWAY_PARTNER.COUNTRY_CODE, form.getCountryCode())
            .set(GATEWAY_PARTNER.ENDPOINT_URL, form.getEndpointUrl())
            .set(GATEWAY_PARTNER.TOKEN_HASH, hashedToken)
            .set(GATEWAY_PARTNER.ENABLED, form.getEnabled())
            .set(GATEWAY_PARTNER.ROLE, GatewayPartnerRole.valueOf(form.getRole()))
            .execute();

        log.info("Added gateway partner: {} ({})", form.getName(), form.getProtocol());
    }

    @Override
    public void updatePartner(GatewayPartnerForm form) {
        var update = ctx.update(GATEWAY_PARTNER)
            .set(GATEWAY_PARTNER.NAME, form.getName())
            .set(GATEWAY_PARTNER.PROTOCOL, GatewayPartnerProtocol.valueOf(form.getProtocol()))
            .set(GATEWAY_PARTNER.PARTY_ID, form.getPartyId())
            .set(GATEWAY_PARTNER.COUNTRY_CODE, form.getCountryCode())
            .set(GATEWAY_PARTNER.ENDPOINT_URL, form.getEndpointUrl())
            .set(GATEWAY_PARTNER.ENABLED, form.getEnabled())
            .set(GATEWAY_PARTNER.ROLE, GatewayPartnerRole.valueOf(form.getRole()));

        if (form.getToken() != null && !form.getToken().isBlank()) {
            String hashedToken = tokenEncryptionService.hashToken(form.getToken());
            update.set(GATEWAY_PARTNER.TOKEN_HASH, hashedToken);
            log.info("Updated gateway partner token: {} (ID: {})", form.getName(), form.getId());
        }

        update.where(GATEWAY_PARTNER.ID.eq(form.getId()))
            .execute();

        log.info("Updated gateway partner: {} (ID: {})", form.getName(), form.getId());
    }

    @Override
    public void deletePartner(Integer id) {
        ctx.deleteFrom(GATEWAY_PARTNER)
            .where(GATEWAY_PARTNER.ID.eq(id))
            .execute();
    }
}