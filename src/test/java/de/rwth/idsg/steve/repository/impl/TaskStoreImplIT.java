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

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with assistance from GPT-5.3-Codex
 */
public class TaskStoreImplIT extends AbstractRepositoryITBase {

    @Autowired
    private DSLContext dslContext;
    @Autowired
    private TaskStore repository;

    @BeforeEach
    public void setup() {
        resetDatabase(dslContext);
        repository.clearFinished();
        repository.clearUnfinished();
    }

    @Test
    public void getOverview() {
        Integer taskId = repository.add(task(false));
        var overview = assertNoDatabaseException(repository::getOverview);
        Assertions.assertFalse(overview.isEmpty());
        Assertions.assertEquals(taskId, overview.get(0).getTaskId());
    }

    @Test
    public void get() {
        Integer taskId = repository.add(task(false));
        var task = assertNoDatabaseException(() -> repository.get(taskId));
        Assertions.assertNotNull(task);
    }

    @Test
    public void add() {
        var task = task(false);
        Integer taskId = assertNoDatabaseException(() -> repository.add(task));
        Assertions.assertEquals(task.getTaskId(), taskId);
    }

    @Test
    public void clearFinished() {
        Integer finishedId = repository.add(task(true));
        Integer unfinishedId = repository.add(task(false));

        assertNoDatabaseException(repository::clearFinished);

        Assertions.assertThrows(SteveException.class, () -> repository.get(finishedId));
        Assertions.assertNotNull(repository.get(unfinishedId));
    }

    @Test
    public void clearUnfinished() {
        Integer finishedId = repository.add(task(true));
        Integer unfinishedId = repository.add(task(false));

        assertNoDatabaseException(repository::clearUnfinished);

        Assertions.assertNotNull(repository.get(finishedId));
        Assertions.assertThrows(SteveException.class, () -> repository.get(unfinishedId));
    }

    private static de.rwth.idsg.steve.ocpp.CommunicationTask task(boolean finished) {
        var params = new MultipleChargePointSelect();
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppProtocol.V_16_JSON, "station")));
        var task = new ClearCacheTask(params);
        if (finished) {
            task.addNewResponse("station", "response");
        }
        return task;
    }
}
