package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Cherry (Sherry Brendy) — Elementaliste, rang C.
 * Magie des Poupées (Marionnettes) : contrôle objets, végétaux et esprits célestes.
 * Lamia Scale. Amoureuse de Lyon. Sort contre Lucy : contrôle de Taurus.
 */
public class perso_Cherry extends PersonnageBase {

    public perso_Cherry() {
        this.nom    = "Cherry";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 330 * mult;
        this.attaque =  85 * mult;
        this.defense =  80 * mult;
        this.vitesse =  80 * mult;
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
        return new String[]{"Arbre Marionnette", "Contrôle des Esprits", "Marionnette de l'Amour"};
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
        log.add("Cherry tente de prendre le contrôle des invocations de " + cible.getNom() );
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        
       
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry libère la Marionnette de l'Amour — la forêt entière se dresse contre les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.50;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                
               
                
            }
            if (Math.random() < 0.30){
                 Combat.appliquerEffet(this, cible, new Silence(2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Arbre Marionnette — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Contrôle des Esprits — Inflige 110% ATK et réduit l'ATK de 15% de la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Marionnette de l'Amour — Inflige 50% ATK à tous, à 30% de silence les cibles pendants 2 tours.");
    }
}
