package de.rwth.idsg.steve.service;

import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isBlocked;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.isExpired;
import static de.rwth.idsg.steve.utils.OcppTagActivityRecordUtils.reachedLimitOfActiveTransactions;

import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizationStatus;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthTagService {

  public AuthorizationStatus decideStatus(@Nullable OcppTagActivityRecord record, String idTag,
      boolean isStartTransactionReqContext) {
    if (record == null) {
      log.error("The user with idTag '{}' is INVALID (not present in DB).", idTag);
      return AuthorizationStatus.INVALID;
    }

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
