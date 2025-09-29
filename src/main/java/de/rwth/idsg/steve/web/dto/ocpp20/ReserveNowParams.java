package de.rwth.idsg.steve.web.dto.ocpp20;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReserveNowParams extends BaseParams {

    @NotNull(message = "Reservation ID is required")
    private Integer id;

    @NotNull(message = "Expiry date/time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiryDateTime;

    @NotBlank(message = "ID Token is required")
    private String idToken;

    @NotBlank(message = "ID Token type is required")
    private String idTokenType;

    private Integer evseId;

    private String groupIdToken;
}