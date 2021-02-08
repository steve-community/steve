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

import javax.persistence.*;

@Entity
@Table(name = "ocpp_address")
@Getter
@Setter
public class OcppAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_pk")
    private int addressPk;

    @Column(name = "street", length = 1000, nullable = true)
    private String street;

    @Column(name = "house_number", length = 255, nullable = true)
    private String houseNumber;

    @Column(name = "zip_code", length = 255, nullable = true)
    private String zipCode;

    @Column(name = "city", length = 255, nullable = true)
    private String city;

    @Column(name = "country", length = 255, nullable = true)
    private String country;

}
