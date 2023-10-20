package uz.khurozov.mytotp.store;

import java.io.File;

public record StoreFileData(File file, String username, String password) {}
