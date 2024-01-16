package uz.khurozov.mytotp.util;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class CryptoUtil {
    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] encrypt(SecretKey secretKey, byte[] input) {
        try {
            byte[] iv = gcmRandomIv();

            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] encryptedData = cipher.doFinal(input);

            byte[] output = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, output, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, output, GCM_IV_LENGTH, encryptedData.length);

            return output;
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] input) {
        try {
            int encryptedDataLength = input.length - GCM_IV_LENGTH;
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedDataLength];
            System.arraycopy(input, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(input, GCM_IV_LENGTH, encryptedData, 0, encryptedDataLength);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            return cipher.doFinal(encryptedData);
        } catch (AEADBadTagException e) {
            throw new RuntimeException("Wrong username or password");
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getSecretKey(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private static byte[] gcmRandomIv() {
        byte[] bytes = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }
}
