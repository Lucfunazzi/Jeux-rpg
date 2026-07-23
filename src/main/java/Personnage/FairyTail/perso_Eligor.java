package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Eligoal — Elementaliste, rang C.
 * Magie du Vent : attaques invisibles et tranchantes, Mur Tempête (bouclier), Mur de Vent (prison).
 * Leader d'Eisenwald, surnommé "Shinigami".
 */
public class perso_Eligor extends PersonnageBase {

    public perso_Eligor() {
        this.nom    = "Eligoal";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 360 * mult;
        this.attaque = 125 * mult;
        this.defense =  85 * mult;
        this.vitesse = 115 * mult;
        this.taux_critiques    = 0.14;
        this.degat_critiques   = 1.28;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Faux", "Lame de Vent", "Mur de Vent — Prison de Tornades"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal déchaîne des lames de vent invisible sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal Lance Lame de Vent " + cible.getNom() + " !");
     
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal libère Storm Bringer sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 0.70) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                
                Combat.appliquerEffet(this, cible, new Saignement(2,0.05), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Faux — 100% ATK à un ennemi.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lame de Vent —  inflige 140% ATK à un ennemi.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Storm Bringer— 70% ATK à tous les ennemis, inflige saignement pendants 2 tours (5% des pv).");
    }
}
