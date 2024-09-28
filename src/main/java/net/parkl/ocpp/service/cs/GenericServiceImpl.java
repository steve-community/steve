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

import de.rwth.idsg.steve.web.dto.Statistics;
import net.parkl.ocpp.repositories.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericServiceImpl implements GenericService {
	@Autowired
	private OcppChargeBoxRepository chargeBoxRepo;
	@Autowired
	private OcppTagRepository tagRepo;
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private OcppReservationRepository reservationRepo;
	@Autowired
	private TransactionRepository transactionRepo;



	@Override
	public Statistics getStats() {
		int chargeBoxCount=(int) chargeBoxRepo.count();
		int tagCount=(int) tagRepo.count();
		int userCount=(int) userRepo.count();
		DateTime now = DateTime.now();
		
		int resCount=(int) reservationRepo.countByExpiryDateGreater(now.toDate());
		
		int trCount=(int) transactionRepo.countByStopTimestampIsNull();
		
		DateTime todayStart=now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		
		int heartbeatsToday=(int)chargeBoxRepo.countLastHeartBeatAfter(todayStart.toDate());
		
		DateTime yesterdayStart = todayStart.minusDays(1);
		int heartbeatsYesterday=(int)chargeBoxRepo.countLastHeartBeatBetween(yesterdayStart.toDate(),todayStart.toDate());
		
		int heartbeatsBefore=(int)chargeBoxRepo.countLastHeartBeatBefore(yesterdayStart.toDate());
		
		return Statistics.builder()
                .numChargeBoxes(chargeBoxCount)
                .numOcppTags(tagCount)
                .numUsers(userCount)
                .numReservations(resCount)
                .numTransactions(trCount)
                .heartbeatToday(heartbeatsToday)
                .heartbeatYesterday(heartbeatsYesterday)
                .heartbeatEarlier(heartbeatsBefore)
                .build();
	}

}
