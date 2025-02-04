package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.repositories.OcppTagRepository;

import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

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
        // Cseréld ki a new Date()-ot LocalDateTime-ra
        return ocppTagRepository.findIdTagsActive(LocalDateTime.now());
    }

    @Override
    public List<String> getIdTags() {
        return ocppTagRepository.findIdTagsAll();
    }
}
