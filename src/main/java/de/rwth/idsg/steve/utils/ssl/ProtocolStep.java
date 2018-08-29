package de.rwth.idsg.steve.utils.ssl;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.08.2018
 */
public interface ProtocolStep {

    KeyManagerAlgorithmStep usingProtocol(String socketProtocol);

    default KeyManagerAlgorithmStep usingTLS() {
        return usingProtocol("TLS");
    }
}
