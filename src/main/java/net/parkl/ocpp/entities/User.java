package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ocpp_user")
@Getter
@Setter
public class User {
	@Id
	@Column(name="user_pk")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int userPk;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ocpp_tag_pk",nullable=true)
	private OcppTag ocppTag;
	
	@ManyToOne
	@JoinColumn(name="address_pk")
	private OcppAddress address;
	
	@Column(name="first_name",length=255,nullable=true)
	private String firstName;


	
	@Column(name="last_name",length=255,nullable=true)
	private String lastName;

	
	@Column(name="birth_day",nullable=true)
	@Temporal(TemporalType.DATE)
	private Date birthDay;
	
	@Column(name="sex",length=1,nullable=true)
	private String sex;
	
	@Column(name="phone",length=255,nullable=true)
	private String phone;

	@Column(name="e_mail",length=255,nullable=true)
	private String email;
	
	@Column(name="note",nullable=true)
	@Lob
	private String note;


}
