package uz.khurozov.mytotp.store;

import uz.khurozov.totp.HMAC;

import java.io.Serializable;

public record TotpData(String name, String secret, HMAC hmac, int passwordLength, long timeStep) implements Serializable { }
