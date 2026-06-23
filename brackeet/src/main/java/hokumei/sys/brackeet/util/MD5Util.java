package hokumei.sys.brackeet.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            String hex = number.toString(16);
            // Pad to 32 characters
            return String.format("%32s", hex).replace(' ', '0');
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
