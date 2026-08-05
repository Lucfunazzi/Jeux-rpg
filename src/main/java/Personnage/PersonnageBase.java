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
import Equipement.PiecesEquipees;

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
        // Plafonne la reduction de degats de la defense a 80% (comme esquive/blocage/critique
        // sont deja plafonnes a 90%), pour eviter que les degats ne s'effondrent a haut niveau :
        // attaque/defense/vie montent tous de +5%/niveau, donc sans plancher la mitigation
        // 100/(100+0.5*def) tend vers 0 alors que les PV explosent (combats interminables).
        double multiplicateurDefense = Math.max(0.20, 100.0 / (100.0 + this.getDefense() * 0.5));
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

    /** Chance fixe qu'un don de rage a un allie (pas un gain sur sa propre action) soit double. */
    private static final double CHANCE_RAGE_CRITIQUE = 0.15;

    /** Gain de rage sur sa propre action (attaque de base, compensation d'esquive, etc.) : deterministe,
     *  le seul doublement possible est celui du coup critique (deja applique par l'appelant). */
    public void ajouterRage(double montant) {
        Ralentissement ralen = getEffet(Ralentissement.class);
        if (ralen != null) montant = ralen.appliquerSurGainRage(montant);
        this.rage += montant;
    }

    /** Rage donnee par un autre personnage (synergie, effet de soutien...) : chance fixe d'etre doublee. */
    public void ajouterRageAllie(double montant) {
        if (Math.random() < CHANCE_RAGE_CRITIQUE) montant *= 2;
        ajouterRage(montant);
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

    /**
     * Calcule une stat de combat (ATK/DEF/VIT) en cumulant, dans l'ordre : multiplicateur
     * etoile, buff/debuff en %, bonus de titre, bonus d'equipement brut, bonus de set en %,
     * bonus de lien en %, bonus compagnons/creature en points, bonus de pierre en %. Factorise
     * getAttaque/getDefense/getVitesse, qui suivent exactement cette meme sequence — seules les
     * sources de chaque bonus different. La DEF n'a pas de bonus de set ni de pierre : lui
     * passer 0 revient exactement a sauter ces etapes (multiplier par (1+0) ou ajouter 0 est
     * neutre), donc ce partage ne change aucun resultat par rapport aux anciennes methodes.
     */
    private double statCombinee(double statBase, Double buffPct, Double debuffPct,
                                 double bonusEquipement, double bonusSetPct,
                                 double bonusLien, double bonusCompagnon, double bonusCreature,
                                 double bonusPierrePct) {
        double base = statBase * getMultiplicateurEtoile();
        if (buffPct   != null) base *= (1 + buffPct);
        if (debuffPct != null) base *= (1 - debuffPct);
        if (bonusTitre > 0) base *= (1 + bonusTitre);
        base += bonusEquipement;
        base *= (1 + bonusSetPct);
        if (bonusLien      > 0) base *= (1 + bonusLien);
        if (bonusCompagnon > 0) base += bonusCompagnon;
        if (bonusCreature  > 0) base += bonusCreature;
        if (bonusPierrePct > 0) base *= (1 + bonusPierrePct);
        return base;
    }

    @Override
    public double getAttaque() {
        BuffAttaque buff        = getEffet(BuffAttaque.class);
        ReductionAttaque debuff = getEffet(ReductionAttaque.class);
        return statCombinee(attaqueBase,
                buff   != null ? buff.getPourcentage()   : null,
                debuff != null ? debuff.getPourcentage() : null,
                getBonusEquipementATK(), bonusSetAttaquePct(),
                bonusLienATK, bonusCompagnonsATK, bonusCreatureATK,
                getBonusPierreFraction(Pierre.Type.FORCE));
    }

    public void setAttaque(double attaque) {
        this.attaque = attaque;
        this.attaqueBase = attaque;
    }

    @Override
    public double getDefense() {
        BuffDefense buff        = getEffet(BuffDefense.class);
        ReductionDefense debuff = getEffet(ReductionDefense.class);
        return statCombinee(defenseBase,
                buff   != null ? buff.getPourcentage()   : null,
                debuff != null ? debuff.getPourcentage() : null,
                getBonusEquipementDEF(), 0.0,
                bonusLienDEF, bonusCompagnonsDEF, bonusCreatureDEF,
                0.0);
    }

    public void setDefense(double defense) {
        this.defense = defense;
        this.defenseBase = defense;
    }

    @Override
    public double getVitesse() {
        BuffVitesse buff        = getEffet(BuffVitesse.class);
        ReductionVitesse debuff = getEffet(ReductionVitesse.class);
        return statCombinee(vitesseBase,
                buff   != null ? buff.getPourcentage()   : null,
                debuff != null ? debuff.getPourcentage() : null,
                getBonusEquipementVIT(), bonusSetVitessePct(),
                bonusLienVIT, bonusCompagnonsVIT, bonusCreatureVIT,
                getBonusPierreFraction(Pierre.Type.AGILITE));
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

    public double getAttaqueBase() { return attaqueBase; }
    public double getDefenseBase() { return defenseBase; }
    public double getVitesseBase() { return vitesseBase; }
    public double getVieMaxBase() { return vieMax; }
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

    /** Courbe d'XP lineaire : chaque niveau requiert 100 XP de plus que le precedent (base 300 au niveau 1). */
    private static final int PALIER_EXPERIENCE_MAX = 100;

    /**
     * Palier de fin de contenu (niveau 59+, avant/pendant le Chapitre 13) : mur d'XP volontaire
     * d'environ 1-2 semaines de farm annexe (quetes journalieres, arene...) avant de pouvoir
     * debloquer la suite, comme dans les jeux de reference (Unlimited Ninja, Naruto Online).
     */
    private static final int NIVEAU_PALIER_ENDGAME       = 59;
    private static final int PALIER_EXPERIENCE_ENDGAME   = 20000;

    /**
     * XP cumulee necessaire pour atteindre {@code niveauCible} en partant du niveau 1 (0 XP),
     * en simulant exactement la meme sequence que {@link #monterDeNiveau()}. Pure, sans effet
     * de bord — reutilisable par le calcul des paliers de chapitre (voir CourbeChapitres).
     */
    public static long experienceCumuleePourNiveau(int niveauCible) {
        long total = 0;
        long experienceMaxCourant = 300;
        int niveau = 1;
        while (niveau < niveauCible) {
            total += experienceMaxCourant;
            niveau++;
            experienceMaxCourant = (niveau >= NIVEAU_PALIER_ENDGAME)
                    ? PALIER_EXPERIENCE_ENDGAME
                    : experienceMaxCourant + PALIER_EXPERIENCE_MAX;
        }
        return total;
    }

    /**
     * Applique le palier courant a experienceMax. Palier normal : +100 sur la valeur precedente.
     * Palier endgame (niveau >= 59) : cout plat de PALIER_EXPERIENCE_ENDGAME a chaque niveau,
     * sans s'additionner sur l'ancienne valeur (sinon le mur de fin de contenu grossirait sans
     * fin au lieu de rester un cout fixe par niveau).
     */
    private void appliquerPalierExperienceMax() {
        this.experienceMax = (this.niveau >= NIVEAU_PALIER_ENDGAME)
                ? PALIER_EXPERIENCE_ENDGAME
                : this.experienceMax + PALIER_EXPERIENCE_MAX;
    }

    /**
     * Multiplicateur applique au taux de croissance par niveau (1.0 = neutre, +5%/+5%/+3%
     * inchanges). Permet a un personnage dont le coefficient peut changer en cours de partie
     * (ex: rang du personnage principal) de ne repercuter un nouveau coefficient que sur les
     * niveaux gagnes APRES le changement, sans reappliquer retroactivement tout l'historique
     * de croissance deja acquis (ce qui provoquerait un boost instantane et disproportionne).
     */
    protected double multiplicateurCroissanceNiveau() {
        return 1.0;
    }

    public void monterDeNiveau() {
        this.niveau++;
        appliquerPalierExperienceMax();
        double croissance = multiplicateurCroissanceNiveau();
        this.vieMax += this.vieMax * 0.05 * croissance;
        this.vie = getVieMax();
        this.attaque   += this.attaque   * 0.05 * croissance;
        this.defense   += this.defense   * 0.05 * croissance;
        this.vitesse   += this.vitesse   * 0.03 * croissance;
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
        appliquerPalierExperienceMax();
        double croissance = multiplicateurCroissanceNiveau();
        this.vieMax   += this.vieMax   * 0.05 * croissance;
        this.vie       = getVieMax();
        this.attaque  += this.attaque  * 0.05 * croissance;
        this.defense  += this.defense  * 0.05 * croissance;
        this.vitesse  += this.vitesse  * 0.03 * croissance;
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
    private final PiecesEquipees piecesEquipees = new PiecesEquipees();

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
        piecesEquipees.equiper(e);
        // getVieMax() inclut déjà getBonusEquipementPV() dynamiquement,
        // donc on ne modifie PAS this.vieMax ici pour éviter le double-comptage.
        if (e.getBonusPV() > 0) {
            this.vie = Math.min(this.vie + e.getBonusPV(), getVieMax());
        }
    }

    public void desequiper(Equipement.Slot slot) {
        Equipement ancien = piecesEquipees.desequiper(slot);
        // getVieMax() recalcule dynamiquement sans cet équipement désormais retiré.
        // On plafonne juste les PV actuels au nouveau maximum.
        if (ancien != null) {
            this.vie = Math.min(this.vie, getVieMax());
        }
    }

    public Equipement getEquipement(Equipement.Slot slot) {
        return piecesEquipees.get(slot);
    }

    public ArrayList<Equipement> getEquipementsPortes() {
        return piecesEquipees.versListe();
    }

    public double getBonusEquipementATK() { return piecesEquipees.bonusATK(); }
    public double getBonusEquipementDEF() { return piecesEquipees.bonusDEF(); }
    public double getBonusEquipementPV()  { return piecesEquipees.bonusPV(); }
    public double getBonusEquipementVIT() { return piecesEquipees.bonusVIT(); }

    private double getBonusPierrePoints(Pierre.Type type)   { return piecesEquipees.bonusPierrePoints(type); }
    private double getBonusPierreFraction(Pierre.Type type) { return piecesEquipees.bonusPierreFraction(type); }

    /** Nombre de pieces equipees de la rarete donnee (max 6, une par emplacement). */
    public int compterPieces(Equipement.Rarete rarete) { return piecesEquipees.compterPieces(rarete); }

    /**
     * Rarete du set actuellement porte en plus grand nombre (pour l'affichage de la progression
     * du bonus de set), ou {@code null} si aucune piece n'est equipee.
     */
    public Equipement.Rarete getRareteSetDominante() { return piecesEquipees.rareteSetDominante(); }

    private double bonusSetPV()          { return piecesEquipees.bonusSetPV(); }
    private double bonusSetVitessePct()  { return piecesEquipees.bonusSetVitessePct(); }
    private double bonusSetAttaquePct()  { return piecesEquipees.bonusSetAttaquePct(); }

    /** Bonus special de set complet (6/6) actif, uniquement pour SSS/UR ; {@code null} sinon. */
    private BonusSet.BonusSpecial bonusSetSpecialActif() { return piecesEquipees.bonusSetSpecialActif(); }
}