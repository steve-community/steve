package net.parkl.ocpp.repositories;

import net.parkl.ocpp.service.driver.DriverTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TransactionRepositoryTest extends DriverTestBase {
    @Autowired
    private TransactionRepository repository;

    @Test
    void countByStopTimestampIsNullTest(){
        repository.countByStopTimestampIsNull();
    }
}