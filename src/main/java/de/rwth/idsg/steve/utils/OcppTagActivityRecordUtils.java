package de.rwth.idsg.steve.utils;

import jooq.steve.db.tables.records.OcppTagActivityRecord;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OcppTagActivityRecordUtils {

  public static boolean isExpired(OcppTagActivityRecord record, DateTime now) {
    DateTime expiry = record.getExpiryDate();
    return expiry != null && now.isAfter(expiry);
  }

  public static boolean isBlocked(OcppTagActivityRecord record) {
    return record.getMaxActiveTransactionCount() == 0;
  }

  public static boolean reachedLimitOfActiveTransactions(OcppTagActivityRecord record) {
    int max = record.getMaxActiveTransactionCount();

    // blocked
    if (max == 0) {
      return true;
    }

    // allow all
    if (max < 0) {
      return false;
    }

    // allow as specified
    return record.getActiveTransactionCount() >= max;
  }
}
