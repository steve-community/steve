package de.rwth.idsg.steve.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -cp steve.war de.rwth.idsg.steve.utils.PasswordHashUtil <password>");
            System.out.println("Example: java -cp steve.war de.rwth.idsg.steve.utils.PasswordHashUtil mySecurePassword123");
            System.exit(1);
        }

        String password = args[0];
        String hash = hashPassword(password);

        System.out.println("BCrypt hash for password:");
        System.out.println(hash);
        System.out.println();
        System.out.println("To set this password for a charge point, run:");
        System.out.println("UPDATE charge_box SET auth_password = '" + hash + "' WHERE charge_box_id = 'YOUR_CHARGE_BOX_ID';");
    }
}