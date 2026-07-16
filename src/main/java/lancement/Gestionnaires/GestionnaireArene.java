package lancement.Gestionnaires;

import Personnage.PersonnageBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GestionnaireArene {

    private static final String URL_FIREBASE =
        "https://rpgjava-d89d3-default-rtdb.europe-west1.firebasedatabase.app/arene/classement/";

    private static final int TAILLE_CLASSEMENT = 100;

    // ── Listes par rôle ───────────────────────────────────────────────────

    private static final List<String> TANKS = List.of(
        "Naruto", "Iruka", "Choji", "Hinata", "Neji", "Hidan",
        "Tien", "Erza", "Nappa", "Gaara", "Cell", "Rogue"
    );

    private static final List<String> SUPPORTS = List.of(
        "Ino", "Kurenai", "Shikamaru", "Kabuto", "Temari", "Asuma",
        "Shino", "Haku", "Chiaotzu", "Krillin", "Evergreen", "Freed",
        "Cana", "Lucy", "C-18", "Yukino", "Piccolo", "Sasori",
        "Jubia", "Wendy"
    );

    private static final List<String> DPS = List.of(
        "Sasuke", "Sakura", "Kakashi", "Lee", "Tenten", "Kiba",
        "Gai", "Itachi", "Orochimaru", "Kankuro", "Deidara", "Zabuza",
        "Sangoku", "Raditz", "Yamcha", "C-17", "Vegeta", "Freezer",
        "Gohan (enfant)",
        "Natsu", "Gray", "Elfman", "Loke", "Alzack", "Bisca",
        "Bickslow", "Mirajane", "Mirajane Halphas", "Natsu Etherion",
        "Sting", "Gajeel", "Angel"
    );

    private static final String[] CLASSES_IA = {"Mage", "Ninja", "Guerrier"};

    private static final String[] PREFIXES = {
        "Shadow", "Dark", "Storm", "Iron", "Blazing",
        "Frozen", "Silent", "Raging", "Swift", "Ancient"
    };
    private static final String[] SUFFIXES = {
        "Warrior", "Hunter", "Sage", "Blade", "Fist",
        "Wolf", "Dragon", "Phoenix", "Oni", "Ghost"
    };

    private final List<AreneData>                  classement = new ArrayList<>();
    private final Gson                             gson       = new GsonBuilder().create();
    private final HttpClient                       client     = HttpClient.newHttpClient();
    private final Function<String, PersonnageBase> factory;

    public GestionnaireArene(Function<String, PersonnageBase> factory) {
        this.factory = factory;
    }

    // ── Génération des faux joueurs ───────────────────────────────────────

    public void genererFauxJoueurs() {
        classement.clear();
        Random rng = new Random(42L);

        for (int rang = 1; rang <= TAILLE_CLASSEMENT; rang++) {
            List<String> equipe = genererEquipeEquilibree(rng);
            String classeIA     = CLASSES_IA[rng.nextInt(CLASSES_IA.length)];
            String principal    = "PP_" + classeIA;
            int niveauMoyen     = 50 - (int)((rang - 1) * 0.4);
            int points          = 10000 - (rang - 1) * 95;

            classement.add(new AreneData(
                "CPU_RANG_" + rang,
                genererPseudoIA(rang),
                true, rang, points, 0,
                equipe, principal, niveauMoyen,
                System.currentTimeMillis()
            ));
        }
    }

    /**
     * Génère une équipe de 4 personnages équilibrée :
     * - 1 tank obligatoire
     * - 3 slots libres (DPS ou Support)
     * Le 5ème slot est le personnage principal IA (géré dans MenuArene)
     */
   private List<String> genererEquipeEquilibree(Random rng) {
    List<String> equipe = new ArrayList<>();

    // 1 tank obligatoire
    List<String> tanksDispo = new ArrayList<>(TANKS);
    Collections.shuffle(tanksDispo, rng);
    equipe.add(tanksDispo.get(0));

    // 4 combinaisons DPS/Support pour les 3 slots restants
    // Le personnage principal (DPS) compte déjà comme DPS obligatoire
    int nbDPS;
    int nbSupport;
    switch (rng.nextInt(4)) {
        case 0 -> { nbDPS = 0; nbSupport = 3; } // PP + 0DPS + 3S + 1Tank
        case 1 -> { nbDPS = 1; nbSupport = 2; } // PP + 1DPS + 2S + 1Tank
        case 2 -> { nbDPS = 2; nbSupport = 1; } // PP + 2DPS + 1S + 1Tank
        default -> { nbDPS = 3; nbSupport = 0; } // PP + 3DPS + 0S + 1Tank
    }

    // Piocher les DPS
    List<String> dpsDispo = new ArrayList<>(DPS);
    Collections.shuffle(dpsDispo, rng);
    for (int i = 0; i < nbDPS && i < dpsDispo.size(); i++)
        equipe.add(dpsDispo.get(i));

    // Piocher les Supports
    List<String> supportsDispo = new ArrayList<>(SUPPORTS);
    Collections.shuffle(supportsDispo, rng);
    for (int i = 0; i < nbSupport && i < supportsDispo.size(); i++)
        equipe.add(supportsDispo.get(i));

    return equipe; // 4 membres — le 5ème = personnage principal IA
}

    private static String genererPseudoIA(int rang) {
        Random rng    = new Random(rang * 31L);
        String prefix = PREFIXES[rng.nextInt(PREFIXES.length)];
        String suffix = SUFFIXES[rng.nextInt(SUFFIXES.length)];
        int number    = 100 + rng.nextInt(900);
        return prefix + suffix + "#" + number;
    }

    // ── Chargement Firebase ───────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public void chargerDepuisFirebase() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(URL_FIREBASE + ".json"))
                .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() != 200 || res.body().trim().equals("null")) {
                System.out.println("[Arène] Aucune donnée Firebase, génération des faux joueurs...");
                genererFauxJoueurs();
                return;
            }

            Map<String, Map<String, Object>> raw = gson.fromJson(
                res.body(),
                new TypeToken<Map<String, Map<String, Object>>>(){}.getType()
            );

            // Base de faux joueurs en premier
            genererFauxJoueurs();

            // Écraser les rangs occupés par de vrais joueurs
            for (Map<String, Object> data : raw.values()) {
                AreneData vrai = AreneData.depuisMap(data);
                int index = vrai.getRang() - 1;
                if (index >= 0 && index < classement.size()) {
                    classement.set(index, vrai);
                }
            }

            System.out.println("[Arène] Classement chargé (" + classement.size() + " entrées)");

        } catch (Exception e) {
            System.out.println("[Arène] Erreur chargement Firebase : " + e.getMessage());
            genererFauxJoueurs();
        }
    }

    // ── Upload Firebase ───────────────────────────────────────────────────

    public void uploaderRangJoueur(AreneData joueur) {
        joueur.setDerniereMiseAJour(System.currentTimeMillis());
        try {
            String json = gson.toJson(joueur.versMap());
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(URL_FIREBASE + securiser(joueur.getUserId()) + ".json"))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200)
                System.out.println("[Arène] Classement mis à jour !");
            else
                System.out.println("[Arène] Échec upload (code " + res.statusCode() + ")");
        } catch (Exception e) {
            System.out.println("[Arène] Erreur upload : " + e.getMessage());
        }
    }

    // ── Logique de rang ───────────────────────────────────────────────────

    public List<AreneData> getAdversairesVisibles(int rangJoueur) {
        if (classement.isEmpty()) return new ArrayList<>();

        int taille = classement.size();
        int debut  = Math.max(0, Math.min(rangJoueur - 6, taille - 1));
        int fin    = Math.min(taille, rangJoueur + 4);

        if (debut >= fin) debut = Math.max(0, fin - 1);

        return new ArrayList<>(classement.subList(debut, fin)).stream()
                .filter(a -> a.getRang() != rangJoueur)
                .collect(Collectors.toList());
    }

    public void appliquerVictoire(AreneData joueur, AreneData adversaire) {
        int rangJoueur     = joueur.getRang();
        int rangAdversaire = adversaire.getRang();

        joueur.ajouterPointsBoutique(200);
        System.out.println("  +200 points de boutique !");

        if (rangAdversaire < rangJoueur) {
            joueur.setRang(rangAdversaire);
            adversaire.setRang(rangJoueur);
            classement.set(rangAdversaire - 1, joueur);
            classement.set(rangJoueur - 1, adversaire);
            System.out.println("  Tu passes du rang " + rangJoueur
                             + " au rang " + rangAdversaire + " !");
        } else {
            System.out.println("  Victoire contre un adversaire moins bien classé.");
        }
    }

    public void appliquerDefaite(AreneData joueur) {
        joueur.ajouterPointsBoutique(100);
        System.out.println("  +100 points de boutique malgré la défaite.");
    }

    public AreneData getOuCreerJoueur(String userId, String pseudo,
                                       List<String> equipeNoms, String principalNom) {
        for (AreneData a : classement) {
            if (a.getUserId().equals(userId)) return a;
        }

        int niveauMoyen = equipeNoms.stream()
                .map(factory)
                .filter(p -> p != null)
                .mapToInt(PersonnageBase::getNiveau)
                .reduce(0, Integer::sum) / Math.max(1, equipeNoms.size());

        AreneData nouveau = new AreneData(
            userId, pseudo, false, TAILLE_CLASSEMENT, 500, 0,
            equipeNoms, principalNom, niveauMoyen,
            System.currentTimeMillis()
        );
        classement.set(TAILLE_CLASSEMENT - 1, nouveau);
        return nouveau;
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private String securiser(String id) {
        return id.trim().toLowerCase().replace(" ", "_");
    }

    public List<AreneData> getClassement() { return Collections.unmodifiableList(classement); }
}