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

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.config.DelegatingTaskExecutor;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.web.dto.SettingsForm.MailSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 24.01.2016
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceDefault implements MailService {

    private final SettingsRepository settingsRepository;
    private final DelegatingTaskExecutor asyncTaskExecutor;

    @Override
    public MailSettings getSettings() {
        return settingsRepository.getMailSettings();
    }

    @Override
    public void sendTestMail() {
        try {
            List<String> noAddress = new ArrayList();
            send("Test", "Test", noAddress);
        } catch (MessagingException e) {
            throw new SteveException("Failed to send mail", e);
        }
    }

    @Override
    public void sendAsync(String subject, String body) {
        asyncTaskExecutor.execute(() -> {
            try {
                List<String> noAddress = new ArrayList();
                send(subject, body, noAddress);
            } catch (MessagingException e) {
                log.error("Failed to send mail", e);
            }
        });
    }

    @Override
    public void sendAsync(String subject, String body, List<String> eMailAddresses) {
        asyncTaskExecutor.execute(() -> {
            try {
                send(subject, body, eMailAddresses);
            } catch (MessagingException e) {
                log.error("Failed to send mail", e);
            }
        });
    }

    private void send(String subject, String body, List<String> eMailAddresses) throws MessagingException {
        MailSettings settings = getSettings();
        Session session = createSession(settings);

        Message mail = new MimeMessage(session);
        mail.setSubject("[SteVe] " + subject);
        mail.setContent(body, "text/plain");
        mail.setFrom(new InternetAddress(settings.getFrom()));

        if (eMailAddresses.isEmpty()) {
            eMailAddresses = settings.getRecipients();
        }

        for (String rep : eMailAddresses) {
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(rep));
        }

        try (Transport transport = session.getTransport()) {
            transport.connect();
            transport.sendMessage(mail, mail.getAllRecipients());
        }
        catch (Exception e) {
            log.error("Failed to send mail(s)! ", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Session createSession(MailSettings settings) {
        Properties props = new Properties();
        String protocol = settings.getProtocol();

        props.setProperty("mail.host", "" + settings.getMailHost());
        props.setProperty("mail.transport.protocol", "" + protocol);
        props.setProperty("mail." + protocol + ".port", "" + settings.getPort());

        if (settings.getPort() == 465) {
            props.setProperty("mail." + protocol + ".ssl.enable", "" + true);

        } else if (settings.getPort() == 587) {
            props.setProperty("mail." + protocol + ".starttls.enable", "" + true);
        }

        boolean isUserSet = !Strings.isNullOrEmpty(settings.getUsername());
        boolean isPassSet = !Strings.isNullOrEmpty(settings.getPassword());

        if (isUserSet && isPassSet) {
            props.setProperty("mail." + protocol + ".auth", "" + true);
            return Session.getInstance(props, getAuth(settings));

        } else {
            return Session.getInstance(props);
        }
    }

    private static Authenticator getAuth(MailSettings settings) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getUsername(), settings.getPassword());
            }
        };
    }
}
