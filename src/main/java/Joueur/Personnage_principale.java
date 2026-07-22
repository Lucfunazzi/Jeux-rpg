package Joueur;

import Personnage.*;
import java.util.List;
import Combat.Combat;
import lancement.GameContext;

public class Personnage_principale extends PersonnageBase {

    private String[]     classes = {"Chevalier", "Chasseur de Dragon", "Mage", "Constellationniste"};
    private String       choixClasse;
    private Competences  competenceChoisie;
    private double       or      = 1000.00;
    private int          coupons = 50;

    // Arbre de compétences
    private ArbreCompetences arbreCompetences = new ArbreCompetences();

    /**
     * Quelle version de la spéciale / ultime est active :
     *   0 = compétences de base (spéciale + ultime normales)
     *   1 = spéciale remplacée par compétenceArbre  (nœud 10 arbre 1 débloqué)
     *   2 = ultime  remplacée par competenceArbre2  (nœud 10 arbre 2 débloqué)
     *   3 = les deux remplacées
     */
    private int competenceSpecialeActive = 0;

    private GameContext ctx;

    public Personnage_principale(String nom, int niveau) {
        this.nom    = nom;
        this.niveau = niveau;
        this.rarete = "C";
        this.role   = "DPS";
        this.vie      = 300;
        this.attaque  = 110;
        this.defense  = 60;
        this.vitesse  = 100;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    public void setGameContext(GameContext ctx) { this.ctx = ctx; }

    private double getMultiplicateurRang() {
        if (ctx != null && ctx.rangJoueur != null)
            return ctx.rangJoueur.getMultiplicateur();
        return 1.00;
    }

    @Override public double getAttaque() {
        return super.getAttaque() * (1.0 + arbreCompetences.getBonusATK()) * getMultiplicateurRang();
    }
    @Override public double getDefense() {
        return super.getDefense() * (1.0 + arbreCompetences.getBonusDEF()) * getMultiplicateurRang();
    }
    @Override public double getVieMax() {
        return super.getVieMax() * (1.0 + arbreCompetences.getBonusPV()) * getMultiplicateurRang();
    }
    @Override public double getVitesse() {
        return super.getVitesse() * (1.0 + arbreCompetences.getBonusVIT()) * getMultiplicateurRang();
    }

    @Override public boolean estPersonnagePrincipal() { return true; }

    @Override public String[] getNomsAttaques() {
        if (competenceChoisie == null)
            return new String[]{"Attaque de base", "Attaque spéciale", "Attaque ultime"};
        String[] noms = competenceChoisie.getNomsCompetences();
        // noms[0] = spéciale de base, noms[1] = ultime de base
        String nomSpeciale = (competenceSpecialeActive == 1 || competenceSpecialeActive == 3)
                ? getNomCompetenceArbre(choixClasse, 1)
                : noms[0];
        String nomUltime   = (competenceSpecialeActive == 2 || competenceSpecialeActive == 3)
                ? getNomCompetenceArbre(choixClasse, 2)
                : noms[1];
        return new String[]{"Attaque de base", nomSpeciale, nomUltime};
    }

    /** Noms des compétences débloquées par l'arbre — dupliqué de MenuAbilite pour éviter la dépendance circulaire. */
    public static String getNomCompetenceArbre(String classe, int arbre) {
        if (classe == null) return "Compétence spéciale " + arbre;
        if (arbre == 3) return "Compétence spéciale (Arbre 3)";
        return switch (classe) {
            case "Mage"               -> arbre == 1 ? "Épée de Glace Éternelle"      : "Ice Make — Démon de Glace";
            case "Chasseur de Dragon" -> arbre == 1 ? "Écailles du Dragon d'Eau"     : "Forme Dragon — Inondation Abyssale";
            case "Chevalier"          -> arbre == 1 ? "Changement d'Armure"          : "Armure du Ciel Brillant";
            case "Constellationniste" -> arbre == 1 ? "Invocation : Leo"             : "Portes des Étoiles";
            default -> "Compétence spéciale " + arbre;
        };
    }

    // ── Attaque de base ───────────────────────────────────────────────────
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    // ── Attaque spéciale ──────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) { log.add("Aucune classe active !"); return; }

        boolean arbre1Actif = (competenceSpecialeActive == 1 || competenceSpecialeActive == 3);
        if (arbre1Actif) {
            competenceChoisie.competenceArbre(this, cible, equipeAlliee, equipeEnnemie, log);
        } else {
            competenceChoisie.attaqueSpeciale(this, cible, equipeAlliee, equipeEnnemie, log);
        }
    }

    // ── Attaque ultime ────────────────────────────────────────────────────
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) { log.add("Aucune classe active !"); return; }

        boolean arbre2Actif = (competenceSpecialeActive == 2 || competenceSpecialeActive == 3);
        if (arbre2Actif) {
            PersonnageBase cible = equipeEnnemie.stream()
                    .filter(PersonnageBase::estVivant).findFirst().orElse(null);
            competenceChoisie.competenceArbre2(this, cible, equipeAlliee, equipeEnnemie, log);
        } else {
            competenceChoisie.ultime(this, equipeAlliee, equipeEnnemie, log);
        }
    }


    // ── Descriptions ──────────────────────────────────────────────────────
    @Override public void descriptionAttaqueBase() {
        System.out.println("Attaque de base — Attaque la cible à 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        if (competenceChoisie == null) { System.out.println("Aucune classe active."); return; }
        boolean arbre1Actif = (competenceSpecialeActive == 1 || competenceSpecialeActive == 3);
        if (arbre1Actif) competenceChoisie.descriptionCompetenceArbre();
        else             competenceChoisie.descriptionAttaqueSpeciale();
    }
    @Override public void descriptionAttaqueUltime() {
        if (competenceChoisie == null) { System.out.println("Aucune classe active."); return; }
        boolean arbre2Actif = (competenceSpecialeActive == 2 || competenceSpecialeActive == 3);
        if (arbre2Actif) competenceChoisie.descriptionCompetenceArbre2();
        else             competenceChoisie.descriptionUltime();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String[]    getClasses()                         { return classes; }
    public void        setClasses(String[] classes)         { this.classes = classes; }
    public String      getChoixClasses()                    { return choixClasse; }
    public void        setChoixClasses(String c)            { this.choixClasse = c; this.type = c; }
    public Competences getCompetencesChoisie()              { return competenceChoisie; }
    public void        setCompetencesChoisie(Competences c) { this.competenceChoisie = c; }
    public double      getOr()                              { return or; }
    public void        setOr(double or)                     { this.or = or; }
    public int         getCoupons()                         { return coupons; }
    public void        setCoupons(int coupons)              { this.coupons = coupons; }

    public ArbreCompetences getArbreCompetences() { return arbreCompetences; }

    /**
     * 0 = base, 1 = spéciale arbre, 2 = ultime arbre, 3 = les deux.
     * Mis à jour automatiquement quand un nœud 10 est débloqué.
     */
    public int  getCompetenceSpecialeActive()     { return competenceSpecialeActive; }
    public void setCompetenceSpecialeActive(int v){ this.competenceSpecialeActive = v; }

    /** Active la spéciale arbre 1 sans toucher à l'ultime. */
    public void activerArbre1() {
        competenceSpecialeActive = (competenceSpecialeActive == 2) ? 3 : 1;
    }

    /** Active l'ultime arbre 2 sans toucher à la spéciale. */
    public void activerArbre2() {
        competenceSpecialeActive = (competenceSpecialeActive == 1) ? 3 : 2;
    }

    // Compatibilité ascendante sauvegarde
    public boolean isUtiliserCompetenceArbre()   { return competenceSpecialeActive > 0; }
    public void    setUtiliserCompetenceArbre(boolean b) { if (b && competenceSpecialeActive == 0) competenceSpecialeActive = 1; }

    // Champs supprimés — gardés pour la compatibilité de sauvegarde uniquement
    public int  getchoixDescription_comp()          { return 0; }
    public void setChoixDescription_comp(int n)     {}
    public int  getChoixComp()                      { return 1; }
    public void setChoixComp(int n)                 {}

    public void ajouterOr(int montant) {
        this.or += montant;
        System.out.println("+ " + montant + " or ! (Total : " + String.format("%.0f", this.or) + ")");
    }

    public void retirerOr(double montant) {
        this.or = Math.max(0, this.or - montant);
    }

    @Override public String toString() {
        return nom + " est niveau " + niveau + " | Classe : "
                + (choixClasse != null ? choixClasse : "Aucune")
                + "\nStatistiques : " + vie + " PV, " + attaque + " ATK, "
                + defense + " DEF, " + vitesse + " VIT";
    }
}