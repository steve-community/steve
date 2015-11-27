package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> idTagEnhancer(List<String> idTagList) {
        Map<String, String> map = new HashMap<>(idTagList.size());
        map.put("", EMPTY_OPTION);

        for (String s : idTagList) {
            map.put(s, s);
        }
        return map;
    }
}
