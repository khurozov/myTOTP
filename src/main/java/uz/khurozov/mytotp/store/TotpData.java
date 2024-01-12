package uz.khurozov.mytotp.store;

import uz.khurozov.totp.Algorithm;

import java.io.Serializable;

public record TotpData(String name, String secret, Algorithm algorithm, int digits, int period) implements Serializable { }
