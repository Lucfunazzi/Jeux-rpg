package Joueur;

import Personnage.*;
import java.util.ArrayList;
import java.util.List;
import Combat.Combat;
import lancement.GameContext;

public class Personnage_principale extends PersonnageBase {

    private String[] classes = {"Mage", "Ninja", "Guerrier"};
    private String   choixClasse;
    private int      choixComp;
    private int      choixDescription_comp;
    private Competences competenceChoisie;
    private double   or      = 1000.00;
    private int      coupons = 50;

    // Arbre de competences
    private ArbreCompetences arbreCompetences        = new ArbreCompetences();
    // 0 = competence originale, 1 = competence arbre 1, 2 = competence arbre 2
    private int              competenceSpecialeActive = 0;

    // Référence au GameContext pour accéder au multiplicateur de rang
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

    // ── Injection du GameContext ──────────────────────────────────────────
    public void setGameContext(GameContext ctx) { this.ctx = ctx; }

    private double getMultiplicateurRang() {
        if (ctx != null && ctx.rangJoueur != null)
            return ctx.rangJoueur.getMultiplicateur();
        return 1.00;
    }

    // ── Stats avec bonus arbre + multiplicateur de rang ───────────────────
    @Override
    public double getAttaque() {
        return super.getAttaque()
                * (1.0 + arbreCompetences.getBonusATK())
                * getMultiplicateurRang();
    }

    @Override
    public double getDefense() {
        return super.getDefense()
                * (1.0 + arbreCompetences.getBonusDEF())
                * getMultiplicateurRang();
    }

    @Override
    public double getVieMax() {
        return super.getVieMax()
                * (1.0 + arbreCompetences.getBonusPV())
                * getMultiplicateurRang();
    }

    @Override
    public double getVitesse() {
        return super.getVitesse()
                * (1.0 + arbreCompetences.getBonusVIT())
                * getMultiplicateurRang();
    }

    // ── Attaques ──────────────────────────────────────────────────────────
    @Override
    public boolean estPersonnagePrincipal() { return true; }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Attaque de base", "Attaque speciale", "Attaque ultime"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) {
            log.add("Aucune competence choisie !");
            return;
        }
        switch (competenceSpecialeActive) {
            case 1 -> competenceChoisie.competenceArbre(this, cible, equipeAlliee, equipeEnnemie, log);
            case 2 -> competenceChoisie.competenceArbre2(this, cible, equipeAlliee, equipeEnnemie, log);
            default -> {
                switch (this.choixComp) {
                    case 1 -> competenceChoisie.choix_competence1(this, cible, equipeAlliee, equipeEnnemie, log);
                    case 2 -> competenceChoisie.choix_competence2(this, cible, equipeAlliee, equipeEnnemie, log);
                    case 3 -> competenceChoisie.choix_competence3(this, cible, equipeAlliee, equipeEnnemie, log);
                    default -> log.add("Competence speciale invalide.");
                }
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) {
            log.add("Aucune competence choisie !");
            return;
        }
        competenceChoisie.ultime(this, equipeAlliee, equipeEnnemie, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Attaque la cible a 100% de son attaque.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        if (competenceChoisie == null) {
            System.out.println("Aucune classe/competence active.");
            return;
        }
        switch (competenceSpecialeActive) {
            case 1 -> competenceChoisie.descriptionCompetenceArbre();
            case 2 -> competenceChoisie.descriptionCompetenceArbre2();
            default -> {
                switch (this.choixComp) {
                    case 1 -> competenceChoisie.descriptionCompetence1();
                    case 2 -> competenceChoisie.descriptionCompetence2();
                    case 3 -> competenceChoisie.descriptionCompetence3();
                    default -> System.out.println("Aucune competence selectionnee.");
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueUltime() {
        if (competenceChoisie != null) competenceChoisie.descriptionUltime();
        else System.out.println("Aucune competence ultime active.");
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String[]    getClasses()                          { return this.classes; }
    public void        setClasses(String[] classes)          { this.classes = classes; }
    public String      getChoixClasses()                     { return this.choixClasse; }
    public void        setChoixClasses(String choixClasse)   { this.choixClasse = choixClasse; this.type = choixClasse; }
    public int         getchoixDescription_comp()            { return this.choixDescription_comp; }
    public void        setChoixDescription_comp(int n)       { this.choixDescription_comp = n; }
    public int         getChoixComp()                        { return this.choixComp; }
    public void        setChoixComp(int choixComp)           { this.choixComp = choixComp; }
    public Competences getCompetencesChoisie()               { return this.competenceChoisie; }
    public void        setCompetencesChoisie(Competences c)  { this.competenceChoisie = c; }
    public double      getOr()                               { return this.or; }
    public void        setOr(double or)                      { this.or = or; }
    public int         getCoupons()                          { return this.coupons; }
    public void        setCoupons(int coupons)               { this.coupons = coupons; }

    public ArbreCompetences getArbreCompetences()               { return arbreCompetences; }

    /** 0 = originale, 1 = arbre 1, 2 = arbre 2 */
    public int  getCompetenceSpecialeActive()                   { return competenceSpecialeActive; }
    public void setCompetenceSpecialeActive(int v)              { this.competenceSpecialeActive = v; }

    /** Compatibilite ascendante */
    public boolean isUtiliserCompetenceArbre()                  { return competenceSpecialeActive > 0; }
    public void    setUtiliserCompetenceArbre(boolean b)        { competenceSpecialeActive = b ? 1 : 0; }

    public void ajouterOr(int montant) {
        this.or += montant;
        System.out.println("+ " + montant + " or ! (Total : "
                + String.format("%.0f", this.or) + ")");
    }

    public void retirerOr(double montant) {
        this.or = Math.max(0, this.or - montant);
    }

    @Override
    public String toString() {
        return this.nom + " est niveau " + this.niveau + " | Classe : "
                + (this.choixClasse != null ? this.choixClasse : "Aucune")
                + "\nStatistiques : " + this.vie + " PV, " + this.attaque + " ATK, "
                + this.defense + " DEF, " + this.vitesse + " VIT";
    }
}