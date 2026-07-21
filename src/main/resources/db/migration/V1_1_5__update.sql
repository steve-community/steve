-- Add the column as nullable first so existing rows can be backfilled with a
-- meaningful lifecycle timestamp instead of the migration execution time.
ALTER TABLE reservation
    ADD COLUMN status_timestamp TIMESTAMP(6) NULL
        AFTER status;


-- Reconstruct when the current reservation status became effective from the
-- best historical timestamp available for each lifecycle state.
UPDATE reservation r
    LEFT JOIN transaction_start tx
        ON tx.transaction_pk = r.transaction_pk
SET r.status_timestamp =
    CASE r.status
        -- The station-provided transaction start timestamp most closely
        -- represents when an accepted reservation became used.
        WHEN 'USED' THEN
            COALESCE(
                tx.start_timestamp,
                CASE
                    WHEN r.updated_at > r.created_at
                        AND (
                            r.start_datetime IS NULL
                            OR r.updated_at >= r.start_datetime
                        )
                        AND (
                            r.expiry_datetime IS NULL
                            OR r.updated_at <= r.expiry_datetime
                        )
                        THEN r.updated_at
                END,
                r.start_datetime,
                r.expiry_datetime,
                r.created_at,
                r.updated_at,
                CURRENT_TIMESTAMP(6)
            )

        -- updated_at is the cancellation time for reservations cancelled after
        -- that audit column was introduced. Existing rows received equal
        -- created_at and updated_at values when the audit-column migration ran,
        -- so only trust a later updated_at inside the reservation window.
        -- Otherwise, use expiry as an upper-bound approximation.
        WHEN 'CANCELLED' THEN
            CASE
                WHEN r.updated_at > r.created_at
                    AND (
                        r.start_datetime IS NULL
                        OR r.updated_at >= r.start_datetime
                    )
                    AND (
                        r.expiry_datetime IS NULL
                        OR r.updated_at <= r.expiry_datetime
                    )
                    THEN r.updated_at
                ELSE COALESCE(
                    r.expiry_datetime,
                    r.start_datetime,
                    r.created_at,
                    r.updated_at,
                    CURRENT_TIMESTAMP(6)
                )
            END

        -- ACCEPTED requires an update after insertion. Equal audit timestamps
        -- identify either an untouched row or a row stamped by the audit-column
        -- migration, so fall back to the beginning of the reservation window.
        WHEN 'ACCEPTED' THEN
            COALESCE(
                CASE
                    WHEN r.updated_at > r.created_at
                        AND (
                            r.start_datetime IS NULL
                            OR r.updated_at >= r.start_datetime
                        )
                        AND (
                            r.expiry_datetime IS NULL
                            OR r.updated_at <= r.expiry_datetime
                        )
                        THEN r.updated_at
                END,
                r.start_datetime,
                r.expiry_datetime,
                r.created_at,
                r.updated_at,
                CURRENT_TIMESTAMP(6)
            )

        -- WAITING is the initial status, so the reservation start is the best
        -- historical value. For rows created after audit columns were added,
        -- created_at and updated_at are equal and are valid insertion fallbacks.
        WHEN 'WAITING' THEN
            COALESCE(
                r.start_datetime,
                r.expiry_datetime,
                r.created_at,
                r.updated_at,
                CURRENT_TIMESTAMP(6)
            )

        -- Preserve the NOT NULL invariant if an unknown historical status exists.
        ELSE COALESCE(
            r.start_datetime,
            r.expiry_datetime,
            r.created_at,
            r.updated_at,
            CURRENT_TIMESTAMP(6)
        )
    END;


-- Once every historical row has a value, enforce the invariant. The default is
-- safe for future inserts because it represents when the initial WAITING status
-- is created; later transitions set status_timestamp explicitly in the repository.
ALTER TABLE reservation
    MODIFY COLUMN status_timestamp TIMESTAMP(6) NOT NULL
    DEFAULT CURRENT_TIMESTAMP(6)
    AFTER status;
