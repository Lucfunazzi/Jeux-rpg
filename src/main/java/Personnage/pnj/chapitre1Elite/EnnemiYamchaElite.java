package Personnage.pnj.chapitre1Elite;

import Effets.Brulure;
import Effets.BuffTauxCritique;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiYamchaElite extends PersonnageBase {

    public EnnemiYamchaElite() {
        this(16);
    }

    public EnnemiYamchaElite(int niveau) {
        this.nom    = "Yamcha";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 577.2 * niv;
        this.attaque = 144.3 * niv;
        this.defense =  72.2 * niv;
        this.vitesse = 128.4 * vit;

        this.taux_critiques    = 0.30;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pieds", "Kamehameha", "Sokidan"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Coup de pied !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Kamehameha !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Sokidan !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getVie() < cible.getVie())
                    cible = ennemi;
            }
        }
        if (cible == null) return;
        double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de pieds — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Kamehameha — inflige 110% ATK a une cible et augmente son taux Critique de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Sokidan — inflige 110% ATK a la cible la plus faible et applique Brulure (8% PV/tour) pendant 2 tours.");
    }
}