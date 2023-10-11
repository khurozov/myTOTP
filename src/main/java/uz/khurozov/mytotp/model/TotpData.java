package uz.khurozov.mytotp.model;

import uz.khurozov.totp.HMAC;
import uz.khurozov.totp.TOTP;

public record TotpData(Integer id, String name, String secret, HMAC hmac, int passwordLength, long timeStep) {
    public TOTP getTOTP() {
        return new TOTP(hmac, secret, passwordLength, timeStep);
    }
}
