package uz.khurozov.mytotp.store;

import uz.khurozov.totp.Algorithm;
import uz.khurozov.totp.TOTP;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record TotpData(
        String label,
        String secret,
        Algorithm algorithm,
        int digits,
        int period
) implements Serializable {
    public static TotpData parseUrl(String url) {
        if (!url.startsWith("otpauth://totp/")) throw new IllegalArgumentException("Not totp url '" + url + "'");

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        var ref = new Object() {
            String label = null;
            String secret = null;
            Algorithm algorithm = TOTP.DEFAULT_ALGORITHM;
            int digits = TOTP.DEFAULT_DIGITS;
            int period = TOTP.DEFAULT_PERIOD;
            String issuer = null;
        };
        
        ref.label = uri.getPath().substring(1);
        Arrays.stream(uri.getRawQuery().split("&"))
                .map(q -> q.split("="))
                .forEach(p -> {
                    switch (p[0]) {
                        case "secret" -> ref.secret = decode(p[1]);
                        case "algorithm" -> ref.algorithm = Algorithm.valueOf(p[1]);
                        case "digits" -> ref.digits = Integer.parseInt(p[1]);
                        case "period" -> ref.period = Integer.parseInt(p[1]);
                        case "issuer" -> ref.issuer = decode(p[1]);
                    }
                });

        if (ref.secret == null) throw new IllegalArgumentException("No secret param in url '" + url + "'");
        // secret might have url encoded characters
        ref.secret = URLDecoder.decode(ref.secret, StandardCharsets.UTF_8);

        // The label is used to identify the account to which a credential is associated with.
        // It is formatted as "issuer:account" when both parameters are present.
        // It is formatted as "account" when there is no Issuer.
        if (ref.issuer != null && !ref.label.startsWith(ref.issuer+":")) {
            ref.label = String.format("%s:%s", ref.issuer, ref.label);
        }

        return new TotpData(ref.label, ref.secret, ref.algorithm, ref.digits, ref.period);
    }

    public String toUrl() {
        String issuer = null;
        // label could contain the issuer
        int index = label.indexOf(":");
        if (index != -1) {
            issuer = label.substring(0, index);
        }
        return "otpauth://totp/"
                + encodePath(label)
                + "?secret=" + encode(secret)
                + "&algorithm=" + algorithm
                + "&digits=" + digits
                + "&period=" + period
                + (issuer != null ? "&issuer=" + encode(issuer) : "");
    }
    
    private static String encode(String str) {
        if (str == null) return null;
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
    
    private static String encodePath(String str) {
        if (str == null) return null;
        return URLEncoder.encode(str, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
    
    private static String decode(String str) {
        if (str == null) return null;
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
}
