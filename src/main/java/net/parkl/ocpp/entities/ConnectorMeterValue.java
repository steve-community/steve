package net.parkl.ocpp.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ocpp_connector_meter_value")
@Getter
@Setter
public class ConnectorMeterValue implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmv_pk")
    private int cmvPk;

    private String format;

    private String location;

    private String measurand;

    @Column(name = "reading_context")
    private String readingContext;

    private String unit;

    private String value;

    @Column(name = "value_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date valueTimestamp;

    @ManyToOne
    @JoinColumn(name = "connector_pk", nullable = false)
    private Connector connector;

    @ManyToOne
    @JoinColumn(name = "transaction_pk")
    private TransactionStart transaction;

    @Column(name = "phase", length = 255, nullable = true)
    private String phase;

}