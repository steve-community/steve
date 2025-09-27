package de.rwth.idsg.steve.gateway.oicp.model;

public enum AuthenticationMode {
    NFC_RFID_CLASSIC,
    NFC_RFID_DESFIRE,
    PNC,
    REMOTE,
    DIRECT_PAYMENT,
    NO_AUTHENTICATION_REQUIRED
}