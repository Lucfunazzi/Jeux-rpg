package lancement.Gestionnaires;

import Personnage.PersonnageBase;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AreneData {

    private String       userId;
    private String       pseudo;
    private boolean      estFauxJoueur;
    private int          rang;
    private int          pointsArene;      // points de classement (échange de rang)
    private int          pointsBoutique;   // points dépensables en boutique
    private List<String> equipeDefensiveNoms;
    private String       personnagePrincipalNom;
    private int          niveauMoyenEquipe;
    private long         derniereMiseAJour;

    public AreneData(String userId, String pseudo, boolean estFauxJoueur,
                     int rang, int pointsArene, int pointsBoutique,
                     List<String> equipeDefensiveNoms,
                     String personnagePrincipalNom,
                     int niveauMoyenEquipe,
                     long derniereMiseAJour) {
        this.userId                 = userId;
        this.pseudo                 = pseudo;
        this.estFauxJoueur          = estFauxJoueur;
        this.rang                   = rang;
        this.pointsArene            = pointsArene;
        this.pointsBoutique         = pointsBoutique;
        this.equipeDefensiveNoms    = new ArrayList<>(equipeDefensiveNoms);
        this.personnagePrincipalNom = personnagePrincipalNom;
        this.niveauMoyenEquipe      = niveauMoyenEquipe;
        this.derniereMiseAJour      = derniereMiseAJour;
    }

    // ── Sérialisation Map (pour Gson + Firebase REST) ─────────────────────

    public Map<String, Object> versMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId",                 userId);
        m.put("pseudo",                 pseudo);
        m.put("estFauxJoueur",          estFauxJoueur);
        m.put("rang",                   rang);
        m.put("pointsArene",            pointsArene);
        m.put("pointsBoutique",         pointsBoutique);
        m.put("equipeDefensiveNoms",    equipeDefensiveNoms);
        m.put("personnagePrincipalNom", personnagePrincipalNom);
        m.put("niveauMoyenEquipe",      niveauMoyenEquipe);
        m.put("derniereMiseAJour",      derniereMiseAJour);
        return m;
    }

    @SuppressWarnings("unchecked")
    public static AreneData depuisMap(Map<String, Object> m) {
        return new AreneData(
            (String)  m.get("userId"),
            (String)  m.get("pseudo"),
            (Boolean) m.get("estFauxJoueur"),
            ((Double) m.get("rang")).intValue(),
            ((Double) m.get("pointsArene")).intValue(),
            ((Double) m.getOrDefault("pointsBoutique", 0.0)).intValue(),
            (List<String>) m.get("equipeDefensiveNoms"),
            (String)  m.get("personnagePrincipalNom"),
            ((Double) m.get("niveauMoyenEquipe")).intValue(),
            ((Double) m.get("derniereMiseAJour")).longValue()
        );
    }

    // ── Utilitaire combat ─────────────────────────────────────────────────

    public List<PersonnageBase> construireEquipe(Function<String, PersonnageBase> factory) {
        // Multiplicateur de stats selon le rang :
        // Rang 1 = x2.5 | Rang 25 = x1.5 | Rang 50 = x1.0 | Rang 75 = x0.75 | Rang 100 = x0.55
        double mult = rangVersMultiplicateur(rang);

        return equipeDefensiveNoms.stream()
                .map(factory)
                .filter(p -> p != null)
                .peek(p -> appliquerMultiplicateurArene(p, mult))
                .collect(java.util.stream.Collectors.toList());
    }

    /** Calcule le multiplicateur de stats selon le rang arène. */
    private static double rangVersMultiplicateur(int rang) {
        // Rang 1 = x1.80 | Rang 50 = x1.00 | Rang 100 = x0.50
        if (rang <= 50) {
            // Interpolation rang 1→50 : 1.80 → 1.00
            return 1.80 - (1.80 - 1.00) * (rang - 1.0) / 49.0;
        } else {
            // Interpolation rang 50→100 : 1.00 → 0.50
            return 1.00 - (1.00 - 0.50) * (rang - 50.0) / 50.0;
        }
    }

    /** Applique un multiplicateur plat sur les stats brutes du personnage. */
    private static void appliquerMultiplicateurArene(PersonnageBase p, double mult) {
        // On surcharge via les bonus de lien pour ne pas toucher aux stats de base
        p.setBonusLienATK(p.getAttaque() * (mult - 1.0));
        p.setBonusLienDEF(p.getDefense() * (mult - 1.0));
        p.setBonusLienPV(p.getVieMax() * (mult - 1.0));
        p.setBonusLienVIT(p.getVitesse() * (mult - 1.0));
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public String       getUserId()                 { return userId; }
    public String       getPseudo()                 { return pseudo; }
    public boolean      isEstFauxJoueur()           { return estFauxJoueur; }
    public int          getRang()                   { return rang; }
    public int          getPointsArene()            { return pointsArene; }
    public int          getPointsBoutique()         { return pointsBoutique; }
    public List<String> getEquipeDefensiveNoms()    { return Collections.unmodifiableList(equipeDefensiveNoms); }
    public String       getPersonnagePrincipalNom() { return personnagePrincipalNom; }
    public int          getNiveauMoyenEquipe()      { return niveauMoyenEquipe; }
    public long         getDerniereMiseAJour()      { return derniereMiseAJour; }

    // ── Setters ───────────────────────────────────────────────────────────

    public void setRang(int rang)                     { this.rang = rang; }
    public void setPointsArene(int points)            { this.pointsArene = points; }
    public void setPointsBoutique(int pts)            { this.pointsBoutique = pts; }
    public void ajouterPointsBoutique(int pts)        { this.pointsBoutique += pts; }
    public void setDerniereMiseAJour(long ts)         { this.derniereMiseAJour = ts; }
}