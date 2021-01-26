package net.parkl.ocpp.service.cs;

import net.parkl.ocpp.entities.OcppAddress;
import net.parkl.ocpp.repositories.OcppAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ocppAddressService")
public class AddressServiceImpl implements AddressService {
	@Autowired
	private OcppAddressRepository addressRepo;
	
	@Override
	@Transactional
	public OcppAddress saveAddress(de.rwth.idsg.steve.web.dto.Address dto) {
		OcppAddress addr = null;
		if (dto.getAddressPk()!=null) {
			addr = addressRepo.findById(dto.getAddressPk()).orElse(null);
		} else {
			addr = new OcppAddress();
		}
		addr.setCity(dto.getCity());
		addr.setCountry(dto.getCountryAlpha2OrNull());
		addr.setHouseNumber(dto.getHouseNumber());
		addr.setStreet(dto.getStreet());
		addr.setZipCode(dto.getZipCode());
		return addressRepo.save(addr);
	}

}
