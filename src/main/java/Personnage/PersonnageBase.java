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
import Effets.ReductionDefense;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Effets.Poison;
import Effets.Bouclier;
import Effets.Brulure;
import java.util.ArrayList;
import java.util.List;
import Equipement.Equipement;
import Effets.BuffTitre;
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
    private double bonusCompagnonsATK = 0.0;
    private double bonusCompagnonsDEF = 0.0;
    private double bonusCompagnonsPV  = 0.0;
    private double bonusCompagnonsVIT = 0.0;
    private double bonusCreatureATK = 0.0;
    private double bonusCreatureDEF = 0.0;
    private double bonusCreaturePV  = 0.0;
    private double bonusCreatureVIT = 0.0;

    protected ArrayList<Effet> effetsActifs = new ArrayList<>();

    public String getNom() { return this.nom; }
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
        Invincibilite invinc = getEffet(Invincibilite.class);
        if (invinc != null) {
            return new ResultatDegats(true, false, 0, false, false, 0, 0);
        }

        boolean bloque = Math.random() < this.getTauxBlocage();
        if (bloque) {
            degats *= (1 - this.reduction_blocage);
        } else {
            double multiplicateurDefense = 100.0 / (100.0 + this.getDefense() * 0.5);
            degats *= multiplicateurDefense;
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
            log.add("💚 " + this.nom + " recupere " + String.format("%.1f", soinEffectif)
                    + " PV (" + String.format("%.1f", pvAvant) + " → " + String.format("%.1f", this.vie) + " PV)");
        }
    }

    public double getRage() { return this.rage; }

    public void ajouterRage(double montant) {
        Ralentissement ralen = getEffet(Ralentissement.class);
        if (ralen != null) montant = ralen.appliquerSurGainRage(montant);
        this.rage += montant;
    }

    public void reinitialiserRage() {
        this.rage = 0;
        this.specialeUtilisee = false;
    }
    
    public void reinitialiserPourCombat() {
    this.vie = getVieMax();      // PV à fond (tient compte des équipements)
    this.rage = 0;
    this.specialeUtilisee = false;
    this.effetsActifs.clear();
}

    public boolean getSpecialeUtilisee() { return this.specialeUtilisee; }
    public void setSpecialeUtilisee(boolean valeur) { this.specialeUtilisee = valeur; }

    public double getVieMax() {
        BuffTitre titre = getEffet(BuffTitre.class);
        double base = this.vieMax;
        if (compterPiecesRangC() >= 3) base += 200;
        base += getBonusEquipementPV();
        if (titre != null) base *= (1 + titre.getBonus());
        if (bonusLienPV      > 0) base *= (1 + bonusLienPV);
        if (bonusCompagnonsPV > 0) base *= (1 + bonusCompagnonsPV);
        if (bonusCreaturePV  > 0) base += bonusCreaturePV;
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
        BuffTitre titre         = getEffet(BuffTitre.class);
        double base = attaqueBase;
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (titre  != null) base *= (1 + titre.getBonus());
        base += getBonusEquipementATK();
        if (compterPiecesRangC() >= 6) base *= 1.05;
        if (bonusLienATK      > 0) base *= (1 + bonusLienATK);
        if (bonusCompagnonsATK > 0) base *= (1 + bonusCompagnonsATK);
        if (bonusCreatureATK  > 0) base += bonusCreatureATK;
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
        BuffTitre titre          = getEffet(BuffTitre.class);
        double base = defenseBase;
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (titre  != null) base *= (1 + titre.getBonus());
        base += getBonusEquipementDEF();
        if (bonusLienDEF      > 0) base *= (1 + bonusLienDEF);
        if (bonusCompagnonsDEF > 0) base *= (1 + bonusCompagnonsDEF);
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
        BuffTitre titre          = getEffet(BuffTitre.class);
        double base = vitesseBase;
        if (buff   != null) base *= (1 + buff.getPourcentage());
        if (debuff != null) base *= (1 - debuff.getPourcentage());
        if (titre  != null) base *= (1 + titre.getBonus());
        base += getBonusEquipementVIT();
        if (compterPiecesRangC() >= 4) base *= 1.02;
        if (bonusLienVIT      > 0) base *= (1 + bonusLienVIT);
        if (bonusCompagnonsVIT > 0) base *= (1 + bonusCompagnonsVIT);
        if (bonusCreatureVIT  > 0) base += bonusCreatureVIT;
        return base;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
        this.vitesseBase = vitesse;
    }

    @Override
    public double getTauxCritique() {
        BuffTauxCritique buff = getEffet(BuffTauxCritique.class);
        return buff != null ? tauxCritiquesBase + buff.getBonus() : tauxCritiquesBase;
    }

    public void setTauxCritique(double taux) {
        this.taux_critiques = taux;
        this.tauxCritiquesBase = taux;
    }

    @Override
    public double getTauxDegatCritique() {
        BuffDegatCritique buff = getEffet(BuffDegatCritique.class);
        return buff != null ? degatCritiquesBase + buff.getBonus() : degatCritiquesBase;
    }

    public void setTauxDegatCritique(double degat) {
        this.degat_critiques = degat;
        this.degatCritiquesBase = degat;
    }

    @Override public double getTauxPrecisions() { return this.taux_precisions; }
    public void setPrecisions(double precision) { this.taux_precisions = precision; }

    @Override
    public double getTauxEsquives() {
        BuffTauxEsquive buff = getEffet(BuffTauxEsquive.class);
        return buff != null ? tauxEsquivesBase + buff.getBonus() : tauxEsquivesBase;
    }

    public void setTauxEsquives(double esquive) {
        this.taux_esquives = esquive;
        this.tauxEsquivesBase = esquive;
    }

    @Override
    public double getTauxBlocage() {
        BuffBlocage buff = getEffet(BuffBlocage.class);
        return buff != null ? tauxBlocageBase + buff.getPourcentage() : tauxBlocageBase;
    }

    public void setTauxBlocage(double blocage) {
        this.taux_blocage = blocage;
        this.tauxBlocageBase = blocage;
    }

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
        System.out.println("Nouvelles stats : " + String.format("%.1f", this.vie) + " PV, "
                + String.format("%.1f", this.attaque) + " ATK, "
                + String.format("%.1f", this.defense) + " DEF, "
                + String.format("%.1f", this.vitesse) + " VIT");
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

    private int compterPiecesRangC() {
        int count = 0;
        for (Equipement e : equipements.values()) {
            if (e.getRarete() == Equipement.Rarete.C) count++;
        }
        return count;
    }
}