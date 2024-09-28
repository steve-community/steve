package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.config.IntegratedIdTagProvider;
import net.parkl.ocpp.service.middleware.OcppChargingMiddleware;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static ocpp.cs._2015._10.AuthorizationStatus.ACCEPTED;
import static ocpp.cs._2015._10.AuthorizationStatus.INVALID;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcppTagServiceImpl implements OcppTagService {
    private final AuthTagService authTagService;
    private final OcppTagRepository ocppTagRepository;

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext,
                                  @Nullable String chargeBoxId, @Nullable Integer connectorId) {
        if (Strings.isNullOrEmpty(idTag)) {
            return null;
        }

        IdTagInfo idTagInfo = authTagService.decideStatus(idTag, isStartTransactionReqContext, chargeBoxId, connectorId);

        if (idTagInfo.getStatus() == AuthorizationStatus.INVALID) {
            invalidOcppTagService.processNewUnidentified(idTag);
        }

        return idTagInfo;
    }

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext,
                                  @Nullable String chargeBoxId, @Nullable Integer connectorId,
                                  Supplier<IdTagInfo> supplierWhenException) {
        try {
            return getIdTagInfo(idTag, isStartTransactionReqContext, chargeBoxId, connectorId);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return supplierWhenException.get();
        }
    }

    @Override
    public String getParentIdtag(String idTag) {
        OcppTag tag = ocppTagRepository.findByIdTag(idTag);
        if (tag != null) {
            return tag.getParentIdTag();
        }
        return null;
    }

    @Override
    public List<String> getActiveIdTags() {
        return ocppTagRepository.findIdTagsActive(new Date());
    }

    @Override
    public List<String> getIdTags() {
        return ocppTagRepository.findIdTagsAll();
    }


}