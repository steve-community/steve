package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.OcppTagForm;
import jooq.steve.db.tables.records.OcppTagActivityRecord;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
public class OcppTagFormMapper {

    public static OcppTagForm toForm(OcppTagActivityRecord record) {
        OcppTagForm form = new OcppTagForm();
        form.setOcppTagPk(record.getOcppTagPk());
        form.setIdTag(record.getIdTag());

        DateTime expiryDate = record.getExpiryDate();
        if (expiryDate != null) {
            form.setExpiration(expiryDate.toLocalDateTime());
        }

        form.setMaxActiveTransactionCount(record.getMaxActiveTransactionCount());
        form.setNote(record.getNote());

        String parentIdTag = record.getParentIdTag();
        if (parentIdTag == null) {
            parentIdTag = ControllerHelper.EMPTY_OPTION;
        }
        form.setParentIdTag(parentIdTag);

        return form;
    }
}
