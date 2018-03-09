package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public interface ChargePointSelection {
    List<ChargePointSelect> getChargePointSelectList();
}
