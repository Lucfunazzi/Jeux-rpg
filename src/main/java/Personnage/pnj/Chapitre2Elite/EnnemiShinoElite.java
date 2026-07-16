package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.Bouclier;
import Effets.Poison;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiShinoElite extends PersonnageBase {

    public EnnemiShinoElite() {
        this(22);
    }

    public EnnemiShinoElite(int niveau) {
        this.nom    = "Shino";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "C";

        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 201.7 * mult * niv;
        this.attaque =  64.3 * mult * niv;
        this.defense =  49.7 * mult * niv;
        this.vitesse =  79.5 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Nuee d'insectes", "Essaim corrosif", "Colonie devoratrice"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shino utilise Nuee d'insectes !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Poison(1, 0.03), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shino utilise Essaim corrosif !");
        double degats = this.getAttaque() * 0.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.05), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shino utilise Colonie devoratrice !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Poison(2, 0.04), log);
            }
        }
        PersonnageBase cible = null;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                if (cible == null || allie.getVie() < cible.getVie())
                    cible = allie;
            }
        }
        if (cible != null) {
            double montantBouclier = this.getAttaque() * 0.80;
            Combat.appliquerEffet(this, cible, new Bouclier(montantBouclier), log);
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Nuee d'insectes — inflige 100% ATK a une cible et applique Poison (3% PV/tour) 1 tour si touche.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Essaim corrosif — inflige 70% ATK a une cible, reduit son ATK de 10% pendant 2 tours et l'empoisonne (5% PV/tour) 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Colonie devoratrice — inflige 110% ATK a tous les ennemis, applique Poison (4% PV/tour) 2 tours, et applique un Bouclier (80% ATK) a l'allie le plus bas.");
    }
}