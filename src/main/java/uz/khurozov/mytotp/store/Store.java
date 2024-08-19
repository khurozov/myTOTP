package uz.khurozov.mytotp.store;

import uz.khurozov.mytotp.util.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

            byte[] encrypted = Files.readAllBytes(path);

            if (encrypted.length > 0) {
                byte[] decrypted = CryptoUtil.decrypt(secretKey, encrypted);
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decrypted), StandardCharsets.UTF_8))
                        .lines()
                        .forEach(url -> {
                            try {
                                TotpData totpData = TotpData.parseUrl(url);
                                store.data.put(totpData.label(), totpData);
                            } catch (Exception ignored) {}
                        });
            }

            return store;
        } catch (IOException e) {
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
            String lines = data.values().stream()
                    .map(TotpData::toUrl)
                    .collect(Collectors.joining("\n"));

            Files.write(path, CryptoUtil.encrypt(secretKey, lines.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<TotpData> getAllData() {
        return data.values();
    }

    public void add(TotpData totpData) {
        data.put(totpData.label(), totpData);
        syncToFile();
    }

    public void deleteByLabel(String label) {
        if (data.remove(label) != null) {
            syncToFile();
        }
    }

    public boolean existsByLabel(String label) {
        return data.containsKey(label);
    }
}
