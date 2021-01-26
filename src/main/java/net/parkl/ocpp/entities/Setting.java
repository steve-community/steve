package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the settings database table.
 * 
 */
@Entity
@Table(name="ocpp_settings")
@Getter
@Setter
public class Setting implements Serializable {
	
	@Id
	@Column(name="app_id")
	private String appId;

	@Column(name="heartbeat_interval_in_seconds")
	private int heartbeatIntervalInSeconds;

	@Column(name="hours_to_expire")
	private int hoursToExpire;

	@Column(name="mail_enabled",length=1)
	private boolean mailEnabled;

	@Column(name="mail_from")
	private String mailFrom;

	@Column(name="mail_host")
	private String mailHost;

	@Column(name="mail_password")
	private String mailPassword;

	@Column(name="mail_port")
	private int mailPort;

	@Column(name="mail_protocol")
	private String mailProtocol;

	@Lob
	@Column(name="mail_recipients")
	private String mailRecipients;

	@Column(name="mail_username")
	private String mailUsername;

	@Lob
	@Column(name="notification_features")
	private String notificationFeatures;


}