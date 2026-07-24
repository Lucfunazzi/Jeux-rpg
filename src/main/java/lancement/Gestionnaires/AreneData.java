package lancement.Gestionnaires;

import Equipement.Equipement;
import Equipement.EquipementFactory;
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
        int niv = Math.max(1, niveauMoyenEquipe);
        List<PersonnageBase> equipe = equipeDefensiveNoms.stream()
                .map(factory)
                .filter(p -> p != null)
                .peek(p -> {
                    // Monter chaque personnage au niveau de sa tranche de rang
                    while (p.getNiveau() < niv) p.monterDeNiveauSilencieux();
                })
                .collect(java.util.stream.Collectors.toList());

        // Equipement fantome uniquement pour les faux joueurs (IA), selon leur position
        // au classement — un vrai joueur adverse n'en reçoit pas.
        if (estFauxJoueur) {
            Equipement.Rarete rarete = EquipementFactory.rareteEnnemiPourRangArene(rang);
            for (PersonnageBase p : equipe) {
                EquipementFactory.equiperSetStandard(p, rarete);
            }
        }

        return equipe;
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