package uz.khurozov.mytotp.store;

import uz.khurozov.totp.Algorithm;
import uz.khurozov.totp.TOTP;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public record TotpData(
        String name,
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
        String name = uri.getPath().substring(1);

        String rawQuery = uri.getRawQuery();
        String[] params = rawQuery.split("&");
        HashMap<String, String> queries = new HashMap<>();

        for (String param : params) {
            int i = param.indexOf('=');
            if (i > 0) {
                queries.put(param.substring(0, i), param.substring(i + 1));
            }
        }

        String s = queries.get("secret");
        if (s == null) throw new IllegalArgumentException("No secret param in url '" + url + "'");
        // secret might have url encoded characters
        String secret = URLDecoder.decode(s, StandardCharsets.UTF_8);

        String algo = queries.get("algorithm");
        Algorithm algorithm = algo != null ? Algorithm.valueOf(algo) : TOTP.DEFAULT_ALGORITHM;


        String d = queries.get("digits");
        int digits = d != null ? Integer.parseInt(d) : TOTP.DEFAULT_DIGITS;

        String p = queries.get("period");
        int period = p != null ? Integer.parseInt(p) : TOTP.DEFAULT_PERIOD;

        return new TotpData(name, secret, algorithm, digits, period);
    }

    public String toUrl() {
        return "otpauth://totp/"
                + URLEncoder.encode(name, StandardCharsets.UTF_8).replaceAll("\\+", "%20")
                + "?secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8).replaceAll("\\+", "%20")
                + "&algorithm=" + algorithm
                + "&digits=" + digits
                + "&period=" + period;
    }
}
