package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.service.dto.UnidentifiedIncomingObject;
import de.rwth.idsg.steve.utils.DateTimeConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.service.OcppMiddleware;
import net.parkl.ocpp.service.config.AdvancedChargeBoxConfiguration;
import net.parkl.ocpp.service.cs.OcppIdTagService;
import net.parkl.ocpp.service.cs.SettingsService;
import net.parkl.ocpp.service.cs.TransactionService;
import ocpp.cp._2015._10.AuthorizationData;
import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.IdTagInfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ocpp.cs._2015._10.AuthorizationStatus.*;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 03.01.2015
 */
@Slf4j
@Service
public class OcppTagServiceImpl implements OcppTagService {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private OcppIdTagService tagService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OcppMiddleware proxyServerFacade;
    @Autowired
    private AdvancedChargeBoxConfiguration config;

    private final UnidentifiedIncomingObjectService invalidOcppTagService = new UnidentifiedIncomingObjectService(1000);

    public List<AuthorizationData> getAuthDataOfAllTags() {
        AuthorisationDataMapper mapper = new AuthorisationDataMapper();

        return tagService.getRecords().stream()
                .map(mapper::map).collect(Collectors.toList());
    }

    public List<AuthorizationData> getAuthData(List<String> idTagList) {
        AuthorisationDataMapper mapper = new AuthorisationDataMapper();

        return tagService.getRecords(idTagList).stream()
                .map(mapper::map).collect(Collectors.toList());
    }

    public List<UnidentifiedIncomingObject> getUnknownOcppTags() {
        return invalidOcppTagService.getObjects();
    }

    public void removeUnknown(String idTag) {
        invalidOcppTagService.remove(idTag);
    }

    public void removeUnknown(List<String> idTagList) {
        invalidOcppTagService.removeAll(idTagList);
    }

    @Nullable
    public IdTagInfo getIdTagInfo(@Nullable String idTag, boolean isStartTransactionReqContext, String askingChargeBoxId) {
        if (Strings.isNullOrEmpty(idTag)) {
            return null;
        }

        OcppTag record = tagService.getRecord(idTag);
        AuthorizationStatus status = decideStatus(record, idTag, isStartTransactionReqContext, askingChargeBoxId);

        switch (status) {
            case INVALID:
                invalidOcppTagService.processNewUnidentified(idTag);
                return new IdTagInfo().withStatus(status);
            case BLOCKED:
            case EXPIRED:
            case CONCURRENT_TX:
            case ACCEPTED:
                return new IdTagInfo().withStatus(status)
                        .withParentIdTag(record.getParentIdTag())
                        .withExpiryDate(getExpiryDateOrDefault(record));
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

    /**
     * If the database contains an actual expiry, use it. Otherwise, calculate an expiry for cached info
     */
    @Nullable
    private DateTime getExpiryDateOrDefault(OcppTag record) {
        if (record.getExpiryDate() != null) {
            return DateTimeConverter.from(record.getExpiryDate());
        }

        int hoursToExpire = settingsService.getHoursToExpire();

        // From web page: The value 0 disables this functionality (i.e. no expiry date will be set).
        if (hoursToExpire == 0) {
            return null;
        } else {
            return DateTime.now().plusHours(hoursToExpire);
        }
    }

    private AuthorizationStatus decideStatus(OcppTag record, String idTag, boolean isStartTransactionReqContext, String askingChargeBoxId) {
        if (config.isIdTagMax10Characters(askingChargeBoxId) || config.isUsingIntegratedTag(askingChargeBoxId)) {
            log.info("Checking in local db only...");
            if (record == null) {
                log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
                return INVALID;
            }
            if (isBlocked(record)) {
                log.error("The user with idTag '{}' is BLOCKED.", idTag);
                return BLOCKED;
            }
            if (isExpired(record, DateTime.now())) {
                log.error("The user with idTag '{}' is EXPIRED.", idTag);
                return EXPIRED;
            }
        } else {
            if (!proxyServerFacade.checkRfidTag(idTag, askingChargeBoxId)) {
                log.error("The user with idTag '{}' is INVALID (validation failed on Parkl backend).", idTag);
                return INVALID;
            }
        }
        // https://github.com/RWTH-i5-IDSG/steve/issues/219
        if (isStartTransactionReqContext) {
            long active = transactionService.getActiveTransactionCountByIdTag(record.getIdTag());
            if (reachedLimitOfActiveTransactions(record, active)) {
                log.warn("The user with idTag '{}' is ALREADY in another transaction(s).", idTag);
                return CONCURRENT_TX;
            }
        }
        log.debug("The user with idTag '{}' is ACCEPTED.", idTag);
        return ACCEPTED;
    }

    /**
     * ConcurrentTx is only valid for StartTransactionRequest
     */
    private static ocpp.cp._2015._10.AuthorizationStatus decideStatusForAuthData(OcppTag record, DateTime now) {
        if (isBlocked(record)) {
            return ocpp.cp._2015._10.AuthorizationStatus.BLOCKED;
        } else if (isExpired(record, now)) {
            return ocpp.cp._2015._10.AuthorizationStatus.EXPIRED;
        } else {
            return ocpp.cp._2015._10.AuthorizationStatus.ACCEPTED;
        }
    }

    private static boolean isExpired(OcppTag record, DateTime now) {
        DateTime expiry = DateTimeConverter.from(record.getExpiryDate());
        return expiry != null && now.isAfter(expiry);
    }

    private static boolean isBlocked(OcppTag record) {
        return getToggle(record) == ConcurrencyToggle.Blocked;
    }

    private static boolean reachedLimitOfActiveTransactions(OcppTag record, long active) {
        ConcurrencyToggle toggle = getToggle(record);
        switch (toggle) {
            case Blocked:
                return true; // for completeness
            case AllowAll:
                return false;
            case AllowAsSpecified:
                int max = record.getMaxActiveTransactionCount();
                return active >= max;
            default:
                throw new RuntimeException("Unexpected ConcurrencyToggle");
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class AuthorisationDataMapper {

        private final DateTime nowDt = DateTime.now();

        public AuthorizationData map(OcppTag record) {
            return new AuthorizationData().withIdTag(record.getIdTag())
                    .withIdTagInfo(
                            new ocpp.cp._2015._10.IdTagInfo()
                                    .withStatus(decideStatusForAuthData(record, nowDt))
                                    .withParentIdTag(record.getParentIdTag())
                                    .withExpiryDate(DateTimeConverter.from(record.getExpiryDate()))
                    );
        }
    }

    private enum ConcurrencyToggle {
        Blocked, AllowAll, AllowAsSpecified
    }

    private static ConcurrencyToggle getToggle(OcppTag r) {
        int max = r.getMaxActiveTransactionCount();
        if (max == 0) {
            return ConcurrencyToggle.Blocked;
        } else if (max < 0) {
            return ConcurrencyToggle.AllowAll;
        } else {
            return ConcurrencyToggle.AllowAsSpecified;
        }
    }
}
