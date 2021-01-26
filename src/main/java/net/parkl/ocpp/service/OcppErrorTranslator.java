package net.parkl.ocpp.service;

import org.springframework.stereotype.Component;

@Component
public class OcppErrorTranslator {

	public String translateError(String errorCode) {
		if (OcppConstants.ERROR_CONNECTOR_LOCK_FAILURE.equals(errorCode)||OcppConstants.ERROR_EV_COMMUNICATION_ERROR.equals(errorCode)) {
			return OcppConstants.REASON_CONNECTOR_LOCK_FAILURE;
		}
		return null;
	}

}
