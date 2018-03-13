package de.rwth.idsg.steve.handler;

/**
 * We need a mechanism to execute additional arbitrary logic, which _can_ be provided by the call site,
 * that acts on the response or the error.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.11.2015
 */
public interface OcppCallback<T> {
    void success(T response);
    void failed(String errorMessage);
}
