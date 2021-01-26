package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the ocpp_tag database table.
 * 
 */
@Entity
@Table(name="ocpp_tag")
@Getter
@Setter
public class OcppTag implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name="ocpp_tag_pk")
	private int ocppTagPk;

	
	@Column(name="expiry_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	@Column(name="id_tag",nullable=false,length=255)
	private String idTag;

	
	@Lob
	private String note;

	//bi-directional many-to-one association to OcppTag
	@Column(name="parent_id_tag",length=255,nullable=true)
	private String parentIdTag;

	@Column(name = "max_active_transaction_count", nullable = false)
	private int maxActiveTransactionCount = 1;

}