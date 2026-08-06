package lancement.Gestionnaires;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Authentification anonyme Firebase : fournit un idToken valide pour signer les requetes
 * REST vers la Realtime Database (parametre "?auth="). Le token est mis en cache et
 * rafraichi automatiquement avant expiration via le refreshToken (pas de nouvelle
 * inscription anonyme a chaque appel, ce qui creerait un utilisateur Firebase different
 * a chaque fois et perdrait le lien avec les donnees deja sauvegardees).
 */
public class FirebaseAuth {

    private static final String WEB_API_KEY = "AIzaSyBwOVVt0YiHDtZgaYQFp21qCkmndNXfgfQ";
    private static final String SIGNUP_URL  = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + WEB_API_KEY;
    private static final String REFRESH_URL = "https://securetoken.googleapis.com/v1/token?key=" + WEB_API_KEY;

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String idToken;
    private static String refreshToken;
    private static long   expirationMillis;

    private FirebaseAuth() {}

    /** Retourne un idToken valide, en se connectant ou en le rafraichissant si necessaire. */
    public static synchronized String getIdToken() {
        try {
            if (idToken == null) {
                signInAnonymement();
            } else if (System.currentTimeMillis() >= expirationMillis) {
                rafraichir();
            }
        } catch (Exception e) {
            System.out.println("Erreur authentification Firebase : " + e.getMessage());
        }
        return idToken;
    }

    /** Ajoute le jeton d'authentification a une URL Firebase (avant l'envoi de la requete). */
    public static String avecAuth(String url) {
        String token = getIdToken();
        if (token == null) return url; // pas de token dispo (hors-ligne) : la requete echouera cote regles Firebase
        return url + (url.contains("?") ? "&" : "?") + "auth=" + token;
    }

    private static void signInAnonymement() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"returnSecureToken\":true}", StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Echec connexion anonyme (code " + res.statusCode() + ") : " + res.body());
        }
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        idToken          = json.get("idToken").getAsString();
        refreshToken     = json.get("refreshToken").getAsString();
        long expiresIn   = Long.parseLong(json.get("expiresIn").getAsString());
        expirationMillis = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
    }

    private static void rafraichir() throws Exception {
        String payload = "grant_type=refresh_token&refresh_token=" + refreshToken;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(REFRESH_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            // Refresh token expire/revoque : on retente une connexion anonyme complete.
            signInAnonymement();
            return;
        }
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        idToken          = json.get("id_token").getAsString();
        refreshToken     = json.get("refresh_token").getAsString();
        long expiresIn   = Long.parseLong(json.get("expires_in").getAsString());
        expirationMillis = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
    }
}
