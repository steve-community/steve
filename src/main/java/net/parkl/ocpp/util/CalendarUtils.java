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
package net.parkl.ocpp.util;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

public class CalendarUtils {
    public static Date getLastMomentOfDay(Date date) {
        Calendar c = getInstance();
        c.setTime(date);
        c.set(HOUR_OF_DAY, 23);
        c.set(MINUTE, 59);
        c.set(SECOND, 59);
        c.set(MILLISECOND, 999);
        return c.getTime();
    }

    public static Date getFirstMomentOfDay(Date date) {
        Calendar c = getInstance();
        c.setTime(date);
        c.set(HOUR_OF_DAY, 0);
        c.set(MINUTE, 0);
        c.set(SECOND, 0);
        c.set(MILLISECOND, 0);
        return c.getTime();
    }

    public static Date createDaysBeforeNow(int days) {
        Calendar c = getInstance();
        c.add(DAY_OF_YEAR, -days);
        return c.getTime();
    }
}
