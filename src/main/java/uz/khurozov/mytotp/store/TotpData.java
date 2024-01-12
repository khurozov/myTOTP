package uz.khurozov.mytotp.store;

import uz.khurozov.totp.Algorithm;

import java.io.Serializable;

public record TotpData(String name, String secret, String issuer, Algorithm algorithm, int digits, long period) implements Serializable { }
