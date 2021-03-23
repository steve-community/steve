package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.Address;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import jooq.steve.db.tables.records.ChargeBoxRecord;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
public class ChargePointDetailsMapper {

    public static ChargePointForm mapToForm(ChargePoint.Details cp) {
        ChargeBoxRecord chargeBox = cp.getChargeBox();

        ChargePointForm form = new ChargePointForm();
        form.setChargeBoxPk(chargeBox.getChargeBoxPk());
        form.setChargeBoxId(chargeBox.getChargeBoxId());
        form.setNote(chargeBox.getNote());
        form.setDescription(chargeBox.getDescription());
        form.setLocationLatitude(chargeBox.getLocationLatitude());
        form.setLocationLongitude(chargeBox.getLocationLongitude());
        form.setInsertConnectorStatusAfterTransactionMsg(chargeBox.getInsertConnectorStatusAfterTransactionMsg());
        form.setAdminAddress(chargeBox.getAdminAddress());
        form.setRegistrationStatus(chargeBox.getRegistrationStatus());
        form.setAddress(AddressMapper.recordToDto(cp.getAddress()));

        return form;
    }

}
