package de.rwth.idsg.steve.ocpp.task;

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.ocpp.Ocpp15AndAboveTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetConfigurationResponse;

import javax.xml.ws.AsyncHandler;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class GetConfigurationTask extends Ocpp15AndAboveTask<GetConfigurationParams, GetConfigurationTask.ResponseWrapper> {

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
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new DefaultOcppCallback<ResponseWrapper>() {
            @Override
            public void success(String chargeBoxId, ResponseWrapper response) {
                String str = String.format(
                        FORMAT,
                        toStringConfList(response.getConfigurationKey()),
                        toStringUnknownList(response.getUnknownKey())
                );

                addNewResponse(chargeBoxId, str);
            }
        };
    }

    @Override
    public ocpp.cp._2012._06.GetConfigurationRequest getOcpp15Request() {
        if (params.isSetConfKeyList()) {
            return new GetConfigurationRequest().withKey(params.getConfKeyList());
        } else {
            return new GetConfigurationRequest();
        }
    }

    @Override
    public ocpp.cp._2015._10.GetConfigurationRequest getOcpp16Request() {
        if (params.isSetConfKeyList()) {
            return new ocpp.cp._2015._10.GetConfigurationRequest().withKey(params.getConfKeyList());
        } else {
            return new ocpp.cp._2015._10.GetConfigurationRequest();
        }
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.GetConfigurationResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                GetConfigurationResponse response = res.get();

                List<KeyValue> keyValues = response.getConfigurationKey()
                                                   .stream()
                                                   .map(k -> new KeyValue(k.getKey(), k.getValue(), k.isReadonly()))
                                                   .collect(Collectors.toList());

                success(chargeBoxId, new ResponseWrapper(keyValues, response.getUnknownKey()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2015._10.GetConfigurationResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ocpp.cp._2015._10.GetConfigurationResponse response = res.get();
                List<KeyValue> keyValues = response.getConfigurationKey()
                                                   .stream()
                                                   .map(k -> new KeyValue(k.getKey(), k.getValue(), k.isReadonly()))
                                                   .collect(Collectors.toList());

                success(chargeBoxId, new ResponseWrapper(keyValues, response.getUnknownKey()));
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

    @Getter
    @RequiredArgsConstructor
    public static class ResponseWrapper {
        private final List<KeyValue> configurationKey;
        private final List<String> unknownKey;
    }

    @Getter
    @RequiredArgsConstructor
    private static class KeyValue {
        private final String key;
        private final String value;
        private final boolean readonly;
    }
}
