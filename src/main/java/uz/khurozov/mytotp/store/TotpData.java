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
        String label = uri.getPath().substring(1);

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

        // The issuer is an optional string value indicating the provider or service the credential is associated with.
        String issuer = decode(queries.get("issuer"));

        // The label is used to identify the account to which a credential is associated with.
        // It is formatted as "issuer:account" when both parameters are present.
        // It is formatted as "account" when there is no Issuer.
        if (issuer != null && !label.startsWith(issuer+":")) {
            label = String.format("%s:%s", issuer, label);
        }

        return new TotpData(label, secret, algorithm, digits, period);
    }

    public String toUrl() {
        String issuer = null;
        // name is the label actually, it could contain the issuer
        int index = name.indexOf(":");
        if (index != -1) {
            issuer = name.substring(0, index);
        }
        return "otpauth://totp/"
                + encodePath(name)
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
