package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.ReleaseReport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.04.2016
 */
public interface ReleaseCheckService {
    ReleaseReport check();
}
