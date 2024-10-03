/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
			addr = addressRepo.findById(dto.getAddressPk()).orElseThrow();
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
