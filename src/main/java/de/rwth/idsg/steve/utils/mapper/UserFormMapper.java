package de.rwth.idsg.steve.utils.mapper;

import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.utils.ControllerHelper;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserSex;
import jooq.steve.db.tables.records.UserRecord;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
public class UserFormMapper {

    public static UserForm toForm(User.Details details) {
        UserRecord userRecord = details.getUserRecord();

        UserForm form = new UserForm();
        form.setUserPk(userRecord.getUserPk());
        form.setFirstName(userRecord.getFirstName());
        form.setLastName(userRecord.getLastName());
        form.setBirthDay(userRecord.getBirthDay());
        form.setPhone(userRecord.getPhone());
        form.setSex(UserSex.fromDatabaseValue(userRecord.getSex()));
        form.setEMail(userRecord.getEMail());
        form.setNote(userRecord.getNote());
        form.setAddress(ControllerHelper.recordToDto(details.getAddress()));
        form.setOcppIdTag(details.getOcppIdTag().orElse(ControllerHelper.EMPTY_OPTION));

        return form;
    }
}
