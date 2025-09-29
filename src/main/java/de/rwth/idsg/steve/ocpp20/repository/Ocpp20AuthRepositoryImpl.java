package de.rwth.idsg.steve.ocpp20.repository;

import de.rwth.idsg.steve.ocpp20.security.Ocpp20AuthenticationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static jooq.steve.db.Tables.*;

@Slf4j
@Repository
@ConditionalOnProperty(name = "ocpp.v20.enabled", havingValue = "true")
@RequiredArgsConstructor
public class Ocpp20AuthRepositoryImpl implements Ocpp20AuthRepository {

    private final DSLContext ctx;

    // Trusted Networks
    @Override
    public List<TrustedNetwork> getTrustedNetworks(boolean enabledOnly) {
        // For now, return hardcoded trusted networks since DB migration is pending
        List<TrustedNetwork> networks = new ArrayList<>();
        networks.add(new TrustedNetwork(1, "127.0.0.1/32", "Localhost", true));
        networks.add(new TrustedNetwork(2, "::1/128", "IPv6 Localhost", true));

        if (!enabledOnly) {
            networks.add(new TrustedNetwork(3, "10.0.0.0/8", "Private Network Class A", false));
            networks.add(new TrustedNetwork(4, "172.16.0.0/12", "Private Network Class B", false));
            networks.add(new TrustedNetwork(5, "192.168.0.0/16", "Private Network Class C", false));
        }

        return networks;
    }

    @Override
    public void addTrustedNetwork(String cidr, String description) {
        log.info("Adding trusted network: {} - {}", cidr, description);
        // Implementation pending DB migration
    }

    @Override
    public void removeTrustedNetwork(String cidr) {
        log.info("Removing trusted network: {}", cidr);
        // Implementation pending DB migration
    }

    @Override
    public void updateTrustedNetwork(String cidr, String description, boolean enabled) {
        log.info("Updating trusted network: {} - {} - {}", cidr, description, enabled);
        // Implementation pending DB migration
    }

    // IP Whitelist
    @Override
    public List<IPWhitelistEntry> getIPWhitelist(boolean enabledOnly) {
        // Return empty list for now
        return new ArrayList<>();
    }

    @Override
    public void addIPWhitelist(String pattern, String description) {
        log.info("Adding IP whitelist: {} - {}", pattern, description);
        // Implementation pending DB migration
    }

    @Override
    public void removeIPWhitelist(String pattern) {
        log.info("Removing IP whitelist: {}", pattern);
        // Implementation pending DB migration
    }

    @Override
    public void updateIPWhitelist(String pattern, String description, boolean enabled) {
        log.info("Updating IP whitelist: {} - {} - {}", pattern, description, enabled);
        // Implementation pending DB migration
    }

    // IP Blacklist
    @Override
    public List<IPBlacklistEntry> getIPBlacklist(boolean enabledOnly) {
        // Return empty list for now
        return new ArrayList<>();
    }

    @Override
    public void addIPBlacklist(String pattern, String description, String reason) {
        log.info("Adding IP blacklist: {} - {} - {}", pattern, description, reason);
        // Implementation pending DB migration
    }

    @Override
    public void removeIPBlacklist(String pattern) {
        log.info("Removing IP blacklist: {}", pattern);
        // Implementation pending DB migration
    }

    @Override
    public void updateIPBlacklist(String pattern, String description, String reason, boolean enabled) {
        log.info("Updating IP blacklist: {} - {} - {} - {}", pattern, description, reason, enabled);
        // Implementation pending DB migration
    }

    // Client Certificates
    @Override
    public List<ClientCertificate> getClientCertificates(String chargeBoxId) {
        // Return empty list for now
        return new ArrayList<>();
    }

    @Override
    public void addClientCertificate(ClientCertificate certificate) {
        log.info("Adding client certificate for charge box: {}", certificate.chargeBoxId);
        // Implementation pending DB migration
    }

    @Override
    public void revokeClientCertificate(String thumbprint) {
        log.info("Revoking client certificate: {}", thumbprint);
        // Implementation pending DB migration
    }

    @Override
    public ClientCertificate getClientCertificateByThumbprint(String thumbprint) {
        // Return null for now
        return null;
    }

    // Auth Settings - Using in-memory defaults for now
    private Ocpp20AuthenticationConfig.AuthMode authMode = Ocpp20AuthenticationConfig.AuthMode.BASIC;
    private boolean allowNoAuth = false;
    private boolean requireCertificate = false;
    private boolean validateCertChain = true;
    private boolean trustedNetworkAuthBypass = true;

    @Override
    public Ocpp20AuthenticationConfig.AuthMode getAuthMode() {
        return authMode;
    }

    @Override
    public void setAuthMode(Ocpp20AuthenticationConfig.AuthMode mode) {
        this.authMode = mode;
        log.info("Auth mode set to: {}", mode);
    }

    @Override
    public boolean isAllowNoAuth() {
        return allowNoAuth;
    }

    @Override
    public void setAllowNoAuth(boolean allow) {
        this.allowNoAuth = allow;
        log.info("Allow no auth set to: {}", allow);
    }

    @Override
    public boolean isRequireCertificate() {
        return requireCertificate;
    }

    @Override
    public void setRequireCertificate(boolean require) {
        this.requireCertificate = require;
        log.info("Require certificate set to: {}", require);
    }

    @Override
    public boolean isValidateCertChain() {
        return validateCertChain;
    }

    @Override
    public void setValidateCertChain(boolean validate) {
        this.validateCertChain = validate;
        log.info("Validate cert chain set to: {}", validate);
    }

    @Override
    public boolean isTrustedNetworkAuthBypass() {
        return trustedNetworkAuthBypass;
    }

    @Override
    public void setTrustedNetworkAuthBypass(boolean bypass) {
        this.trustedNetworkAuthBypass = bypass;
        log.info("Trusted network auth bypass set to: {}", bypass);
    }
}