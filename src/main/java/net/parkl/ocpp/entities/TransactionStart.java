package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;
import net.parkl.ocpp.entities.Connector;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the transaction database table.
 * 
 */
@Entity
@Table(name="ocpp_transaction_start")
@Getter
@Setter
public class TransactionStart implements Serializable {
	@Id
	@Column(name="transaction_pk")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int transactionPk;

	@Column(name="start_timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTimestamp;

	@Column(name="start_value")
	private String startValue;



	//bi-directional many-to-one association to Connector
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="connector_pk",nullable=false)
	private Connector connector;

	//bi-directional many-to-one association to OcppTag
	@Column(name="id_tag",length=255,nullable=false)
	private String ocppTag;


	@Column(name="event_timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventTimestamp;


	public TransactionStart() {
	}




}