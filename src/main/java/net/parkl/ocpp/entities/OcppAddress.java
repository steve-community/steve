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
