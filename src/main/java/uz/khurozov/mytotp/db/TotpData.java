package uz.khurozov.mytotp.db;

import uz.khurozov.totp.HMAC;

public record TotpData(String name, String secret, HMAC hmac, int passwordLength, long timeStep) { }
