package de.rwth.idsg.steve.service;

import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isBlocked;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isExpired;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.reachedLimitOfActiveTransactions;

import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTagService {

  private final RealtimeAuthService realtimeAuthService;

  public AuthorizationStatus decideStatus(@Nullable OcppTagActivityRecord record, String idTag,
      boolean isStartTransactionReqContext, @Nullable String chargeBoxId,
      @Nullable Integer connectorId) {
    if (record == null) {
      log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
      return AuthorizationStatus.INVALID;
    }

    switch (record.getWhitelist()) {
      case always:
      case allowed:
        return getInternalAuthorizationStatus(record, idTag, isStartTransactionReqContext);
      case allowed_offline:
        try {
          realtimeAuthService.decideStatus(idTag, chargeBoxId, connectorId);
        } catch (Throwable t) {
          log.error("RealtimeAuth failed, use internal logic.", t);
          return getInternalAuthorizationStatus(record, idTag, isStartTransactionReqContext);
        }
      case never:
        try {
          return realtimeAuthService.decideStatus(idTag, chargeBoxId, connectorId);
        } catch (Throwable t) {
          log.error("RealtimeAuth failed, status is INVALID.", t);
          return AuthorizationStatus.INVALID;
        }
      default:
        throw new IllegalStateException("Unknown whitelist value: " + record.getWhitelist());
    }
  }

  @NotNull
  private static AuthorizationStatus getInternalAuthorizationStatus(@NotNull OcppTagActivityRecord record,
      String idTag, boolean isStartTransactionReqContext) {
    if (isBlocked(record)) {
      log.error("The user with idTag '{}' is BLOCKED.", idTag);
      return AuthorizationStatus.BLOCKED;
    }

    if (isExpired(record, DateTime.now())) {
      log.error("The user with idTag '{}' is EXPIRED.", idTag);
      return AuthorizationStatus.EXPIRED;
    }

    // https://github.com/steve-community/steve/issues/219
    if (isStartTransactionReqContext && reachedLimitOfActiveTransactions(record)) {
      log.warn("The user with idTag '{}' is ALREADY in another transaction(s).", idTag);
      return AuthorizationStatus.CONCURRENT_TX;
    }

    log.debug("The user with idTag '{}' is ACCEPTED.", record.getIdTag());
    return AuthorizationStatus.ACCEPTED;
  }
}
