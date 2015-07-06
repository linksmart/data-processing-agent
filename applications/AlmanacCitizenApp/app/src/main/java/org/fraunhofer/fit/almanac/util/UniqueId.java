package org.fraunhofer.fit.almanac.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by Werner-Kytölä on 08.05.2015.
 */
public class UniqueId {
    public static String generateUUID(){
        try {
            return sha256(UUID.randomUUID().toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    static String sha256(String input) throws NoSuchAlgorithmException {

        final MessageDigest mDigest = MessageDigest.getInstance("SHA-256");

        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}