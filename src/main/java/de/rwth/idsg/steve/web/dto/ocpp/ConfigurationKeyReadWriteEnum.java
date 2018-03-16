package de.rwth.idsg.steve.web.dto.ocpp;

//Determines if a configuration key is read-only ("R") or read-write
//("RW"). In case the key is read-only, the Central System can read the value for the key using GetConfiguration, but
//not write it. In case the accessibility is read-write, the Central System can also write the value for the key using
//ChangeConfiguration.

public enum ConfigurationKeyReadWriteEnum
{
    R,
    RW
}
