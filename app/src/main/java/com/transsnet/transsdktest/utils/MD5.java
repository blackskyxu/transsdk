package com.transsnet.transsdktest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final int STREAM_BUFFER_LENGTH = 1024;
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public MD5() {
    }

    public static byte[] encrypt(String text) {
        return encrypt(text.getBytes());
    }

    public static byte[] encrypt(byte[] bytes) {
        try {
            MessageDigest digest = getDigest("MD5");
            digest.update(bytes);
            return digest.digest();
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(InputStream is) throws NoSuchAlgorithmException, IOException {
        return updateDigest(getDigest("MD5"), is).digest();
    }

    public static String encryptToHexStr(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                byte[] in = text.getBytes();
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(in);
                byte[] out = messageDigest.digest();
                return bytesToHexStr(out);
            } catch (Exception var4) {
                return null;
            }
        } else {
            return "";
        }
    }

    private static MessageDigest getDigest(String algorithm) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm);
    }

    private static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[1024];

        int size;
        while ((size = data.read(buffer, 0, 1024)) > -1) {
            digest.update(buffer, 0, size);
        }

        return digest;
    }

    private static String bytesToHexStr(byte[] bytes) {
        char[] outChars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; ++i) {
            outChars[i * 2] = DIGITS_LOWER[bytes[i] >>> 4 & 15];
            outChars[i * 2 + 1] = DIGITS_LOWER[bytes[i] & 15];
        }

        return new String(outChars);
    }

    public static String getMD5(String content) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.update(content.getBytes());
            return getHashString(var1);
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder var1 = new StringBuilder();
        byte[] var2 = digest.digest();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            var1.append(Integer.toHexString(var5 >> 4 & 15));
            var1.append(Integer.toHexString(var5 & 15));
        }

        return var1.toString();
    }
}
