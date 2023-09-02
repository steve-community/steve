ALTER TABLE `charge_box`
  ADD admin_address varchar(255) NULL,
  ADD insert_connector_status_after_transaction_msg BOOLEAN DEFAULT TRUE;
