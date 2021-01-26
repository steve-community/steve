package net.parkl.ocpp.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.parkl.ocpp.entities.OcppChargingProcess;
import org.springframework.util.StringUtils;

/**
 * OCPP szerver külső konfigurációs komponens
 * @author andor
 *
 */
@Component
public class OcppProxyConfiguration {

	
	   
	/**
	 * Felhasználható Parkl ID tagek
	 */
	@Value("${ocpp.integration.idtag:044943121F1D80,100069656E72,100069656E73,100069656E74}")
	private String integrationIdTags;
	
	/**
	 * Szülő Parkl ID tag
	 */
	private String integrationParentIdTag="PARKLTAG1";
	

	/**
	 * Töltés indítás utáni timeout be van-e kapcsolva az OCPP szerveren?  (pl. Alfen kábeles töltő)
	 */
	@Value("${ocpp.start.timeout.enabled:false}")
	private boolean startTimeoutEnabled;

	/**
	 * Töltés indítás utáni timeout másodpercben
	 */
	@Value("${ocpp.start.timeout.secs:60}")
	private int startTimeoutSecs;

	/**
	 * Töltés indítás utáni timeout mely charge boxokra van bekapcsolva (vesszővel elválasztott lista)
	 */
	@Value("${ocpp.start.timeout.chargebox.ids:}")
	private String startTimeoutChargeBoxIds;
	
	/**
	 * Töltés indításkor megvárja-e a tranzakció létrehozáskor az {@link OcppChargingProcess} rekord létrejöttét.<br>
	 * Olyan töltőknél érdemes bekapcsolni, amelyek túl gyorsan reagálnak StartTransaction üzenettel a RemoteStartTransactionre (pl. Elinta 20ms)
	 */
	@Value("${ocpp.waiting.for.chargingprocess.onstart.chargebox.ids:}")
	private String waitingForChargingProcessChargeBoxIds;
	
	/**
	 * Az olyan charge boxok idjei vesszővel elválasztva, amelyeknél a transaction stop value partial értéket tartalmaz 
	 * (a mérőóra nem küld abszolút állást, pl. Schneider)
	 */
	@Value("${ocpp.transaction.partial.chargebox.ids:}")
	private String transactionPartialChargeBoxIds;


	/**
	 * @return Felhasználható Parkl ID tagek
	 */
	public String getIntegrationIdTags() {
		return integrationIdTags;
	}

	/**
	 * @return Szülő Parkl ID tag
	 */
	public String getIntegrationParentIdTag() {
		return integrationParentIdTag;
	}


	/**
	 * @return Töltés indítás utáni timeout be van-e kapcsolva az OCPP szerveren?  (pl. Alfen kábeles töltő)
	 */
	public boolean isStartTimeoutEnabled() {
		return startTimeoutEnabled;
	}

	/**
	 * @return Töltés indítás utáni timeout másodpercben
	 */
	public int getStartTimeoutSecs() {
		return startTimeoutSecs;
	}

	/**
	 * @return Töltés indítás utáni timeout mely charge boxokra van bekapcsolva (vesszővel elválasztott lista)
	 */
	public String getStartTimeoutChargeBoxIds() {
		return startTimeoutChargeBoxIds;
	}

	/**
	 * @return Az olyan charge boxok idjei vesszővel elválasztva, amelyeknél a transaction stop value partial értéket tartalmaz 
	 * (a mérőóra nem küld abszolút állást, pl. Schneider)
	 */
	public String getTransactionPartialChargeBoxIds() {
		return transactionPartialChargeBoxIds;
	}

	public void clearPartialTransactionChargeBoxIds() {
		transactionPartialChargeBoxIds="";
	}

	public void addPartialTransactionChargeBoxId(String id) {
		if (StringUtils.isEmpty(transactionPartialChargeBoxIds)) {
			transactionPartialChargeBoxIds=id;
		} else {
			transactionPartialChargeBoxIds+=(","+id);
		}
		
	}

	public String getWaitingForChargingProcessChargeBoxIds() {
		return waitingForChargingProcessChargeBoxIds;
	}
	
	
}
