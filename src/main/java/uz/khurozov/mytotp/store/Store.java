package uz.khurozov.mytotp.store;

import uz.khurozov.mytotp.crypto.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Store {
    private final Path path;
    private final SecretKey secretKey;

    private final Map<String, TotpData> data;

    private Store(Path path, SecretKey secretKey) {
        this.path = path;
        this.secretKey = secretKey;
        this.data = new LinkedHashMap<>();
    }

    public static Store open(Path path, SecretKey secretKey) {
        try {
            Store store = new Store(path, secretKey);

            byte[] decrypted = CryptoUtil.decrypt(secretKey, Files.readAllBytes(path));
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(decrypted));

            while (true) {
                try {
                    TotpData totpData = (TotpData) inputStream.readObject();
                    store.data.put(totpData.name(), totpData);
                } catch (EOFException e) {
                    break;
                }
            }

            return store;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Store create(Path path, SecretKey secretKey) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.createFile(path);
            return new Store(path, secretKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void syncToFile() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

            for (TotpData totpData : data.values()) {
                outputStream.writeObject(totpData);
            }

            Files.write(path, CryptoUtil.encrypt(secretKey, byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<TotpData> getAllData() {
        return data.values();
    }

    public void add(TotpData totpData) {
        data.put(totpData.name(), totpData);
        syncToFile();
    }

    public void deleteByName(String name) {
        if (data.remove(name) != null) {
            syncToFile();
        }
    }

    public boolean existsByName(String name) {
        return data.containsKey(name);
    }
}
