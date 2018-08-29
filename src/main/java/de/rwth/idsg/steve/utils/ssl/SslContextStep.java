package de.rwth.idsg.steve.utils.ssl;

import javax.net.ssl.SSLContext;
import java.security.GeneralSecurityException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 28.08.2018
 */
public interface SslContextStep {

    SSLContext buildMergedWithSystem() throws GeneralSecurityException;

    default void buildMergedWithSystemAndSetDefault() throws GeneralSecurityException {
        SSLContext.setDefault(buildMergedWithSystem());
    }

}
