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

import java.time.LocalDate;
import java.time.LocalDateTime;

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
	private LocalDate birthDay;

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
