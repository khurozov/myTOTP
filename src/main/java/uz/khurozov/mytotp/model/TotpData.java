package uz.khurozov.mytotp.model;

import uz.khurozov.totp.HMAC;

public record TotpData(Integer id, String name, String secret, HMAC hmac, int passwordLength, long timeStep) { }
