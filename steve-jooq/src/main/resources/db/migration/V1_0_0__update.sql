CREATE TABLE transaction_stop_failed (
  transaction_pk INT,
  event_timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  event_actor ENUM('station', 'manual'),
  stop_timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  stop_value VARCHAR(255),
  stop_reason VARCHAR(255),
  fail_reason TEXT
);
