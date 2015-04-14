package de.rwth.idsg.steve.repository;

import de.rwth.idsg.steve.repository.dto.DbVersion;
import de.rwth.idsg.steve.web.dto.Statistics;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 19.08.2014
 */
public interface GenericRepository {
    Statistics getStats();

    /**
     * Returns database version of SteVe and last database update timestamp
     *
     */
    DbVersion getDBVersion();
}
