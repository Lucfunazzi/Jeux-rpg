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
import lancement.RangJoueur;

public class GestionnaireArene {

    private static final String URL_FIREBASE =
        "https://rpgjava-d89d3-default-rtdb.europe-west1.firebasedatabase.app/arene/classement/";

    private static final int TAILLE_CLASSEMENT = 100;

    // ── Pools par rareté et rôle ──────────────────────────────────────────
    // Basés sur le nom, le rang (rarete) et le rôle réels des classes du
    // package Personnage.FairyTail. Seuls les personnages déjà branchés
    // dans creerPersonnageConnu() sont utilisés ici.

    private static final List<String> TANKS_C  = List.of("Nab", "Duc Everlue", "Yuka");
    private static final List<String> TANKS_B  = List.of("Elfman", "Sol", "Simon");
    private static final List<String> TANKS_A  = List.of("Ikaruga", "Hoteye", "Sugarboy");
    private static final List<String> TANKS_S  = List.of("Erza", "Rogue", "Erza Knightwalker", "Azuma", "Rustyrose");
    private static final List<String> TANKS_SS = List.of("Jura", "Zeref");

    private static final List<String> SUPPORTS_C  = List.of("Cherry", "Miliana");
    private static final List<String> SUPPORTS_B  = List.of("Kana", "Levy", "Lisanna", "Shaw");
    private static final List<String> SUPPORTS_A  = List.of("Evergreen", "Freed", "Jubia", "Lucy", "Wendy", "Vivaldus",
            "Ichiya", "Hibiki", "Mest", "Byro", "Eve");
    private static final List<String> SUPPORTS_S  = List.of("Yukino", "Meredy", "Ultear", "Brain");
    private static final List<String> SUPPORTS_SS = List.of("Lucas");

    private static final List<String> DPS_C  = List.of("Alzack", "Bisca", "Bora", "Eligoal", "Tobi", "Wolly");
    private static final List<String> DPS_B  = List.of("Leon", "Totomaru");
    private static final List<String> DPS_A  = List.of("Angel", "Gajeel", "Gray", "Natsu", "Aria", "Bickslow", "Owl",
            "Panther Lily", "Ren", "Cobra", "Racer", "Midnight");
    private static final List<String> DPS_S  = List.of("Mirajane", "Sting", "Natsu Etherion", "José Pora", "Jellal",
            "Loki", "Zancrow", "Capricorn");
    private static final List<String> DPS_SS = List.of("Mirajane Halphas", "Ul Milkovich", "Jellal Intermagie", "Luxus",
            "Zero", "Bluenote", "Gildarts", "Acnologia", "Hades");

    private static final String[] CLASSES_IA = {"Chevalier", "Chasseur de Dragon", "Mage", "Constellationniste"};

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
            int niveau;
            List<String> poolTanks, poolSupports, poolDPS;

            if (rang >= 90) {
                niveau = 15 + rng.nextInt(6);
                poolTanks = TANKS_C; poolSupports = SUPPORTS_C; poolDPS = DPS_C;
            } else if (rang >= 80) {
                niveau = 20 + rng.nextInt(6);
                poolTanks    = mix(rng, TANKS_C,    TANKS_B,    0.6);
                poolSupports = mix(rng, SUPPORTS_C, SUPPORTS_B, 0.6);
                poolDPS      = mix(rng, DPS_C,      DPS_B,      0.6);
            } else if (rang >= 60) {
                niveau = 30 + rng.nextInt(11);
                poolTanks = TANKS_B; poolSupports = SUPPORTS_B; poolDPS = DPS_B;
            } else if (rang >= 40) {
                niveau = 40 + rng.nextInt(16);
                poolTanks    = mix(rng, TANKS_B,    TANKS_A,    0.5);
                poolSupports = mix(rng, SUPPORTS_B, SUPPORTS_A, 0.5);
                poolDPS      = mix(rng, DPS_B,      DPS_A,      0.5);
            } else if (rang >= 20) {
                niveau = 55 + rng.nextInt(21);
                poolTanks = TANKS_A; poolSupports = SUPPORTS_A; poolDPS = DPS_A;
            } else if (rang >= 10) {
                niveau = 75 + rng.nextInt(11);
                poolTanks    = mix(rng, TANKS_A,    TANKS_S,    0.5);
                poolSupports = mix(rng, SUPPORTS_A, SUPPORTS_S, 0.5);
                poolDPS      = mix(rng, DPS_A,      DPS_S,      0.5);
            } else if (rang >= 5) {
                niveau = 85 + rng.nextInt(6);
                poolTanks = TANKS_S; poolSupports = SUPPORTS_S; poolDPS = DPS_S;
            } else {
                niveau = 90 + rng.nextInt(11);
                poolTanks    = mix(rng, TANKS_S,     TANKS_SS,    0.4);
                poolSupports = mix(rng, SUPPORTS_S,  SUPPORTS_SS, 0.4);
                poolDPS      = mix(rng, DPS_S,        DPS_SS,      0.4);
            }

            List<String> equipe = genererEquipeParPools(rng, poolTanks, poolSupports, poolDPS);
            String classeIA  = CLASSES_IA[rng.nextInt(CLASSES_IA.length)];
            String principal = "PP_" + classeIA;
            int points = 10000 - (rang - 1) * 95;

            classement.add(new AreneData(
                "CPU_RANG_" + rang,
                genererPseudoIA(rang),
                true, rang, points, 0,
                equipe, principal, niveau,
                System.currentTimeMillis()
            ));
        }
    }

    private List<String> mix(Random rng, List<String> primary, List<String> secondary, double probSecondaire) {
        List<String> result = new ArrayList<>(primary);
        for (String s : secondary) {
            if (rng.nextDouble() < probSecondaire) result.add(s);
        }
        return result.isEmpty() ? primary : result;
    }

    private List<String> genererEquipeParPools(Random rng, List<String> tanks,
                                                List<String> supports, List<String> dps) {
        List<String> equipe = new ArrayList<>();
        java.util.Set<String> deja = new java.util.HashSet<>();

        String tank = piocher(rng, tanks, deja);
        if (tank    != null) { equipe.add(tank);    deja.add(tank); }

        String support = piocher(rng, supports, deja);
        if (support != null) { equipe.add(support); deja.add(support); }

        for (int i = 0; i < 2; i++) {
            String d = piocher(rng, dps, deja);
            if (d != null) { equipe.add(d); deja.add(d); }
        }
        return equipe;
    }

    private String piocher(Random rng, List<String> pool, java.util.Set<String> deja) {
        List<String> dispo = new ArrayList<>(pool);
        dispo.removeAll(deja);
        if (dispo.isEmpty()) return null;
        return dispo.get(rng.nextInt(dispo.size()));
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
                .uri(URI.create(FirebaseAuth.avecAuth(URL_FIREBASE + ".json")))
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

            genererFauxJoueurs();

            for (Map<String, Object> data : raw.values()) {
                AreneData vrai  = AreneData.depuisMap(data);
                int       index = vrai.getRang() - 1;
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
                .uri(URI.create(FirebaseAuth.avecAuth(URL_FIREBASE + securiser(joueur.getUserId()) + ".json")))
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

    // ── Coffre journalier ──────────────────────────────────────────────────
    // Le coffre s'ouvre a partir de 20h et une seule fois par "jour d'arene" (qui commence a
    // 20h, pas a minuit) : avant 20h, le jour courant compte encore comme la veille.

    /** Vrai si le coffre journalier n'a pas encore ete reclame pour le jour d'arene en cours. */
    public boolean coffreJournalierDisponible(lancement.GameContext ctx) {
        if (java.time.LocalDateTime.now().getHour() < 20) return false;
        String derniereCle = ctx.dernierCoffreArene != null ? ctx.dernierCoffreArene : "";
        return !cleJourCoffre().equals(derniereCle);
    }

    private String cleJourCoffre() {
        java.time.LocalDate aujourd = java.time.LocalDate.now();
        if (java.time.LocalDateTime.now().getHour() < 20) aujourd = aujourd.minusDays(1);
        return aujourd.toString();
    }

    /** Recompense du coffre journalier d'Arene pour un rang donne, avant application. */
    public record RecompenseCoffreJournalier(int pointsBoutique, int or, int coupons, String tranche) {}

    private RecompenseCoffreJournalier calculerRecompenseCoffre(int rang) {
        if (rang == 1)   return new RecompenseCoffreJournalier(15_000, 110_000, 50, "Rang #1 — Légendaire");
        if (rang <= 4)   return new RecompenseCoffreJournalier(11_000, 100_000, 0, "Rang #2–4 — Élite");
        if (rang <= 9)   return new RecompenseCoffreJournalier(10_000, 90_000, 0, "Rang #5–9 — Maître");
        if (rang <= 24)  return new RecompenseCoffreJournalier(7_000, 60_000, 0, "Rang #10–24 — Expert");
        if (rang <= 49)  return new RecompenseCoffreJournalier(5_000, 40_000, 0, "Rang #25–49 — Avancé");
        if (rang <= 74)  return new RecompenseCoffreJournalier(2_500, 20_000, 0, "Rang #50–74 — Intermédiaire");
        return new RecompenseCoffreJournalier(1_500, 10_000, 0, "Rang #75–100 — Débutant");
    }

    /**
     * Reclame le coffre journalier d'Arene pour {@code joueurArene} : calcule la recompense
     * selon son rang, l'applique (points boutique, or, coupons), marque le coffre reclame pour
     * aujourd'hui, uploade le nouveau rang et sauvegarde. Retourne {@code null} si le coffre
     * n'est pas encore disponible (a verifier au prealable avec {@link #coffreJournalierDisponible}).
     * Logique partagee entre MenuArene (console) et EcranAreneController (GUI).
     */
    public RecompenseCoffreJournalier reclamerCoffreJournalier(AreneData joueurArene, lancement.GameContext ctx) {
        if (!coffreJournalierDisponible(ctx)) return null;

        RecompenseCoffreJournalier recompense = calculerRecompenseCoffre(joueurArene.getRang());

        joueurArene.ajouterPointsBoutique(recompense.pointsBoutique());
        ctx.joueur.ajouterOr(recompense.or());
        if (recompense.coupons() > 0) ctx.joueur.setCoupons(ctx.joueur.getCoupons() + recompense.coupons());

        ctx.dernierCoffreArene = cleJourCoffre();
        uploaderRangJoueur(joueurArene);
        ctx.sauvegarde.sauvegarder(ctx);

        return recompense;
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

    // ── Compagnon adverse (par rang) ────────────────────────────────────────

    /**
     * Attribue a l'equipe adverse un compagnon (meme mecanique que CompagnonsType/
     * GestionnaireCompagnons cote joueur, applique directement via les setters de
     * PersonnageBase) selon le rang RangJoueur de l'adversaire :
     *   C : aucun compagnon. B : Happy. A : Carla ou Panthere Lily (au hasard).
     *   S : Panthere Lily ou Bebe Igneel. SS : Bebe Igneel ou Bebe Grandine.
     *   SSS : Bebe Grandine ou Bebe Metalicana. UR : Bebe Metalicana, niveau 10 fixe.
     * Le niveau est tire aleatoirement entre 1 et 10 pour tous les rangs sauf UR (toujours 10).
     * A appeler AVANT reinitialiserPourCombat() (getVieMax() lit deja le bonus PV du compagnon).
     */
    public static void appliquerCompagnonAdversaire(List<PersonnageBase> equipeAdverse, RangJoueur.Rang rang) {
        if (equipeAdverse.isEmpty() || rang == RangJoueur.Rang.C) return;

        Random rng = new Random();
        CompagnonsType type;
        int niveau;
        switch (rang) {
            case B   -> { type = CompagnonsType.HAPPY;                                                          niveau = 1 + rng.nextInt(10); }
            case A   -> { type = rng.nextBoolean() ? CompagnonsType.CARLA         : CompagnonsType.PANTHERE_LILY;   niveau = 1 + rng.nextInt(10); }
            case S   -> { type = rng.nextBoolean() ? CompagnonsType.PANTHERE_LILY : CompagnonsType.BEBE_IGNEEL;     niveau = 1 + rng.nextInt(10); }
            case SS  -> { type = rng.nextBoolean() ? CompagnonsType.BEBE_IGNEEL   : CompagnonsType.BEBE_GRANDINE;   niveau = 1 + rng.nextInt(10); }
            case SSS -> { type = rng.nextBoolean() ? CompagnonsType.BEBE_GRANDINE : CompagnonsType.BEBE_METALICANA; niveau = 1 + rng.nextInt(10); }
            default  -> { type = CompagnonsType.BEBE_METALICANA;                                                 niveau = 10; } // UR
        }

        double atk = type.getATK(niveau), pv = type.getPV(niveau), def = type.getDEF(niveau), vit = type.getVIT(niveau);
        for (PersonnageBase p : equipeAdverse) {
            p.setBonusCompagnonsATK(atk);
            p.setBonusCompagnonsDEF(def);
            p.setBonusCompagnonsPV(pv);
            p.setBonusCompagnonsVIT(vit);
        }
    }

    public AreneData getOuCreerJoueur(String userId, String pseudo,
                                       List<String> equipeNoms, String principalNom) {
        int niveauMoyen = calculerNiveauMoyen(equipeNoms);

        // Si le profil existe deja (localement ou charge depuis Firebase), on le resynchronise
        // sur l'equipe/niveau actuels au lieu de renvoyer tel quel le dernier instantane connu —
        // sinon un joueur qui a progresse depuis sa premiere visite reste fige a ses stats
        // d'origine aux yeux des autres joueurs qui l'affrontent en defense.
        for (AreneData a : classement) {
            if (a.getUserId().equals(userId)) {
                a.actualiserEquipeDefensive(equipeNoms, niveauMoyen);
                return a;
            }
        }

        AreneData nouveau = new AreneData(
            userId, pseudo, false, TAILLE_CLASSEMENT, 500, 0,
            equipeNoms, principalNom, niveauMoyen,
            System.currentTimeMillis()
        );
        classement.set(TAILLE_CLASSEMENT - 1, nouveau);
        return nouveau;
    }

    private int calculerNiveauMoyen(List<String> equipeNoms) {
        return equipeNoms.stream()
                .map(factory)
                .filter(p -> p != null)
                .mapToInt(PersonnageBase::getNiveau)
                .reduce(0, Integer::sum) / Math.max(1, equipeNoms.size());
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private String securiser(String id) {
        return id.trim().toLowerCase().replace(" ", "_");
    }

    public List<AreneData> getClassement() { return Collections.unmodifiableList(classement); }
}