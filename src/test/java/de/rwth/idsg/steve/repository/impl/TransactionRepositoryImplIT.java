/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.repository.TransactionRepository;
import de.rwth.idsg.steve.web.dto.TransactionQueryForm;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
public class TransactionRepositoryImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
    }

    @Test
    public void getTransactions() {
        assertNoDatabaseException(() -> repository.getTransactions(new TransactionQueryForm()));
    }

    @Test
    public void writeTransactionsCSV() {
        assertNoDatabaseException(() -> repository.writeTransactionsCSV(new TransactionQueryForm(), new StringWriter()));
    }

    @Test
    public void getActiveTransactionIds() {
        assertNoDatabaseException(() -> repository.getActiveTransactionIds(KNOWN_CHARGE_BOX_ID));
    }

    @Test
    public void getDetails() {
        assertNoDatabaseException(() -> repository.getDetails(1));
    }

    @Test
    public void getStoppedTransactions() {
        assertNoDatabaseException(() -> repository.getStoppedTransactions(DateTime.now().minusDays(1), DateTime.now()));
    }
}

