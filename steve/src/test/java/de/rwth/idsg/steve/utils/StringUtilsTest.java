/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
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
package de.rwth.idsg.steve.utils;

import de.rwth.idsg.steve.ocpp.task.CancelReservationTask;
import de.rwth.idsg.steve.ocpp.task.ClearCacheTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.web.dto.ocpp.CancelReservationParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.MultipleChargePointSelect;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.10.2021
 */
public class StringUtilsTest {

    @Test
    public void testOperationName_ocpp12andMultiple() {
        var operationName = StringUtils.getOperationName(new ClearCacheTask(new MultipleChargePointSelect()));
        assertThat(operationName).isEqualTo("Clear Cache");
    }

    @Test
    public void testOperationName_ocpp15andSingle() {
        var operationName = StringUtils.getOperationName(new CancelReservationTask(new CancelReservationParams(), null));
        assertThat(operationName).isEqualTo("Cancel Reservation");
    }

    @Test
    public void testOperationName_ocpp16() {
        var operationName = StringUtils.getOperationName(new GetCompositeScheduleTask(new GetCompositeScheduleParams()));
        assertThat(operationName).isEqualTo("Get Composite Schedule");
    }

    @Test
    public void testJoinByComma_inputNull() {
        var val = StringUtils.joinByComma(null);
        assertThat(val).isNull();
    }

    @Test
    public void testJoinByComma_inputEmpty() {
        var val = StringUtils.joinByComma(List.of());
        assertThat(val).isNull();
    }

    @Test
    public void testJoinByComma_inputOneElement() {
        var val = StringUtils.joinByComma(List.of("hey"));
        assertThat(val).isEqualTo("hey");
    }

    @Test
    public void testJoinByComma_inputTwoElements() {
        var val = StringUtils.joinByComma(List.of("hey", "ho"));
        assertThat(val).isEqualTo("hey,ho");
    }

    @Test
    public void testJoinByComma_inputDuplicateElements() {
        var val = StringUtils.joinByComma(List.of("hey", "ho", "hey"));
        assertThat(val).isEqualTo("hey,ho");
    }

    @Test
    public void testSplitByComma_inputNull() {
        var val = StringUtils.splitByComma(null);
        assertThat(val).isNotNull().isEmpty();
    }

    @Test
    public void testSplitByComma_inputEmpty() {
        var val = StringUtils.splitByComma("");
        assertThat(val).isNotNull().isEmpty();
    }

    @Test
    public void testSplitByComma_inputOneElement() {
        var val = StringUtils.splitByComma("1one");
        assertThat(val).hasSize(1);
        assertThat(val.get(0)).isEqualTo("1one");
    }

    @Test
    public void testSplitByComma_inputTwoElements() {
        var val = StringUtils.splitByComma("1one,2two");
        assertThat(val).hasSize(2);

        var sortedVal = val.stream().sorted().toList();
        assertThat(sortedVal).containsExactly("1one", "2two");
    }
}
