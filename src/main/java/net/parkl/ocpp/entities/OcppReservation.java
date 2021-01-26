package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the reservation database table.
 * 
 */
@Entity
@Table(name="ocpp_reservation")
@Getter
@Setter
public class OcppReservation implements Serializable {
	
	@Id
	@Column(name="reservation_pk")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int reservationPk;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="expiry_datetime")
	private Date expiryDatetime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_datetime")
	private Date startDatetime;

	private String status;

	//bi-directional many-to-one association to Connector
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="connector_pk",nullable=false)
	private Connector connector;

	//bi-directional many-to-one association to OcppTag
	@Column(name="id_tag",length=255,nullable=false)
	private String ocppTag;

	//bi-directional many-to-one association to Transaction
	@ManyToOne
	@JoinColumn(name="transaction_pk",nullable=true)
	private TransactionStart transaction;


}