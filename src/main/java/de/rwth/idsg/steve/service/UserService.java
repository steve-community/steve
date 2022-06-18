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
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.repository.UserRepository;
import de.rwth.idsg.steve.repository.dto.User;
import de.rwth.idsg.steve.repository.dto.User.Overview;
import de.rwth.idsg.steve.service.notification.UserCreated;
import de.rwth.idsg.steve.service.notification.UserDeleted;
import de.rwth.idsg.steve.service.notification.UserUpdated;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import java.util.List;
import jooq.steve.db.tables.records.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private ApplicationEventPublisher applicationEventPublisher;
  @Autowired private UserRepository userRepository;

  public List<Overview> getOverview(UserQueryForm form) {
    return userRepository.getOverview(form);
  }

  public User.Details getDetails(int userPk) {
    return userRepository.getDetails(userPk);
  }

  public void add(UserForm form) {
    int userPk = userRepository.add(form);
    applicationEventPublisher.publishEvent(new UserCreated(userPk, form.getEMail()));
  }

  public void update(UserForm form) {
    userRepository.update(form);
    applicationEventPublisher.publishEvent(new UserUpdated(form.getUserPk(), form.getEMail()));
  }

  public void delete(int userPk) {
    UserRecord user = userRepository.getDetails(userPk).getUserRecord();
    userRepository.delete(userPk);
    applicationEventPublisher.publishEvent(new UserDeleted(userPk, user.getEMail()));
  }
}
