package Personnage.pnj.chapitre1Elite;


import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bora de Prominens — Mage de la Protubérance, rang C.
 * Arc Prologue : se faisait passer pour Salamander de Fairy Tail.
 * Magie de la Protubérance (feu) : flammes violettes en spirale + Charme Magique + Magie du Sommeil.
 * Sorts principaux : Fouet de la Protubérance, Typhon de la Protubérance, Douche Écarlate.
 */
public class EnnemiBora extends PersonnageBase {

    public EnnemiBora() { this(12); }

    public EnnemiBora(int niveau) {
        this.nom    = "Bora";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 150.0 * niv;
        this.attaque =  85.0 * niv;
        this.defense =  25.0 * niv;
        this.vitesse =  75.0 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.03;
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
        System.out.println("Coup de poings — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Fouet de la Protubérance — Inflige 120% ATK à un ennemi, augmente sa précision de 100% pendatns 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Bague de Charme— Inflige 80% ATK à un ennemi avec 30% d'endormir la cible.");
    }
}
