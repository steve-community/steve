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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.NotificationFeature;
import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void getUserForMail_testEmpty() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of();

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNull(user);
    }

    @Test
    public void getUserForMail_mailMissing() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, ocppTag)))
                .notificationFeatures(List.of(NotificationFeature.OcppTransactionStarted))
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNull(user);
    }

    @Test
    public void getUserForMail_emptyNotifications() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .email("foo@bar.com")
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, ocppTag)))
                .notificationFeatures(List.of())
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNull(user);
    }

    @Test
    public void getUserForMail_differentNotification() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .email("foo@bar.com")
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, ocppTag)))
                .notificationFeatures(List.of(NotificationFeature.OcppTransactionEnded))
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNull(user);
    }

    @Test
    public void getUserForMail_ocppTagPartial() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .email("foo@bar.com")
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, "abc1234xyz")))
                .notificationFeatures(List.of(NotificationFeature.OcppTransactionStarted))
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNull(user);
    }

    @Test
    public void getUserForMail_happyCase() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .email("foo@bar.com")
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, ocppTag)))
                .notificationFeatures(List.of(NotificationFeature.OcppTransactionStarted))
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user, usersFromRepo.getFirst());
    }

    @Test
    public void getUserForMail_ocppTagCaseInsensitive() {
        // given
        String ocppTag = "abc1234";
        List<User.Overview> usersFromRepo = List.of(
            User.Overview.builder()
                .email("foo@bar.com")
                .ocppTagEntries(List.of(new User.OcppTagEntry(1, "ABC1234")))
                .notificationFeatures(List.of(NotificationFeature.OcppTransactionStarted))
                .build()
        );

        // when
        when(userRepository.getOverview(any())).thenReturn(usersFromRepo);

        // then
        var user = userService.getUserForMail(ocppTag, NotificationFeature.OcppTransactionStarted);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user, usersFromRepo.getFirst());
    }
}
