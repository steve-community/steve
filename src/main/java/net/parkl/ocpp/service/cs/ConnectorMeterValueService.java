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
package net.parkl.ocpp.service.cs;

import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.ConnectorMeterValue;
import net.parkl.ocpp.entities.TransactionStart;
import net.parkl.ocpp.repositories.ConnectorMeterValueRepository;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ConnectorMeterValueService {

    private final ConnectorMeterValueRepository connectorMeterValueRepo;

    @Autowired
    public ConnectorMeterValueService(ConnectorMeterValueRepository connectorMeterValueRepo) {
        this.connectorMeterValueRepo = connectorMeterValueRepo;
    }

    public void insertMeterValues(List<MeterValue> meterValues, TransactionStart transactionStart) {
        for (MeterValue meterValue : meterValues) {
            for (SampledValue sampledValue : meterValue.getSampledValue()) {
                ConnectorMeterValue connectorMeterValue = new ConnectorMeterValue();
                connectorMeterValue.setConnector(transactionStart.getConnector());
                connectorMeterValue.setTransaction(transactionStart);

                connectorMeterValue.setValue(sampledValue.getValue());
                if (meterValue.getTimestamp() != null) {
                    connectorMeterValue.setValueTimestamp(meterValue.getTimestamp().toDate());
                }
                connectorMeterValue.setReadingContext(sampledValue.isSetContext() ? sampledValue.getContext().value() : null);
                connectorMeterValue.setFormat(sampledValue.isSetFormat() ? sampledValue.getFormat().value() : null);
                connectorMeterValue.setMeasurand(sampledValue.isSetMeasurand() ? sampledValue.getMeasurand().value() : null);
                connectorMeterValue.setLocation(sampledValue.isSetLocation() ? sampledValue.getLocation().value() : null);
                connectorMeterValue.setUnit(sampledValue.isSetUnit() ? sampledValue.getUnit().value() : null);
                connectorMeterValue.setPhase(sampledValue.isSetPhase() ? sampledValue.getPhase().value() : null);
                connectorMeterValueRepo.save(connectorMeterValue);
            }
        }
    }

    public List<ConnectorMeterValue> getConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction,
                                                                                     String measurand) {
        return connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction,
                measurand);
    }

    public ConnectorMeterValue getLastConnectorMeterValueByTransactionAndMeasurand(TransactionStart transaction,
                                                                                   String measurand) {
        List<ConnectorMeterValue> list =
                connectorMeterValueRepo.findByTransactionAndMeasurandAndPhaseIsNullOrderByValueTimestampDesc(transaction,
                        measurand);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<ConnectorMeterValue> findByTransactionPk(int transactionPk) {
        return connectorMeterValueRepo.findByTransactionPk(transactionPk);
    }

    public List<ConnectorMeterValue> findByChargeBoxIdAndConnectorIdAfter(String chargeBoxId,
                                                                          int connectorId,
                                                                          Date startTimestamp) {
        return connectorMeterValueRepo.findByChargeBoxIdAndConnectorIdAfter(chargeBoxId, connectorId, startTimestamp);
    }

    public List<ConnectorMeterValue> findByChargeBoxIdAndConnectorIdBetween(String chargeBoxId,
                                                                            int connectorId,
                                                                            Date startTimestamp,
                                                                            Date nextTxTimestamp) {

        return connectorMeterValueRepo.findByChargeBoxIdAndConnectorIdBetween(chargeBoxId,
                connectorId,
                startTimestamp,
                nextTxTimestamp);
    }
}
