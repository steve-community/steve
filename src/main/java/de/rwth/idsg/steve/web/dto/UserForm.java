package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 25.11.2015
 */
@Getter
@Setter
public class UserForm {

    // Internal database id
    private Integer userPk;

    private String ocppIdTag;

    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String phone;
    private String note;

    @NotNull(message = "Sex is required")
    private UserSex sex;

    @Email(message = "Not a valid e-mail address")
    private String eMail;

    private Address address;

}
