CREATE INDEX idx_gateway_partner_protocol_enabled ON gateway_partner(protocol, enabled);

CREATE INDEX idx_gateway_partner_party ON gateway_partner(protocol, party_id, country_code);