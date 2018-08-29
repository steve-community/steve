package de.rwth.idsg.steve.utils.ssl;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.08.2018
 */
public interface KeyManagerAlgorithmStep {

    /**
     * @param keyManagerAlgorithm The algorithm for the custom key store. Defaults to system one, if null.
     */
    KeyManagerPasswordStep usingAlgorithm(String keyManagerAlgorithm);

    default KeyManagerPasswordStep usingDefaultAlgorithm() {
        return usingAlgorithm(null);
    }

    default KeyManagerPasswordStep usingSunX509() {
        return usingAlgorithm("SunX509");
    }
}
