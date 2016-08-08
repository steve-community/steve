package de.rwth.idsg.steve.service;

import com.google.common.base.Strings;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.MailSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 24.01.2016
 */
@Slf4j
@Service
public class MailService {

    @Autowired private SettingsRepository settingsRepository;
    @Autowired private ScheduledExecutorService executorService;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private MailSettings settings;
    private Session session;

    @PostConstruct
    public void loadSettingsFromDB() {
        writeLock.lock();
        try {
            settings = settingsRepository.getMailSettings();
            session = createSession(settings);
        } finally {
            writeLock.unlock();
        }
    }

    public MailSettings getSettings() {
        readLock.lock();
        try {
            return this.settings;
        } finally {
            readLock.unlock();
        }
    }

    public void sendTestMail() {
        try {
            send("Test", "Test");
        } catch (MessagingException e) {
            throw new SteveException("Failed to send mail", e);
        }
    }

    public void sendAsync(String subject, String body) {
        executorService.execute(() -> {
            try {
                send(subject, body);
            } catch (MessagingException e) {
                log.error("Failed to send mail", e);
            }
        });
    }

    public void send(String subject, String body) throws MessagingException {
        MailSettings settings = getSettings();

        Message mail = new MimeMessage(session);
        mail.setSubject("[SteVe] " + subject);
        mail.setContent(body, "text/plain");
        mail.setFrom(new InternetAddress(settings.getFrom()));

        for (String rep : settings.getRecipients()) {
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(rep));
        }

        Transport transport = session.getTransport();
        try {
            transport.connect();
            transport.sendMessage(mail, mail.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Session createSession(MailSettings settings) {
        Properties props = new Properties();
        String protocol = settings.getProtocol();

        props.setProperty("mail.host", "" + settings.getHost());
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
