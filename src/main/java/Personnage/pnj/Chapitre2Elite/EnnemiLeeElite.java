package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffTauxCritique;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiLeeElite extends PersonnageBase {

    public EnnemiLeeElite() {
        this(28);
    }

    public EnnemiLeeElite(int niveau) {
        this.nom    = "Lee";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 218.1 * mult * niv;
        this.attaque =  82.9 * mult * niv;
        this.defense =  48.0 * mult * niv;
        this.vitesse =  78.7 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pieds", "Danse de Konoha", "Premier Porte"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lee utilise Coup de pieds !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lee utilise Danse de Konoha !");
        PersonnageBase cibleDanse = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cibleDanse == null || ennemi.getAttaque() > cibleDanse.getAttaque())
                    cibleDanse = ennemi;
            }
        }
        if (cibleDanse == null) return;
        double degats = this.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(this, cibleDanse, degats, log);
        Combat.appliquerEffet(this, cibleDanse, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lee lance Premier Porte !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.10, 2), log);
            }
        }
        Combat.appliquerEffet(this, new BuffTauxCritique(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de pieds — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Danse de Konoha — inflige 180% ATK a l'ennemi avec la plus haute ATK et reduit sa defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Premier Porte — inflige 110% ATK a tous les ennemis, reduit leur defense de 10% pendant 2 tours, et augmente son taux critique de 15% et son ATK de 10% pendant 2 tours.");
    }
}