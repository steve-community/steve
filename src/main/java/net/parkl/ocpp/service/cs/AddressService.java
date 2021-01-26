package net.parkl.ocpp.service.cs;

import net.parkl.ocpp.entities.OcppAddress;

public interface AddressService {
	OcppAddress saveAddress(de.rwth.idsg.steve.web.dto.Address addressDto);
}
