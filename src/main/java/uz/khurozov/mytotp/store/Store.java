package uz.khurozov.mytotp.store;

import uz.khurozov.mytotp.crypto.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Store {
    private final File file;
    private final SecretKey secretKey;

    private final Map<String, TotpData> data;

    public Store(StoreFileData storeFileData) {
        try {
            file = storeFileData.file();

            secretKey = CryptoUtil.getSecretKey(
                    storeFileData.password().toCharArray(),
                    storeFileData.username().getBytes(StandardCharsets.UTF_8)
            );

            data = new LinkedHashMap<>();
            init();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        if (file.exists()) {
            loadFromFile();
        } else {
            loadToFile();
        }
    }

    private void loadFromFile() {
        try {
            byte[] decrypted = CryptoUtil.decrypt(secretKey, Files.readAllBytes(file.toPath()));
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(decrypted));

            int n = inputStream.readInt();

            for (int i = 0; i < n; i++) {
                try {
                    TotpData totpData = (TotpData) inputStream.readObject();
                    data.put(totpData.name(), totpData);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadToFile() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

            outputStream.writeInt(data.size());

            for (TotpData totpData : data.values()) {
                outputStream.writeObject(totpData);
            }

            Files.write(file.toPath(), CryptoUtil.encrypt(secretKey, byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<TotpData> getAllData() {
        return data.values();
    }

    public void add(TotpData totpData) {
        data.put(totpData.name(), totpData);
        loadToFile();
    }

    public void deleteByName(String name) {
        if (data.remove(name) != null) {
            loadToFile();
        }
    }

    public boolean existsByName(String name) {
        return data.containsKey(name);
    }
}
