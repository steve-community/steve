package de.rwth.idsg.steve.web.validation;

import de.rwth.idsg.steve.ocpp.OcppSecurityProfile;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointRegistration;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SecurityProfileValidatorTest {

    @Mock
    private ChargePointRepository chargePointRepository;

    @InjectMocks
    private SecurityProfileValidator securityProfileValidator;

    @ParameterizedTest
    @CsvSource(
        useHeadersInDisplayName = true,
        nullValues = {"null"},
        textBlock = """
            securityProfileNumber,  authPassword,   stationExistsInDB,  authPasswordInDB,   isValid
            0,                      null,           false,              null,               true
            0,                      password123,    false,              null,               true
            0,                      null,           true,               null,               true
            0,                      null,           true,               oldPass,            true
            1,                      password123,    false,              null,               true
            1,                      null,           false,              null,               false
            1,                      password123,    true,               oldPass,            true
            1,                      null,           true,               oldPass,            true
            1,                      null,           true,               null,               false
            2,                      password123,    false,              null,               true
            2,                      null,           false,              null,               false
            2,                      password123,    true,               oldPass,            true
            2,                      null,           true,               oldPass,            true
            2,                      null,           true,               null,               false
            3,                      null,           false,              null,               true
            3,                      password123,    false,              null,               true
            3,                      null,           true,               null,               true
            3,                      null,           true,               oldPass,            true
            """)
    public void testInputValidation(int securityProfileNumber,
                                    String authPassword,
                                    boolean stationExistsInDB,
                                    String authPasswordInDB,
                                    boolean isValid) {
        ChargePointForm form = new ChargePointForm();
        form.setSecurityProfile(OcppSecurityProfile.fromValue(securityProfileNumber));
        form.setAuthPassword(authPassword);

        ChargePointRegistration toReturn = stationExistsInDB
            ? new ChargePointRegistration("Accepted", authPasswordInDB)
            : null;

        when(chargePointRepository.getRegistration(any())).thenReturn(Optional.ofNullable(toReturn));

        boolean result = securityProfileValidator.isValid(form, null);
        Assertions.assertEquals(isValid, result);
    }

}
