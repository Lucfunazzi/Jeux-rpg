package Personnage;
import Joueur.*;
import Effets.Effet;
import Effets.Invincibilite;
import Effets.Fragilite;
import Effets.Marquage;
import Effets.Sommeil;
import Effets.Malediction;
import Effets.Ralentissement;
import Effets.Resurrection;
import Effets.BuffAttaque;
import Effets.BuffDefense;
import Effets.BuffVitesse;
import Effets.BuffBlocage;
import Effets.BuffTauxCritique;
import Effets.BuffDegatCritique;
import Effets.BuffTauxEsquive;
import Effets.BuffPrecision;
import Effets.Aveuglement;
import Effets.ReductionDefense;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Effets.Poison;
import Effets.Bouclier;
import Effets.Brulure;
import Effets.Absorption;
import java.util.ArrayList;
import java.util.List;
import Equipement.Equipement;
import Equipement.Pierre;
import Equipement.BonusSet;
import java.util.HashMap;

public abstract class PersonnageBase implements Statistiques, Attaques {
    protected String nom;
    protected int niveau;
    protected int experience = 0;
    protected int experienceMax = 300;
    protected String type;
    protected String role;
    protected String rarete;
    protected double vie;
    protected double vieMax;
    protected double attaque;
    protected double defense;
    protected double vitesse;
    private double attaqueBase;
    private double defenseBase;
    private double vitesseBase;
    protected double taux_critiques;
    protected double degat_critiques;
    protected double taux_precisions;
    protected double taux_esquives;
    protected double taux_blocage;
    /** Attaque S : reduit l'efficacite du blocage adverse. 100 = neutre. */
    protected double taux_attaque_s = 100.0;
    /** Contre : reduit les chances de subir un coup critique. 100 = neutre. */
    protected double taux_contre = 100.0;
    private double tauxCritiquesBase;
    private double degatCritiquesBase;
    private double tauxEsquivesBase;
    private double tauxBlocageBase;
    protected double reduction_blocage;
    protected double degats_renvoi;
    private double rage = 0;
    private double rageMax = 100;
    /** Vrai si la derniere attaque de base resolue via Combat.attaquer() etait un coup critique (pour le bonus de furie). */
    private boolean dernierCoupCritique = false;
    protected boolean specialeUtilisee = false;
    private double bonusLienATK = 0.0;
    private double bonusLienDEF = 0.0;
    private double bonusLienPV  = 0.0;
    private double bonusLienVIT = 0.0;
    private double bonusTitre   = 0.0;
    private double bonusCompagnonsATK = 0.0;
    private double bonusCompagnonsDEF = 0.0;
    private double bonusCompagnonsPV  = 0.0;
    private double bonusCompagnonsVIT = 0.0;
    private double bonusCreatureATK = 0.0;
    private double bonusCreatureDEF = 0.0;
    private double bonusCreaturePV  = 0.0;
    private double bonusCreatureVIT = 0.0;
    private int    nbreEtoiles    = 0;   // 0-5 : chaque étoile = +5% ATK/DEF/PV/VIT

    protected ArrayList<Effet> effetsActifs = new ArrayList<>();

    public String getNom() { return this.nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getNiveau() { return this.niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }
    public String getType() { return this.type; }
    public String getRole() { return this.role; }
    public String getRarete() { return this.rarete; }
    public boolean estPersonnagePrincipal() { return false; }
    public void setBonusLienATK(double v) { this.bonusLienATK = v; }
    public void setBonusLienDEF(double v) { this.bonusLienDEF = v; }
    public void setBonusLienPV(double v)  { this.bonusLienPV  = v; }
    public void setBonusLienVIT(double v) { this.bonusLienVIT = v; }
    public double getBonusLienATK() { return bonusLienATK; }
    public double getBonusLienDEF() { return bonusLienDEF; }
    public double getBonusLienPV()  { return bonusLienPV; }
    public double getBonusLienVIT() { return bonusLienVIT; }
    public void setBonusTitre(double v) { this.bonusTitre = v; }
    public double getBonusTitre() { return bonusTitre; }
    public void setBonusCompagnonsATK(double v) { this.bonusCompagnonsATK = v; }
    public void setBonusCompagnonsDEF(double v) { this.bonusCompagnonsDEF = v; }
    public void setBonusCompagnonsPV(double v)  { this.bonusCompagnonsPV  = v; }
    public void setBonusCompagnonsVIT(double v) { this.bonusCompagnonsVIT = v; }
    public double getBonusCompagnonsATK() { return bonusCompagnonsATK; }
    public double getBonusCompagnonsDEF() { return bonusCompagnonsDEF; }
    public double getBonusCompagnonsPV()  { return bonusCompagnonsPV; }
    public double getBonusCompagnonsVIT() { return bonusCompagnonsVIT; }
    public void setBonusCreatureATK(double v) { this.bonusCreatureATK = v; }
    public void setBonusCreatureDEF(double v) { this.bonusCreatureDEF = v; }
    public void setBonusCreaturePV(double v)  { this.bonusCreaturePV  = v; }
    public void setBonusCreatureVIT(double v) { this.bonusCreatureVIT = v; }
    public double getBonusCreatureATK() { return bonusCreatureATK; }
    public double getBonusCreatureDEF() { return bonusCreatureDEF; }
    public double getBonusCreaturePV()  { return bonusCreaturePV; }
    public double getBonusCreatureVIT() { return bonusCreatureVIT; }

    // ── Étoiles ───────────────────────────────────────────────────────────
    public int  getNbreEtoiles()        { return nbreEtoiles; }
    public void setNbreEtoiles(int n)   { this.nbreEtoiles = Math.max(0, Math.min(5, n)); }

    /** Monte d'une étoile (max 5) et recalcule les stats. */
    public void monterEtoile() {
        if (nbreEtoiles < 5) nbreEtoiles++;
    }

    // ── Ascension (palier bonus au-dela de 5 etoiles, via Parchemin d'Ascension) ──
    private boolean ascensionne = false;

    public boolean isAscensionne()      { return ascensionne; }
    public void    setAscensionne(boolean v) { this.ascensionne = v; }

    /** Vrai si ce personnage est eligible a l'ascension (5 etoiles, pas deja ascensionne). */
    public boolean peutAscensionner() { return nbreEtoiles >= 5 && !ascensionne; }

    /** Applique l'ascension si eligible. */
    public void ascensionner() {
        if (peutAscensionner()) ascensionne = true;
    }

    /** Multiplicateur étoile : 1.00 à 1.25, +0.10 supplementaire si ascensionne. */
    public double getMultiplicateurEtoile() {
        double mult = 1.0 + (nbreEtoiles * 0.05);
        if (ascensionne) mult += 0.10;
        return mult;
    }

    protected void initialiserVieMax() {
        this.vieMax            = this.vie;
        this.attaqueBase       = this.attaque;
        this.defenseBase       = this.defense;
        this.vitesseBase       = this.vitesse;
        this.tauxCritiquesBase = this.taux_critiques;
        this.degatCritiquesBase= this.degat_critiques;
        this.tauxEsquivesBase  = this.taux_esquives;
        this.tauxBlocageBase   = this.taux_blocage;
    }

    public boolean estVivant() { return this.vie > 0; }

    /**
     * Résultat d'un appel à subirDegats : contient les infos nécessaires
     * pour que l'appelant construise son propre message de log.
     */
    public static class ResultatDegats {
        public final boolean invincible;
        public final boolean bloque;
        public final double degatsAppliques;
        public final boolean ko;
        public final boolean bouclierAbsorbe;
        public final double degatsAbsorbesBouclier;
        public final double pvRestantsBouclier;

        public ResultatDegats(boolean invincible, boolean bloque,
                              double degatsAppliques, boolean ko, boolean bouclierAbsorbe,
                              double degatsAbsorbesBouclier, double pvRestantsBouclier) {
            this.invincible             = invincible;
            this.bloque                 = bloque;
            this.degatsAppliques        = degatsAppliques;
            this.ko                     = ko;
            this.bouclierAbsorbe        = bouclierAbsorbe;
            this.degatsAbsorbesBouclier = degatsAbsorbesBouclier;
            this.pvRestantsBouclier     = pvRestantsBouclier;
        }
    }

    public ResultatDegats subirDegats(double degats) {
        return subirDegats(degats, 100.0);
    }

    /** @param tauxAttaqueSAttaquant Attaque S de l'attaquant (100 = neutre) : reduit l'efficacite du blocage. */
    public ResultatDegats subirDegats(double degats, double tauxAttaqueSAttaquant) {
        Invincibilite invinc = getEffet(Invincibilite.class);
        if (invinc != null) {
            return new ResultatDegats(true, false, 0, false, false, 0, 0);
        }

        double blocageEffectif = Math.min(this.getTauxBlocage() / (tauxAttaqueSAttaquant / 100.0), 0.90);
        boolean bloque = Math.random() < blocageEffectif;
        double multiplicateurDefense = 100.0 / (100.0 + this.getDefense() * 0.5);
        degats *= multiplicateurDefense;
        if (bloque) {
            degats *= (1 - this.reduction_blocage);
        }

        Fragilite fragilite = getEffet(Fragilite.class);
        if (fragilite != null) degats = fragilite.appliquerSurDegats(degats);

        Marquage marquage = getEffet(Marquage.class);
        if (marquage != null) degats = marquage.appliquerSurDegats(degats);

        boolean bouclierAbsorbe = false;
        double degatsAbsorbesBouclier = 0;
        double pvRestantsBouclier = 0;
        Bouclier bouclier = getEffet(Bouclier.class);
        if (bouclier != null && degats > 0) {
            double degatsAvantBouclier = degats;
            degats = bouclier.absorberDegats(degats);
            bouclierAbsorbe = true;
            degatsAbsorbesBouclier = degatsAvantBouclier - degats;
            pvRestantsBouclier = bouclier.getPointsBouclier();
        }

        this.vie = Math.max(0, this.vie - degats);

        Sommeil sommeil = getEffet(Sommeil.class);
        if (sommeil != null && degats > 0) sommeil.reveillerSiDegats();

        if (!estVivant()) {
            Resurrection resurrection = getEffet(Resurrection.class);
            if (resurrection != null) resurrection.tenterResurrection(this);
        }

        return new ResultatDegats(false, bloque, degats, !estVivant(),
                bouclierAbsorbe, degatsAbsorbesBouclier, pvRestantsBouclier);
    }
    public void retirerVie(double montant) {
        this.vie = Math.max(0, this.vie - montant);
        if (!estVivant()) {
            Effets.Resurrection resurrection = getEffet(Effets.Resurrection.class);
            if (resurrection != null) resurrection.tenterResurrection(this);
        }
    }

    /** Surcharge avec log — utilisée par les DoT (Brûlure, Saignement, Poison) via appliquerEffets(log). */
    public void retirerVie(double montant, java.util.List<String> log) {
        this.vie = Math.max(0, this.vie - montant);
        if (!estVivant()) {
            Effets.Resurrection resurrection = getEffet(Effets.Resurrection.class);
            if (resurrection != null) resurrection.tenterResurrection(this, log);
        }
    }

    public void restaurerPv(double montant) {
        this.vie = Math.min(getVieMax(), this.vie + montant);
    }

    public void recevoirSoin(double montant, List<String> log) {
        if (aEffet(Brulure.class)) {
            log.add("🔥 " + this.nom + " est en feu ! Le soin est annule !");
            return;
        }
        Malediction malediction = getEffet(Malediction.class);
        if (malediction != null) montant = malediction.appliquerSurSoin(montant);
        double pvAvant = this.vie;
        restaurerPv(montant);
        double soinEffectif = this.vie - pvAvant;
        if (soinEffectif > 0) {
            log.add("💚 " + this.nom + " recupere " + String.format("%.0f", soinEffectif)
                    + " PV (" + String.format("%.0f", pvAvant) + " → " + String.format("%.0f", this.vie) + " PV)");
        } else {
            log.add(this.nom + " est deja au maximum de PV.");
        }
    }

    public double getRage() { return this.rage; }

    /** Chance fixe (identique pour tous, alliés comme ennemis) que tout gain de rage soit doublé. */
    private static final double CHANCE_RAGE_CRITIQUE = 0.15;

    public void ajouterRage(double montant) {
        Ralentissement ralen = getEffet(Ralentissement.class);
        if (ralen != null) montant = ralen.appliquerSurGainRage(montant);
        if (Math.random() < CHANCE_RAGE_CRITIQUE) montant *= 2;
        this.rage += montant;
    }

    public void reinitialiserRage() {
        this.rage = rageApresUltime();
        this.specialeUtilisee = false;
    }

    /** Rage conservee juste apres le declenchement de l'ultime (0 par defaut). Redefinissable par personnage. */
    protected double rageApresUltime() { return 0; }
    
    public void reinitialiserPourCombat() {
    this.vie = getVieMax();      // PV à fond (tient compte des équipements)
    this.rage = 0;
    this.specialeUtilisee = false;
    this.effetsActifs.clear();
    BonusSet.BonusSpecial special = bonusSetSpecialActif();
    if (special != null && special.bonusVolDeVie() > 0) {
        ajouterEffet(new Absorption(Integer.MAX_VALUE, special.bonusVolDeVie()));
    }
}

    public boolean getSpecialeUtilisee() { return this.specialeUtilisee; }
    public void setSpecialeUtilisee(boolean valeur) { this.specialeUtilisee = valeur; }

    public double getVieMax() {
        double base = this.vieMax * getMultiplicateurEtoile();
        base += bonusSetPV();
        base += getBonusEquipementPV();
        if (bonusTitre > 0) base *= (1 + bonusTitre);
        if (bonusLienPV      > 0) base *= (1 + bonusLienPV);
        if (bonusCompagnonsPV > 0) base += bonusCompagnonsPV;
        if (bonusCreaturePV  > 0) base += bonusCreaturePV;
        double bonusPierreVie = getBonusPierreFraction(Pierre.Type.VIE);
        if (bonusPierreVie > 0) base *= (1 + bonusPierreVie);
        return base;
    }

    @Override public double getVie() { return this.vie; }
    public void setVie(double vie) { this.vie = vie; }
    public void setVieMax(double vieMax) { this.vieMax = vieMax; }
    public void setAttaqueBase(double v) { this.attaqueBase = v; this.attaque = v; }
    public void setDefenseBase(double v) { this.defenseBase = v; this.defense = v; }
    public void setVitesseBase(double v) { this.vitesseBase = v; this.vitesse = v; }

    @Override
    public double getAttaque() {
        BuffAttaque buff        = getEffet(BuffAttaque.class);
        ReductionAttaque debuff = getEffet(ReductionAttaque.class);
        double base = attaqueBase * getMultiplicateurEtoile();
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (bonusTitre > 0) base *= (1 + bonusTitre);
        base += getBonusEquipementATK();
        base *= (1 + bonusSetAttaquePct());
        if (bonusLienATK      > 0) base *= (1 + bonusLienATK);
        if (bonusCompagnonsATK > 0) base += bonusCompagnonsATK;
        if (bonusCreatureATK  > 0) base += bonusCreatureATK;
        double bonusPierreForce = getBonusPierreFraction(Pierre.Type.FORCE);
        if (bonusPierreForce > 0) base *= (1 + bonusPierreForce);
        return base;
    }

    public void setAttaque(double attaque) {
        this.attaque = attaque;
        this.attaqueBase = attaque;
    }

    @Override
    public double getDefense() {
        BuffDefense buff         = getEffet(BuffDefense.class);
        ReductionDefense debuff  = getEffet(ReductionDefense.class);
        double base = defenseBase * getMultiplicateurEtoile();
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (bonusTitre > 0) base *= (1 + bonusTitre);
        base += getBonusEquipementDEF();
        if (bonusLienDEF      > 0) base *= (1 + bonusLienDEF);
        if (bonusCompagnonsDEF > 0) base += bonusCompagnonsDEF;
        if (bonusCreatureDEF  > 0) base += bonusCreatureDEF;
        return base;
    }

    public void setDefense(double defense) {
        this.defense = defense;
        this.defenseBase = defense;
    }

    @Override
    public double getVitesse() {
        BuffVitesse buff         = getEffet(BuffVitesse.class);
        ReductionVitesse debuff  = getEffet(ReductionVitesse.class);
        double base = vitesseBase * getMultiplicateurEtoile();
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (bonusTitre > 0) base *= (1 + bonusTitre);
        base += getBonusEquipementVIT();
        base *= (1 + bonusSetVitessePct());
        if (bonusLienVIT      > 0) base *= (1 + bonusLienVIT);
        if (bonusCompagnonsVIT > 0) base += bonusCompagnonsVIT;
        if (bonusCreatureVIT  > 0) base += bonusCreatureVIT;
        double bonusPierreAgilite = getBonusPierreFraction(Pierre.Type.AGILITE);
        if (bonusPierreAgilite > 0) base *= (1 + bonusPierreAgilite);
        return base;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
        this.vitesseBase = vitesse;
    }

    @Override
    public double getTauxCritique() {
        BuffTauxCritique buff = getEffet(BuffTauxCritique.class);
        double base = buff != null ? tauxCritiquesBase + buff.getBonus() : tauxCritiquesBase;
        base += getBonusPierreFraction(Pierre.Type.CRITIQUE);
        BonusSet.BonusSpecial special = bonusSetSpecialActif();
        if (special != null) base += special.bonusTauxCritique();
        return base;
    }

    public void setTauxCritique(double taux) {
        this.taux_critiques = taux;
        this.tauxCritiquesBase = taux;
    }

    @Override
    public double getTauxDegatCritique() {
        BuffDegatCritique buff = getEffet(BuffDegatCritique.class);
        double base = buff != null ? degatCritiquesBase + buff.getBonus() : degatCritiquesBase;
        BonusSet.BonusSpecial special = bonusSetSpecialActif();
        if (special != null) base += special.bonusDegatCritiquePct();
        return base;
    }

    public void setTauxDegatCritique(double degat) {
        this.degat_critiques = degat;
        this.degatCritiquesBase = degat;
    }

    @Override
    public double getTauxPrecisions() {
        BuffPrecision buff   = getEffet(BuffPrecision.class);
        Aveuglement   debuff = getEffet(Aveuglement.class);
        double base = this.taux_precisions + getBonusPierrePoints(Pierre.Type.PRECISION);
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        return base;
    }
    public void setPrecisions(double precision) { this.taux_precisions = precision; }

    @Override
    public double getTauxEsquives() {
        BuffTauxEsquive buff = getEffet(BuffTauxEsquive.class);
        double base = buff != null ? tauxEsquivesBase + buff.getBonus() : tauxEsquivesBase;
        base += getBonusPierreFraction(Pierre.Type.ESQUIVE);
        BonusSet.BonusSpecial special = bonusSetSpecialActif();
        if (special != null) base += special.bonusEsquive();
        return base;
    }

    public void setTauxEsquives(double esquive) {
        this.taux_esquives = esquive;
        this.tauxEsquivesBase = esquive;
    }

    @Override
    public double getTauxBlocage() {
        BuffBlocage buff = getEffet(BuffBlocage.class);
        double base = buff != null ? tauxBlocageBase + buff.getPourcentage() : tauxBlocageBase;
        return base + getBonusPierreFraction(Pierre.Type.BLOCAGE);
    }

    public void setTauxBlocage(double blocage) {
        this.taux_blocage = blocage;
        this.tauxBlocageBase = blocage;
    }

    public double getTauxAttaqueS() { return taux_attaque_s + getBonusPierrePoints(Pierre.Type.ATTAQUE_S); }
    public void setTauxAttaqueS(double attaqueS) { this.taux_attaque_s = attaqueS; }

    public double getTauxContre() { return taux_contre + getBonusPierrePoints(Pierre.Type.CONTRE); }
    public void setTauxContre(double contre) { this.taux_contre = contre; }

    public boolean isDernierCoupCritique() { return dernierCoupCritique; }
    public void setDernierCoupCritique(boolean v) { this.dernierCoupCritique = v; }

    public double getAttaqueBase() { return attaqueBase; }
    public double getDegatsRenvoi() { return degats_renvoi; }

    public void gagnerExperience(int montant) {
        this.experience += montant;
        System.out.println(this.nom + " gagne " + montant + " XP ! ("
                + this.experience + "/" + this.experienceMax + ")");
        while (this.experience >= this.experienceMax) {
            this.experience -= this.experienceMax;
            monterDeNiveau();
        }
    }

    public void monterDeNiveau() {
        this.niveau++;
        this.experienceMax = (int)(this.experienceMax * 1.20);
        this.vieMax += this.vieMax * 0.05;
        this.vie = getVieMax();
        this.attaque   += this.attaque   * 0.05;
        this.defense   += this.defense   * 0.05;
        this.vitesse   += this.vitesse   * 0.03;
        this.attaqueBase        = this.attaque;
        this.defenseBase        = this.defense;
        this.vitesseBase        = this.vitesse;
        this.tauxCritiquesBase  = this.taux_critiques;
        this.degatCritiquesBase = this.degat_critiques;
        this.tauxEsquivesBase   = this.taux_esquives;
        this.tauxBlocageBase    = this.taux_blocage;
        System.out.println(this.nom + " passe au niveau " + this.niveau + " !");
        System.out.println("Nouvelles stats : " + String.format("%.0f", this.vie) + " PV, "
                + String.format("%.0f", this.attaque) + " ATK, "
                + String.format("%.0f", this.defense) + " DEF, "
                + String.format("%.0f", this.vitesse) + " VIT");
    }

    /** Même chose sans log console — utilisé pour les adversaires arène. */
    public void monterDeNiveauSilencieux() {
        this.niveau++;
        this.experienceMax = (int)(this.experienceMax * 1.20);
        this.vieMax   += this.vieMax   * 0.05;
        this.vie       = getVieMax();
        this.attaque  += this.attaque  * 0.05;
        this.defense  += this.defense  * 0.05;
        this.vitesse  += this.vitesse  * 0.03;
        this.attaqueBase        = this.attaque;
        this.defenseBase        = this.defense;
        this.vitesseBase        = this.vitesse;
        this.tauxCritiquesBase  = this.taux_critiques;
        this.degatCritiquesBase = this.degat_critiques;
        this.tauxEsquivesBase   = this.taux_esquives;
        this.tauxBlocageBase    = this.taux_blocage;
    }

    /**
     * Reinitialise ce personnage au niveau 1 en annulant exactement la croissance des stats
     * accumulee par les montees de niveau (inverse de monterDeNiveau/monterDeNiveauSilencieux).
     * L'equipement, les etoiles, les liens et les autres bonus externes restent inchanges car
     * ils sont recalcules dynamiquement par les getters (getAttaque(), getVieMax()...).
     * Utilise par le transfert de niveaux entre personnages (MenuPersonnage.transfererNiveaux).
     */
    public void reinitialiserNiveauUn() {
        if (this.niveau <= 1) return;
        double diviseurStat = Math.pow(1.05, this.niveau - 1);
        double diviseurVit  = Math.pow(1.03, this.niveau - 1);
        this.attaqueBase /= diviseurStat;
        this.defenseBase /= diviseurStat;
        this.vitesseBase /= diviseurVit;
        this.vieMax      /= diviseurStat;
        this.attaque = this.attaqueBase;
        this.defense = this.defenseBase;
        this.vitesse = this.vitesseBase;
        this.vie     = getVieMax();
        this.niveau       = 1;
        this.experience    = 0;
        this.experienceMax = 300;
    }

    public void ajouterEffet(Effet effet) {
        if (effet instanceof Poison) {
            Poison poisonExistant = getEffet(Poison.class);
            if (poisonExistant != null) {
                poisonExistant.ajouterStack();
                System.out.println(this.nom + " : Poison renforce (stack "
                        + poisonExistant.getStacks() + ")");
                return;
            }
        }
        effetsActifs.removeIf(e -> e.getClass() == effet.getClass());
        effetsActifs.add(effet);
        effet.appliquer(this);
    }

    public ArrayList<Effet> getEffetsActifs() { return this.effetsActifs; }
    private final HashMap<Equipement.Slot, Equipement> equipements = new HashMap<>();

    public void appliquerEffets() {
        effetsActifs.removeIf(effet -> {
            effet.tick(this);
            return effet.estTermine();
        });
    }

    public void appliquerEffets(List<String> log) {
        effetsActifs.removeIf(effet -> {
            effet.tick(this, log);
            return effet.estTermine();
        });
    }

    public <T extends Effet> T getEffet(Class<T> type) {
        return effetsActifs.stream()
                .filter(e -> type.isInstance(e) && !e.estTermine())
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    public boolean aEffet(Class<? extends Effet> type) {
        return getEffet(type) != null;
    }

    @Override public abstract void attaqueBase(PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);
    @Override public abstract void attaqueSpeciale(PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);
    @Override public abstract void attaqueUltime(
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log);
    @Override public abstract String[] getNomsAttaques();
    @Override public abstract void descriptionAttaqueBase();
    @Override public abstract void descriptionAttaqueSpeciale();
    @Override public abstract void descriptionAttaqueUltime();

    public int getExperience() { return this.experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public int getExperienceMax() { return this.experienceMax; }
    public void setExperienceMax(int experienceMax) { this.experienceMax = experienceMax; }

    public void equiper(Equipement e) {
        equipements.put(e.getSlot(), e);
        // getVieMax() inclut déjà getBonusEquipementPV() dynamiquement,
        // donc on ne modifie PAS this.vieMax ici pour éviter le double-comptage.
        if (e.getBonusPV() > 0) {
            this.vie = Math.min(this.vie + e.getBonusPV(), getVieMax());
        }
    }

    public void desequiper(Equipement.Slot slot) {
        Equipement ancien = equipements.remove(slot);
        // getVieMax() recalcule dynamiquement sans cet équipement désormais retiré.
        // On plafonne juste les PV actuels au nouveau maximum.
        if (ancien != null) {
            this.vie = Math.min(this.vie, getVieMax());
        }
    }

    public Equipement getEquipement(Equipement.Slot slot) {
        return equipements.get(slot);
    }

    public ArrayList<Equipement> getEquipementsPortes() {
        return new ArrayList<>(equipements.values());
    }

    public double getBonusEquipementATK() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusATK).sum();
    }

    public double getBonusEquipementDEF() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusDEF).sum();
    }

    public double getBonusEquipementPV() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusPV).sum();
    }

    public double getBonusEquipementVIT() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusVIT).sum();
    }

    // ── Bonus des pierres inserees dans les pieces equipees ────────────────
    /** Somme brute des pierres du type donne (ex : 1.5 pour +1.5%), pour les stats deja exprimees en points 100. */
    private double getBonusPierrePoints(Pierre.Type type) {
        double total = 0;
        for (Equipement e : equipements.values()) total += e.getBonusPierre(type);
        return total;
    }

    /** Meme somme convertie en fraction (0.015 pour +1.5%), pour les stats exprimees en 0-1. */
    private double getBonusPierreFraction(Pierre.Type type) {
        return getBonusPierrePoints(type) / 100.0;
    }

    /** Nombre de pieces equipees de la rarete donnee (max 6, une par emplacement). */
    public int compterPieces(Equipement.Rarete rarete) {
        int count = 0;
        for (Equipement e : equipements.values()) {
            if (e.getRarete() == rarete) count++;
        }
        return count;
    }

    /**
     * Rarete du set actuellement porte en plus grand nombre (pour l'affichage de la progression
     * du bonus de set), ou {@code null} si aucune piece n'est equipee.
     */
    public Equipement.Rarete getRareteSetDominante() {
        Equipement.Rarete dominante = null;
        int max = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            int n = compterPieces(r);
            if (n > max) { max = n; dominante = r; }
        }
        return dominante;
    }

    private double bonusSetPV() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_PV) total += BonusSet.palier(r).bonusPV();
        }
        return total;
    }

    private double bonusSetVitessePct() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_VIT) total += BonusSet.palier(r).bonusVitessePct();
        }
        return total;
    }

    private double bonusSetAttaquePct() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_ATK) total += BonusSet.palier(r).bonusAttaquePct();
        }
        return total;
    }

    /** Bonus special de set complet (6/6) actif, uniquement pour SSS/UR ; {@code null} sinon. */
    private BonusSet.BonusSpecial bonusSetSpecialActif() {
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SET_COMPLET) {
                BonusSet.BonusSpecial special = BonusSet.special(r);
                if (special != null) return special;
            }
        }
        return null;
    }
}