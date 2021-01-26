package net.parkl.ocpp.service.cs;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.rwth.idsg.steve.web.dto.Statistics;
import net.parkl.ocpp.repositories.OcppChargeBoxRepository;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.repositories.OcppReservationRepository;
import net.parkl.ocpp.repositories.TransactionRepository;
import net.parkl.ocpp.repositories.UserRepository;

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
