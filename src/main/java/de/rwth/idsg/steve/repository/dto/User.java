package de.rwth.idsg.steve.repository.dto;

import com.google.common.base.Optional;
import jooq.steve.db.tables.records.AddressRecord;
import jooq.steve.db.tables.records.UserRecord;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public class User {

    @Getter
    @Builder
    public static final class Overview {
        private final String userPk, ocppIdTag, name, phone, email;
    }

    @Getter
    @Builder
    public static final class Details {
        private final UserRecord userRecord;
        private final AddressRecord address;
        private Optional<String> ocppIdTag;
    }
}
