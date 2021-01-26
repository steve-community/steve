package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the connector_status database table.
 * 
 */
@Entity
@Table(name="ocpp_connector_status")
@Getter
@Setter
public class ConnectorStatus implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name="connector_status_pk")
	private int connectorStatusPk;

	@Column(name="error_code")
	private String errorCode;

	@Column(name="error_info")
	private String errorInfo;

	private String status;

	@Column(name="status_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date statusTimestamp;

	@Column(name="vendor_error_code")
	private String vendorErrorCode;

	@Column(name="vendor_id")
	private String vendorId;

	//bi-directional many-to-one association to Connector
	@ManyToOne
	@JoinColumn(name="connector_pk",nullable=false)
	private Connector connector;

}