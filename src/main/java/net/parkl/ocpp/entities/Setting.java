/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ocpp_settings")
@Getter
@Setter
public class Setting implements Serializable {
    @Id
    @Column(name = "app_id")
    private String appId;

    @Column(name = "heartbeat_interval_in_seconds")
    private int heartbeatIntervalInSeconds;

    @Column(name = "hours_to_expire")
    private int hoursToExpire;

    @Column(name = "mail_enabled", length = 1)
    private boolean mailEnabled;

    @Column(name = "mail_from")
    private String mailFrom;

    @Column(name = "mail_host")
    private String mailHost;

    @Column(name = "mail_password")
    private String mailPassword;

    @Column(name = "mail_port")
    private int mailPort;

    @Column(name = "mail_protocol")
    private String mailProtocol;

    @Lob
    @Column(name = "mail_recipients")
    private String mailRecipients;

    @Column(name = "mail_username")
    private String mailUsername;

    @Lob
    @Column(name = "notification_features")
    private String notificationFeatures;

}