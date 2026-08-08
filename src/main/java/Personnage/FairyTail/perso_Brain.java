package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Brain — ancien chef d'Oración Seis, rang S.
 * Ancien membre du Conseil de la Magie, maître de la manipulation mentale et du drain de magie.
 */
public class perso_Brain extends PersonnageBase {

    public perso_Brain() {
        this.nom    = "Brain";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.55;
        this.vie     = 540 * mult;
        this.attaque = 170 * mult;
        this.defense = 120 * mult;
        this.vitesse = 110 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Barrière de Particules Magiques", "Manipulation Mentale", "Drain Magique Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain projette des Particules Magiques sur " + cible.getNom() + " !");
        for (PersonnageBase ennemi : equipeEnnemie){
            if (ennemi.estVivant()){
                double degats =this.getAttaque() *1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                
            }
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain envahit l'esprit de " + cible.getNom() + " — Manipulation Mentale !");
        for(PersonnageBase ennemi : equipeEnnemie){
            if (ennemi.estVivant()){
                double degats = this.getAttaque() *0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                        
            }
        }
                
        for (PersonnageBase al : equipeAlliee) if (al.estVivant()){
            al.ajouterRage(25);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain draine la magie de toute l'équipe ennemie !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                degatsTotaux += degats;
            }
        }
        for (PersonnageBase al : equipeAlliee) if (al.estVivant()){
            al.ajouterRage(25);
        }
        double soin = degatsTotaux * 0.20;
        if (soin > 0) {
            this.recevoirSoin(soin, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Barrière de Particules Magiques — Inflige 100% ATK à tout les ennemis.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation Mentale — Inflige 80% ATK à tout les ennemis et donne 25 de rage a tout les alliées.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Drain Magique Absolu — Inflige 100% ATK à tous les ennemis et se soigne de 20% des dégâts infligés et donne 25 de rage a tout les alliée.");
    }
}
