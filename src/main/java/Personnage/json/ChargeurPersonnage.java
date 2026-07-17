package Personnage.json;

import com.google.gson.Gson;
import Personnage.PersonnageBase;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Charge les personnages depuis les fichiers JSON dans src/main/resources/personnages/.
 *
 * Usage :
 *   PersonnageBase gaara = ChargeurPersonnage.charger("Naruto/gaara.json");
 *   PersonnageBase vegeta = ChargeurPersonnage.charger("DragonBallZ/vegeta.json");
 *
 * Les données sont mises en cache — charger le même fichier deux fois
 * ne relit pas le disque, mais crée bien deux instances distinctes.
 */
public class ChargeurPersonnage {

    private static final Gson GSON = new Gson();
    private static final String RACINE = "/personnages/";

    /** Cache des PersonnageData brutes (pas des instances). */
    private static final Map<String, PersonnageData> CACHE = new HashMap<>();

    /**
     * Charge et retourne une nouvelle instance de personnage depuis un fichier JSON.
     *
     * @param chemin Chemin relatif depuis /personnages/ (ex: "Naruto/gaara.json")
     * @return Instance prête pour le combat, ou null si le fichier est introuvable.
     */
    public static PersonnageBase charger(String chemin) {
        PersonnageData data = CACHE.computeIfAbsent(chemin, ChargeurPersonnage::lireJson);
        if (data == null) return null;
        return new PersonnageJson(data);
    }

    /**
     * Vérifie si un fichier JSON existe pour ce chemin.
     */
    public static boolean existe(String chemin) {
        return ChargeurPersonnage.class.getResourceAsStream(RACINE + chemin) != null;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static PersonnageData lireJson(String chemin) {
        String ressource = RACINE + chemin;
        InputStream is = ChargeurPersonnage.class.getResourceAsStream(ressource);
        if (is == null) {
            System.err.println("[ChargeurPersonnage] Fichier introuvable : " + ressource);
            return null;
        }
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, PersonnageData.class);
        } catch (Exception e) {
            System.err.println("[ChargeurPersonnage] Erreur lecture " + ressource + " : " + e.getMessage());
            return null;
        }
    }
}