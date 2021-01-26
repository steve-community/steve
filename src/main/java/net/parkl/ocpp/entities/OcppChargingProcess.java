package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ocpp_charging_process")
@Getter
@Setter
public class OcppChargingProcess {
	@Id
	@Column(name="ocpp_charging_process_id")
	private String ocppChargingProcessId;

	
	/**
	 * Létrehozás időpontja
	 */
	@Column(name="start_date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name="end_date", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name="stop_request_date", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date stopRequestDate;
	
	@Column(name="license_plate", nullable=true,length=20)
	private String licensePlate;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="connector_pk",nullable=false)
	private Connector connector;

	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="transaction_pk",nullable=true)
	private TransactionStart transaction;
	
	@Column(name="id_tag",length=255,nullable=true)
	private String ocppTag;
	
	@Column(name="error_code",length=100,nullable=true)
	private String errorCode;
	
	@Column(name="limit_kwh",nullable=true)
	private Float limitKwh;

	@Column(name = "limit_minute", nullable = true)
	private Integer limitMinute;
	
	/**
	 * INSERT előtt lefutó metódus
	 */
	@PrePersist
	public void prePersist() {
		startDate=new Date();
	}



}
