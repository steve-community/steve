--
-- update the character set for existing installations.

-- mysql will refuse to change the character set of tables with foreign key relationships,
-- so we disable foreign key checks (we could also first drop FKs and after changes insert FKs again).
-- but then, to ensure data integrity, we should block other access to these tables. hence, the lock tables.
--
-- and also we cannot touch book keeping table "schema_version" of flyway,
-- since it will be in use during the migration.
--

LOCK TABLES
  address WRITE,
  charge_box WRITE,
  connector WRITE,
  connector_meter_value WRITE,
  connector_status WRITE,
  ocpp_tag WRITE,
  reservation WRITE,
  settings WRITE,
  schema_version WRITE,
  `transaction` WRITE,
  `user` WRITE;

alter table charge_box
    drop foreign key FK_charge_box_address_apk;

alter table connector
    drop foreign key FK_connector_charge_box_cbid;

alter table connector_meter_value
    drop foreign key FK_pk_cm;

alter table connector_meter_value
    drop foreign key FK_tid_cm;

alter table connector_status
    drop foreign key FK_cs_pk;

alter table ocpp_tag
    drop foreign key FK_ocpp_tag_parent_id_tag;

alter table reservation
    drop foreign key FK_reservation_charge_box_cbid;

alter table reservation
    drop foreign key FK_reservation_ocpp_tag_id_tag;

alter table reservation
    drop foreign key FK_transaction_pk_r;

alter table transaction
    drop foreign key FK_connector_pk_t;

alter table transaction
    drop foreign key FK_transaction_ocpp_tag_id_tag;

alter table user
    drop foreign key FK_user_address_apk;

alter table user
    drop foreign key FK_user_ocpp_tag_otpk;


ALTER TABLE charge_box CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE address CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE connector CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE connector_meter_value CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE connector_status CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE ocpp_tag CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE reservation CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE settings CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE `transaction` CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE `user` CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

alter table charge_box
    add constraint FK_charge_box_address_apk
        foreign key (address_pk) references address (address_pk)
            on delete set null;

alter table connector
    add constraint FK_connector_charge_box_cbid
        foreign key (charge_box_id) references charge_box (charge_box_id)
            on delete cascade;

alter table connector_meter_value
    add constraint FK_pk_cm
        foreign key (connector_pk) references connector (connector_pk)
            on delete cascade;

alter table connector_meter_value
    add constraint FK_tid_cm
        foreign key (transaction_pk) references transaction (transaction_pk)
            on delete set null;

alter table connector_status
    add constraint FK_cs_pk
        foreign key (connector_pk) references connector (connector_pk)
            on delete cascade;

alter table ocpp_tag
    add constraint FK_ocpp_tag_parent_id_tag
        foreign key (parent_id_tag) references ocpp_tag (id_tag);

alter table reservation
    add constraint FK_reservation_charge_box_cbid
        foreign key (charge_box_id) references charge_box (charge_box_id)
            on delete cascade;

alter table reservation
    add constraint FK_reservation_ocpp_tag_id_tag
        foreign key (id_tag) references ocpp_tag (id_tag)
            on delete cascade;

alter table reservation
    add constraint FK_transaction_pk_r
        foreign key (transaction_pk) references transaction (transaction_pk);

alter table transaction
    add constraint FK_connector_pk_t
        foreign key (connector_pk) references connector (connector_pk)
            on delete cascade;

alter table transaction
    add constraint FK_transaction_ocpp_tag_id_tag
        foreign key (id_tag) references ocpp_tag (id_tag)
            on delete cascade;
alter table user
    add constraint FK_user_address_apk
        foreign key (address_pk) references address (address_pk)
            on delete set null;

alter table user
    add constraint FK_user_ocpp_tag_otpk
        foreign key (ocpp_tag_pk) references ocpp_tag (ocpp_tag_pk)
            on delete set null;

UNLOCK TABLES;

REPAIR TABLE
  address,
  charge_box,
  connector,
  connector_meter_value,
  connector_status,
  ocpp_tag,
  reservation,
  settings,
  `transaction`,
  `user`;

OPTIMIZE TABLE
  address,
  charge_box,
  connector,
  connector_meter_value,
  connector_status,
  ocpp_tag,
  reservation,
  settings,
  `transaction`,
  `user`;
