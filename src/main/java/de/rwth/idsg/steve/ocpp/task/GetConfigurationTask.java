package de.rwth.idsg.steve.ocpp.task;

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.handler.OcppCallback;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.ocpp.ResponseType;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetConfigurationResponse;
import ocpp.cp._2012._06.KeyValue;

import javax.xml.ws.AsyncHandler;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class GetConfigurationTask extends CommunicationTask<GetConfigurationParams, GetConfigurationResponse> {

    private static final String FORMAT =
            "<b>Known keys:</b>"
                    + "<br>"
                    + "%s"
                    + "<br>"
                    + "<b>Unknown keys:</b>"
                    + "<br>"
                    + "%s";

    private static final Joiner JOINER = Joiner.on(",");

    public GetConfigurationTask(OcppVersion ocppVersion, GetConfigurationParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<GetConfigurationResponse> defaultCallback() {
        return new DefaultOcppCallback<GetConfigurationResponse>() {
            @Override
            public void success(String chargeBoxId, GetConfigurationResponse response) {
                String str = String.format(
                        FORMAT,
                        toStringConfList(response.getConfigurationKey()),
                        toStringUnknownList(response.getUnknownKey())
                );

                addNewResponse(chargeBoxId, str);
            }
        };
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public GetConfigurationRequest getOcpp15Request() {
        if (params.isSetConfKeyList()) {
            return new GetConfigurationRequest().withKey(params.getConfKeyList());
        } else {
            return new GetConfigurationRequest();
        }
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public AsyncHandler<GetConfigurationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    private static String toStringConfList(List<KeyValue> confList) {
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

    private static String toStringUnknownList(List<String> unknownList) {
        return JOINER.join(unknownList);
    }
 }
