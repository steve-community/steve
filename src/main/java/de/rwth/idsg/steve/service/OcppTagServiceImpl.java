package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.config.IntegratedIdTagProvider;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static ocpp.cs._2015._10.AuthorizationStatus.ACCEPTED;
import static ocpp.cs._2015._10.AuthorizationStatus.INVALID;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@Slf4j
@Service
public class OcppTagServiceImpl implements OcppTagService {
    @Autowired
    private OcppMiddleware proxyServerFacade;
    @Autowired
    private AdvancedChargeBoxConfiguration config;
    @Autowired
    private IntegratedIdTagProvider integratedIdTagProvider;

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext, String askingChargeBoxId) {
        if (Strings.isNullOrEmpty(idTag)) {
            return null;
        }

        AuthorizationStatus status = decideStatus(idTag, askingChargeBoxId);

        switch (status) {
            case INVALID:
                invalidOcppTagService.processNewUnidentified(idTag);
                return new IdTagInfo().withStatus(status);
            case BLOCKED:
            case EXPIRED:
            case CONCURRENT_TX:
            case ACCEPTED:
                return new IdTagInfo().withStatus(status);
            default:
                throw new SteveException("Unexpected AuthorizationStatus");
        }
    }

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext, String askingChargeBoxId, Supplier<IdTagInfo> supplierWhenException) {
        try {
            return getIdTagInfo(idTag, isStartTransactionReqContext, askingChargeBoxId);
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return supplierWhenException.get();
        }
    }

    private AuthorizationStatus decideStatus(String idTag, String askingChargeBoxId) {

        if (config.isUsingIntegratedTag(askingChargeBoxId)
                && integratedIdTagProvider.integratedTags().stream().noneMatch(idTag::equalsIgnoreCase)) {
            return INVALID;
        } else {
            if (!proxyServerFacade.checkRfidTag(idTag, askingChargeBoxId)) {
                log.error("The user with idTag '{}' is INVALID (validation failed on Parkl backend).", idTag);
                return INVALID;
            }
        }
        log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
        return ACCEPTED;
    }
}