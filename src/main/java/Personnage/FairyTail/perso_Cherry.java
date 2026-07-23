package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;


public class perso_Cherry extends PersonnageBase {

    public perso_Cherry() {
        this.nom    = "Cherry";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 330 * mult;
        this.attaque =  90 * mult;
        this.defense =  100 * mult;
        this.vitesse =  125 * mult;
        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Arbre Marionnette", "Marionnette de l'amour ", "Forêt de l'Amour"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry anime un arbre géant qui attaque " + cible.getNom() + " de ses branches !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry utilise marionnette de l'amour  ");
          PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
        }
    }
    if (cibleSoin == null) return;
    double soin = this.getAttaque() * 0.80;
    cibleSoin.recevoirSoin(soin, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry libère la Forêt de l'amour — la forêt entière se dresse contre les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.50;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);

                if (Math.random() < 0.30) {
                    Combat.appliquerEffet(this, cible, new Silence(2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Arbre Marionnette — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Marionnette de l'amour — Soigne l'allié le plus bas en vie de 80% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Forêt de l'Amour — Inflige 50% ATK à tous, 30% de chance de silence 2 tours sur chaque cible touchée.");
    }
}
