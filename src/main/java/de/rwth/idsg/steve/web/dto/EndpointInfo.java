package de.rwth.idsg.steve.web.dto;


import de.rwth.idsg.steve.config.WebEnvironment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.08.2018
 */
public class EndpointInfo implements IEndpointInfo{
   private static final EndpointInfo INSTANCE=new EndpointInfo();

   public static EndpointInfo getInstance() {
   		return INSTANCE;
   }

    private final ItemsWithInfo webInterface = new ItemsWithInfo("Access the web interface using", WebEnvironment.getContextRoot()+"/manager/home");
    private final ItemsWithInfo ocppSoap = new ItemsWithInfo("SOAP endpoint for OCPP", WebEnvironment.getContextRoot()+"/services/CentralSystemService");
    private final ItemsWithInfo ocppWebSocket = new ItemsWithInfo("WebSocket/JSON endpoint for OCPP", WebEnvironment.getContextRoot()+"/websocket/CentralSystemService/(chargeBoxId)");


    public static class ItemsWithInfo {
        private final String info;
        private final String dataElementPostFix;
        private List<String> data;

        private ItemsWithInfo(String info, String dataElementPostFix) {
            this.info = info;
            this.dataElementPostFix = dataElementPostFix;
            this.data = Collections.emptyList();
        }

        public synchronized void setData(List<String> data) {
            this.data = data.stream()
                            .map(s -> s + dataElementPostFix)
                            .collect(Collectors.toList());
        }

		public String getInfo() {
			return info;
		}

		public String getDataElementPostFix() {
			return dataElementPostFix;
		}

		public List<String> getData() {
			return data;
		}
    }


	public ItemsWithInfo getWebInterface() {
		return webInterface;
	}


	public ItemsWithInfo getOcppSoap() {
		return ocppSoap;
	}


	public ItemsWithInfo getOcppWebSocket() {
		return ocppWebSocket;
	}
}
