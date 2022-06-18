package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargePointService {

  @Autowired
  private ChargePointRepository chargePointRepository;

  public Optional<String> getRegistrationStatus(String chargeBoxId) {
    return chargePointRepository.getRegistrationStatus(chargeBoxId);
  }

  public List<ChargePointSelect> getChargePointSelect(OcppProtocol protocol, List<String> inStatusFilter) {
    return chargePointRepository.getChargePointSelect(protocol, inStatusFilter);
  }

  public List<String> getChargeBoxIds() {
    return chargePointRepository.getChargeBoxIds();
  }

  public Map<String, Integer> getChargeBoxIdPkPair(List<String> chargeBoxIdList) {
    return chargePointRepository.getChargeBoxIdPkPair(chargeBoxIdList);
  }

  public List<ChargePoint.Overview> getOverview(ChargePointQueryForm form) {
    return chargePointRepository.getOverview(form);
  }

  public ChargePoint.Details getDetails(int chargeBoxPk) {
    return chargePointRepository.getDetails(chargeBoxPk);
  }

  public List<ConnectorStatus> getChargePointConnectorStatus() {
    return chargePointRepository.getChargePointConnectorStatus();
  }

  public List<ConnectorStatus> getChargePointConnectorStatus(@Nullable ConnectorStatusForm form) {
    return chargePointRepository.getChargePointConnectorStatus(form);
  }

  public List<Integer> getNonZeroConnectorIds(String chargeBoxId) {
    return chargePointRepository.getNonZeroConnectorIds(chargeBoxId);
  }

  public void addChargePointList(List<String> chargeBoxIdList) {
    chargePointRepository.addChargePointList(chargeBoxIdList);
  }

  public int addChargePoint(ChargePointForm form) {
    return chargePointRepository.addChargePoint(form);
  }

  public void updateChargePoint(ChargePointForm form) {
    chargePointRepository.updateChargePoint(form);
  }

  public void deleteChargePoint(int chargeBoxPk) {
    chargePointRepository.deleteChargePoint(chargeBoxPk);
  }
}
