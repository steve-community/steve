package de.rwth.idsg.steve.utils.ssl;

import javax.net.ssl.KeyManagerFactory;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.08.2018
 */
public interface KeyManagerPasswordStep {

    /**
     * @param keyManagerPwd The password to init {@link KeyManagerFactory}. Defaults to KeyStore password, if null.
     */
    SslContextStep usingKeyManagerPassword(String keyManagerPwd);

    default SslContextStep usingKeyManagerPasswordFromKeyStore() {
        return usingKeyManagerPassword(null);
    }

}
