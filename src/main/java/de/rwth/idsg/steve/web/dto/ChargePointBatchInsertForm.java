package de.rwth.idsg.steve.web.dto;

import de.rwth.idsg.steve.web.validation.ChargeBoxId;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.06.2016
 */
@Getter
@Setter
public class ChargePointBatchInsertForm {

    @NotEmpty
    @ChargeBoxId
    private List<String> idList;
}
