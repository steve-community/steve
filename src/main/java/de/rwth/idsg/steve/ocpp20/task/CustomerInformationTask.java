package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.CustomerInformationRequest;
import de.rwth.idsg.steve.ocpp20.model.CustomerInformationResponse;
import de.rwth.idsg.steve.ocpp20.model.IdToken;
import de.rwth.idsg.steve.ocpp20.model.IdTokenEnum;
import lombok.Getter;
import java.util.List;

@Getter
public class CustomerInformationTask extends Ocpp20Task<CustomerInformationRequest, CustomerInformationResponse> {

    private final Integer requestId;
    private final Boolean report;
    private final Boolean clear;
    private final String customerIdentifier;

    public CustomerInformationTask(List<String> chargeBoxIdList, Integer requestId,
                                   Boolean report, Boolean clear, String customerIdentifier) {
        super("CustomerInformation", chargeBoxIdList);
        this.requestId = requestId;
        this.report = report;
        this.clear = clear;
        this.customerIdentifier = customerIdentifier;
    }

    @Override
    public CustomerInformationRequest createRequest() {
        CustomerInformationRequest request = new CustomerInformationRequest();
        request.setRequestId(requestId);
        request.setReport(report != null ? report : true);
        request.setClear(clear != null ? clear : false);

        if (customerIdentifier != null) {
            IdToken idToken = new IdToken();
            idToken.setIdToken(customerIdentifier);
            idToken.setType(IdTokenEnum.ISO_14443);
            request.setIdToken(idToken);
        }

        return request;
    }

    @Override
    public Class<CustomerInformationResponse> getResponseClass() {
        return CustomerInformationResponse.class;
    }
}
