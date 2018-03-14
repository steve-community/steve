package de.rwth.idsg.steve;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 05.11.2015
 */
public interface ApplicationStarter {
    void start() throws Exception;
    void join() throws Exception;
    void stop() throws Exception;
}
