package de.rwth.idsg.steve.handler.ocpp15;

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.handler.AbstractOcppResponseHandler;
import de.rwth.idsg.steve.web.RequestTask;
import ocpp.cp._2012._06.GetConfigurationResponse;
import ocpp.cp._2012._06.KeyValue;

import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
public class GetConfigurationResponseHandler extends AbstractOcppResponseHandler<GetConfigurationResponse> {

    private static final String FORMAT =
            "<b>Known keys:</b>"
                    + "<br>"
                    + "%s"
                    + "<br>"
                    + "<b>Unknown keys:</b>"
                    + "<br>"
                    + "%s";

    private static final Joiner JOINER = Joiner.on(",");

    public GetConfigurationResponseHandler(RequestTask requestTask, String chargeBoxId) {
        super(requestTask, chargeBoxId);
    }

    @Override
    public void handleResult(GetConfigurationResponse response) {
        String str = String.format(
                FORMAT,
                toStringConfList(response.getConfigurationKey()),
                toStringUnknownList(response.getUnknownKey())
        );

        requestTask.addNewResponse(chargeBoxId, str);
    }

    private String toStringConfList(List<KeyValue> confList) {
        StringBuilder sb = new StringBuilder();

        for (KeyValue keyValue : confList) {
            sb.append(keyValue.getKey())
              .append(": ")
              .append(keyValue.getValue());

            if (keyValue.isReadonly()) {
                sb.append(" (read-only)");
            }

            sb.append("<br>");
        }

        return sb.toString();
    }

    private String toStringUnknownList(List<String> unknownList) {
        return JOINER.join(unknownList);
    }
}
