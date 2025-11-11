package de.rwth.idsg.steve.web.validation;

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class SecurityProfileValidator implements ConstraintValidator<SecurityProfileValid, ChargePointForm> {

    private final ChargePointRepository chargePointRepository;

    @Override
    public boolean isValid(ChargePointForm form, ConstraintValidatorContext context) {
        OcppSecurityProfile securityProfile = form.getSecurityProfile();
        String newAuthPassword = form.getAuthPassword();

        if (!securityProfile.requiresBasicAuth()) {
            return true;
        }

        // from here on: station with profile 1 or 2

        // let's handle the easiest possibility first
        boolean hasNewPassword = StringUtils.hasText(newAuthPassword);
        if (hasNewPassword) {
            return true;
        }

        // from here on: station with profile 1 or 2 zone without Auth pwd in form

        // we do not require it to be set, if the DB already has it.
        var optionalRegistration = chargePointRepository.getRegistration(form.getChargeBoxId());

        // must be new station without pwd
        if (optionalRegistration.isEmpty()) {
            return false;
        }

        // from here on: existing station with profile 1 or 2 without Auth pwd in form

        // all is good, if we have pwd in DB
        String pwd = optionalRegistration.get().hashedAuthPassword();
        return StringUtils.hasText(pwd);
    }
}
