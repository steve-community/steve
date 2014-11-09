package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 18.09.2014
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CommunicationContext {
    private final String operation;
    private final List<Response> responseList = new ArrayList<>();

    @Getter
    @RequiredArgsConstructor
    public final class Response {
        private final String chargeBoxId;
        private final String response;
    }
}