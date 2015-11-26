package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
public final class ControllerHelper {
    private ControllerHelper() { }

    public static final String EMPTY_OPTION = "-- Empty --";

    public static Address recordToDto(AddressRecord record) {
        Address address = new Address();
        if (record != null) {
            address.setAddressPk(record.getAddressPk());
            address.setStreetAndHouseNumber(record.getStreetAndHouseNumber());
            address.setZipCode(record.getZipCode());
            address.setCity(record.getCity());
            address.setCountry(record.getCountry());
        }
        return address;
    }

    public static List<String> idTagEnhancer(List<String> idTagList) {
        idTagList.add(EMPTY_OPTION);
        return idTagList;
    }
}
