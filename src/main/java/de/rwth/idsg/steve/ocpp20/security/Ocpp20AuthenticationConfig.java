package de.rwth.idsg.steve.ocpp20.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@ConfigurationProperties(prefix = "ocpp.v20.auth")
@Getter
@Setter
public class Ocpp20AuthenticationConfig {

    public enum AuthMode {
        NONE,           // No authentication required
        BASIC,          // Username/password authentication
        CERTIFICATE,    // Client certificate authentication
        COMBINED        // Both basic and certificate
    }

    private AuthMode mode = AuthMode.BASIC;
    private boolean allowNoAuth = false;
    private boolean requireCertificate = false;
    private boolean validateCertificateChain = true;

    // Trusted networks (CIDR notation)
    private List<String> trustedNetworks = new ArrayList<>();

    // Trusted certificate DNs
    private Set<String> trustedCertificateDNs = new HashSet<>();

    // IP whitelist
    private List<String> ipWhitelist = new ArrayList<>();

    // IP blacklist
    private List<String> ipBlacklist = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.info("OCPP 2.0 Authentication Configuration:");
        log.info("  Mode: {}", mode);
        log.info("  Allow No Auth: {}", allowNoAuth);
        log.info("  Require Certificate: {}", requireCertificate);
        log.info("  Trusted Networks: {}", trustedNetworks.size());
        log.info("  IP Whitelist: {}", ipWhitelist.size());
        log.info("  IP Blacklist: {}", ipBlacklist.size());
    }

    public boolean isTrustedNetwork(String ipAddress) {
        if (trustedNetworks.isEmpty()) {
            return false;
        }

        for (String network : trustedNetworks) {
            if (isInNetwork(ipAddress, network)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWhitelisted(String ipAddress) {
        if (ipWhitelist.isEmpty()) {
            return true; // No whitelist means all allowed
        }

        for (String pattern : ipWhitelist) {
            if (matchesPattern(ipAddress, pattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlacklisted(String ipAddress) {
        for (String pattern : ipBlacklist) {
            if (matchesPattern(ipAddress, pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInNetwork(String ipAddress, String cidr) {
        try {
            if (!cidr.contains("/")) {
                return ipAddress.equals(cidr);
            }

            String[] parts = cidr.split("/");
            String networkAddr = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            long ipLong = ipToLong(ipAddress);
            long networkLong = ipToLong(networkAddr);
            long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            log.warn("Invalid CIDR notation: {}", cidr);
            return false;
        }
    }

    private boolean matchesPattern(String ipAddress, String pattern) {
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return ipAddress.matches(regex);
        }
        return ipAddress.equals(pattern);
    }

    private long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress);
        }

        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | Integer.parseInt(octets[i]);
        }
        return result;
    }
}