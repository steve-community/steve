package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2012._06.UpdateType;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@Setter
@Getter
public class SendLocalListParams extends MultipleChargePointSelect {

    @NotNull(message = "List version is required")
    private Integer listVersion;

    @NotNull(message = "Update Type is required")
    private UpdateType updateType = UpdateType.FULL;

    private List<String> deleteList;
    private List<String> addUpdateList;

    @AssertTrue(message = "When Update Type is DIFFERENTIAL, either Add/Update or Delete list should not be empty")
    public boolean isValidWhenDifferential() {
        return UpdateType.FULL.equals(updateType) || !getDeleteList().isEmpty() || !getAddUpdateList().isEmpty();
    }

    @AssertTrue(message = "The Add/Update and Delete lists should have no elements in ocpp")
    public boolean isDisjoint() {
        return Collections.disjoint(getDeleteList(), getAddUpdateList());
    }

    public List<String> getDeleteList() {
        if (deleteList == null) {
            deleteList = new ArrayList<>();
        }
        return deleteList;
    }

    public List<String> getAddUpdateList() {
        if (addUpdateList == null) {
            addUpdateList = new ArrayList<>();
        }
        return addUpdateList;
    }
}
