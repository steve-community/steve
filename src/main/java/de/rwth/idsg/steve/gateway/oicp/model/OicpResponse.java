package de.rwth.idsg.steve.gateway.oicp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OicpResponse<T> {
    private T data;
    private Boolean result;
    private String statusCode;
    private String statusMessage;
    
    public static <T> OicpResponse<T> success(T data) {
        return OicpResponse.<T>builder()
            .data(data)
            .result(true)
            .statusCode("000")
            .statusMessage("Success")
            .build();
    }

    public static <T> OicpResponse<T> error(String statusCode, String message) {
        return OicpResponse.<T>builder()
            .result(false)
            .statusCode(statusCode)
            .statusMessage(message)
            .build();
    }
}
