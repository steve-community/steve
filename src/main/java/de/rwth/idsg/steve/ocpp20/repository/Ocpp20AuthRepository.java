package de.rwth.idsg.steve.ocpp20.repository;

import de.rwth.idsg.steve.ocpp20.security.Ocpp20AuthenticationConfig;
import java.util.List;

public interface Ocpp20AuthRepository {

    // Trusted Networks
    List<TrustedNetwork> getTrustedNetworks(boolean enabledOnly);
    void addTrustedNetwork(String cidr, String description);
    void removeTrustedNetwork(String cidr);
    void updateTrustedNetwork(String cidr, String description, boolean enabled);

    // IP Whitelist
    List<IPWhitelistEntry> getIPWhitelist(boolean enabledOnly);
    void addIPWhitelist(String pattern, String description);
    void removeIPWhitelist(String pattern);
    void updateIPWhitelist(String pattern, String description, boolean enabled);

    // IP Blacklist
    List<IPBlacklistEntry> getIPBlacklist(boolean enabledOnly);
    void addIPBlacklist(String pattern, String description, String reason);
    void removeIPBlacklist(String pattern);
    void updateIPBlacklist(String pattern, String description, String reason, boolean enabled);

    // Client Certificates
    List<ClientCertificate> getClientCertificates(String chargeBoxId);
    void addClientCertificate(ClientCertificate certificate);
    void revokeClientCertificate(String thumbprint);
    ClientCertificate getClientCertificateByThumbprint(String thumbprint);

    // Auth Settings
    Ocpp20AuthenticationConfig.AuthMode getAuthMode();
    void setAuthMode(Ocpp20AuthenticationConfig.AuthMode mode);
    boolean isAllowNoAuth();
    void setAllowNoAuth(boolean allow);
    boolean isRequireCertificate();
    void setRequireCertificate(boolean require);
    boolean isValidateCertChain();
    void setValidateCertChain(boolean validate);
    boolean isTrustedNetworkAuthBypass();
    void setTrustedNetworkAuthBypass(boolean bypass);

    // DTO Classes
    class TrustedNetwork {
        public int networkId;
        public String networkCidr;
        public String description;
        public boolean enabled;

        public TrustedNetwork(int networkId, String networkCidr, String description, boolean enabled) {
            this.networkId = networkId;
            this.networkCidr = networkCidr;
            this.description = description;
            this.enabled = enabled;
        }
    }

    class IPWhitelistEntry {
        public int whitelistId;
        public String ipPattern;
        public String description;
        public boolean enabled;

        public IPWhitelistEntry(int whitelistId, String ipPattern, String description, boolean enabled) {
            this.whitelistId = whitelistId;
            this.ipPattern = ipPattern;
            this.description = description;
            this.enabled = enabled;
        }
    }

    class IPBlacklistEntry {
        public int blacklistId;
        public String ipPattern;
        public String description;
        public String reason;
        public boolean enabled;

        public IPBlacklistEntry(int blacklistId, String ipPattern, String description, String reason, boolean enabled) {
            this.blacklistId = blacklistId;
            this.ipPattern = ipPattern;
            this.description = description;
            this.reason = reason;
            this.enabled = enabled;
        }
    }

    class ClientCertificate {
        public int certId;
        public String chargeBoxId;
        public String certificateDn;
        public String certificateSerial;
        public String certificateThumbprint;
        public String issuerDn;
        public String status;

        public ClientCertificate(String chargeBoxId, String certificateDn, String certificateSerial,
                                String certificateThumbprint, String issuerDn, String status) {
            this.chargeBoxId = chargeBoxId;
            this.certificateDn = certificateDn;
            this.certificateSerial = certificateSerial;
            this.certificateThumbprint = certificateThumbprint;
            this.issuerDn = issuerDn;
            this.status = status;
        }
    }
}