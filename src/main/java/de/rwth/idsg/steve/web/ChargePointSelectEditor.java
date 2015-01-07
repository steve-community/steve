package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

import java.beans.PropertyEditorSupport;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.12.2014
 */
public class ChargePointSelectEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            String[] chargePointItem = text.split(";");

            // chargePointItem[0] : chargebox id
            // chargePointItem[1] : endpoint (IP) address
            ChargePointSelect cps = ChargePointSelect.builder()
                    .chargeBoxId(chargePointItem[0])
                    .endpointAddress(chargePointItem[1])
                    .build();

            setValue(cps);
        }
    }
}
