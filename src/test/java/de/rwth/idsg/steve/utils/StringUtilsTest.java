/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 05.10.2021
 */
public class StringUtilsTest {

    @Test
    public void testOperationName_ocpp12andMultiple() {
        var operationName = StringUtils.getOperationName(new ClearCacheTask(null, new MultipleChargePointSelect()));
        Assertions.assertEquals("Clear Cache", operationName);
    }

    @Test
    public void testOperationName_ocpp15andSingle() {
        var operationName = StringUtils.getOperationName(new CancelReservationTask(null, new CancelReservationParams(), null));
        Assertions.assertEquals("Cancel Reservation", operationName);
    }

    @Test
    public void testOperationName_ocpp16() {
        var operationName = StringUtils.getOperationName(new GetCompositeScheduleTask(null, new GetCompositeScheduleParams()));
        Assertions.assertEquals("Get Composite Schedule", operationName);
    }

    @Test
    public void testJoinByComma_inputNull() {
        String val = StringUtils.joinByComma(null);
        Assertions.assertNull(val);
    }

    @Test
    public void testJoinByComma_inputEmpty() {
        String val = StringUtils.joinByComma(new ArrayList<>());
        Assertions.assertNull(val);
    }

    @Test
    public void testJoinByComma_inputOneElement() {
        String val = StringUtils.joinByComma(Arrays.asList("hey"));
        Assertions.assertEquals("hey", val);
    }

    @Test
    public void testJoinByComma_inputTwoElements() {
        String val = StringUtils.joinByComma(Arrays.asList("hey", "ho"));
        Assertions.assertEquals("hey,ho", val);
    }

    @Test
    public void testJoinByComma_inputDuplicateElements() {
        String val = StringUtils.joinByComma(Arrays.asList("hey", "ho", "hey"));
        Assertions.assertEquals("hey,ho", val);
    }


    @Test
    public void testSplitByComma_inputNull() {
        List<String> val = StringUtils.splitByComma(null);
        Assertions.assertNotNull(val);
        Assertions.assertTrue(val.isEmpty());
    }

    @Test
    public void testSplitByComma_inputEmpty() {
        List<String> val = StringUtils.splitByComma("");
        Assertions.assertNotNull(val);
        Assertions.assertTrue(val.isEmpty());
    }

    @Test
    public void testSplitByComma_inputOneElement() {
        List<String> val = StringUtils.splitByComma("1one");
        Assertions.assertTrue(val.size() == 1);
        Assertions.assertEquals("1one", val.get(0));
    }

    @Test
    public void testSplitByComma_inputTwoElements() {
        List<String> val = StringUtils.splitByComma("1one,2two");
        Assertions.assertTrue(val.size() == 2);

        List<String> sortedVal = val.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals("1one", sortedVal.get(0));
        Assertions.assertEquals("2two", sortedVal.get(1));
    }

    @Test
    public void testGetLastBitFromUrl_empty() {
        String in = "";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_null() {
        String in = null;
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_successFull() {
        String in = "https://www.google.com/steve/websocket/CentralSystemService/BBEI12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_noPostfix() {
        String in = "/steve/websocket/CentralSystemService/";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("", out);
    }

    @Test
    public void testGetLastBitFromUrl_successPartial() {
        String in = "/steve/websocket/CentralSystemService/BBEI12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithPercent() {
        String in = "/steve/websocket/CentralSystemService/BBE%I12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE%I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithDash() {
        String in = "/steve/websocket/CentralSystemService/BBE-I12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE-I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithSpace() {
        String in = "/steve/websocket/CentralSystemService/BBE I12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("BBE I12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successWithExtraSlash() {
        String in = "/steve/websocket/CentralSystemService/889/BBEI12";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("889/BBEI12", out);
    }

    @Test
    public void testGetLastBitFromUrl_successComplex() {
        String in = "/steve/websocket/CentralSystemService/%889 /BBEI12-";
        String out = StringUtils.getLastBitFromUrl(in);
        Assertions.assertEquals("%889 /BBEI12-", out);
    }
}
