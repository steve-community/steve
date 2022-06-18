package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePoint;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.ConnectorStatus;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.service.notification.ChargePointCreated;
import de.rwth.idsg.steve.service.notification.ChargePointDeleted;
import de.rwth.idsg.steve.service.notification.ChargePointUpdated;
import de.rwth.idsg.steve.service.notification.UnidentifiedChargePointProceed;
import de.rwth.idsg.steve.service.notification.UnidentifiedChargePointRemoved;
import de.rwth.idsg.steve.web.dto.ChargePointForm;
import de.rwth.idsg.steve.web.dto.ChargePointQueryForm;
import de.rwth.idsg.steve.web.dto.ConnectorStatusForm;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ChargePointService {

  @Autowired private ApplicationEventPublisher applicationEventPublisher;
  @Autowired private ChargePointRepository chargePointRepository;

  private final UnidentifiedIncomingObjectService unknownChargePointService = new UnidentifiedIncomingObjectService(100);

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
    getChargeBoxIdPkPair(chargeBoxIdList).forEach(
        (chargeBoxId, chargeBoxPk) -> applicationEventPublisher.publishEvent(new ChargePointCreated(chargeBoxPk, chargeBoxId))
    );
    removeUnknown(chargeBoxIdList);
  }

  public int addChargePoint(ChargePointForm form) {
    int chargeBoxPk =  chargePointRepository.addChargePoint(form);
    applicationEventPublisher.publishEvent(new ChargePointCreated(chargeBoxPk, form.getChargeBoxId()));
    removeUnknown(Collections.singletonList(form.getChargeBoxId()));
    return chargeBoxPk;
  }

  public void updateChargePoint(ChargePointForm form) {
    chargePointRepository.updateChargePoint(form);
    applicationEventPublisher.publishEvent(new ChargePointUpdated(form.getChargeBoxPk(), form.getChargeBoxId()));
  }

  public void deleteChargePoint(int chargeBoxPk) {
    ChargeBoxRecord chargeBox = chargePointRepository.getDetails(chargeBoxPk).getChargeBox();
    chargePointRepository.deleteChargePoint(chargeBoxPk);
    applicationEventPublisher.publishEvent(new ChargePointDeleted(chargeBoxPk, chargeBox.getChargeBoxId()));
  }

  public List<UnidentifiedIncomingObject> getUnknownChargePoints() {
    return unknownChargePointService.getObjects();
  }

  public void proceedUnknown(String chargeBoxId) {
    unknownChargePointService.processNewUnidentified(chargeBoxId);
    applicationEventPublisher.publishEvent(new UnidentifiedChargePointProceed(chargeBoxId));
  }

  public void removeUnknown(List<String> chargeBoxIdList) {
    unknownChargePointService.removeAll(chargeBoxIdList);
    chargeBoxIdList.forEach(chargeBoxId -> applicationEventPublisher.publishEvent(new UnidentifiedChargePointRemoved(chargeBoxId)));
  }
}
