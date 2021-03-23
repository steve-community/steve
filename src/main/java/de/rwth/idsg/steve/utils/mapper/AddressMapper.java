package de.rwth.idsg.steve.utils.mapper;

import com.neovisionaries.i18n.CountryCode;
import de.rwth.idsg.steve.web.dto.Address;
import jooq.steve.db.tables.records.AddressRecord;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
public class AddressMapper {

    public static Address recordToDto(AddressRecord record) {
        Address address = new Address();
        if (record != null) {
            address.setAddressPk(record.getAddressPk());
            address.setStreet(record.getStreet());
            address.setHouseNumber(record.getHouseNumber());
            address.setZipCode(record.getZipCode());
            address.setCity(record.getCity());
            address.setCountry(CountryCode.getByCode(record.getCountry()));
        }
        return address;
    }
}
