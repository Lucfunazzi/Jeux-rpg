package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bora de Prominens — Elementaliste, rang C.
 * Magie de la Protubérance (Prominens) : flammes violettes.
 * Sorts : Tapis Écarlate (vol), Fouet de la Protubérance, Typhon de la Protubérance, Douche Écarlate.
 * Aussi : Charme Magique (bague) + Magie du Sommeil (bague).
 */
public class perso_Bora extends PersonnageBase {

    public perso_Bora() {
        this.nom    = "Bora";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 300 * mult;
        this.attaque = 115 * mult;
        this.defense =  65 * mult;
        this.vitesse =  85 * mult;
        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
 @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poings", "Fouet de la Protubérance", "Bague de charme"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora fait un coup de poing " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
       
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora utilise Fouet de la Protubérance " + cible.getNom());
       double degats = this.getAttaque() *1.20;
       Combat.appliquerDegatsAvecLog(this, cible, degats, log);
       Combat.appliquerEffet(this, new BuffPrecision(1, 2), log);
           
        }
    

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora utilise Bague de charme  !");

        PersonnageBase cible = Combat.choisirCible(this, equipeEnnemie);
        if (cible == null) return; // sécurité : tous KO

    double degats = this.getAttaque() * 0.80;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);

    if (Math.random() < 0.30) {
        Combat.appliquerEffet(this, cible, new Sommeil(1), log);
    }
}
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de poings — Inflige 100% ATK à un ennemi");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Fouet de la Protubérance — Inflige 120% ATK à un ennemi, augmente sa précision de 100% pendatns 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Bague de Charme— Inflige 80% ATK à un ennemi avec 30% d'endormir la cible pendant 1 tour.");
    }
}
