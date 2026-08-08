package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zero — véritable identité et forme ultime de Brain, rang SS.
 * Magie des ténèbres capable d'effacer purement et simplement ce qu'elle touche.
 */
public class perso_Zero extends PersonnageBase {

    public perso_Zero() {
        this.nom    = "Zero";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "SS";
        this.niveau = 1;
        double mult = 1.75;
        this.vie     = 680 * mult;
        this.attaque = 260 * mult;
        this.defense = 150 * mult;
        this.vitesse = 160 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 110.00;
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
        log.add("Zero projette des Particules Magiques sur " + cible.getNom() + " !");
        for (PersonnageBase ennemi : equipeEnnemie){
            if (ennemi.estVivant()){
                double degats =this.getAttaque() *1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.30){
                    Combat.appliquerEffet(ennemi, new Sommeil(2), log);
                }
                
            }
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain envahit l'esprit de " + cible.getNom() + " — Manipulation Mentale !");
        for(PersonnageBase ennemi : equipeEnnemie){
            if (ennemi.estVivant()){
                double degats = this.getAttaque() *1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.30){
                    Combat.appliquerEffet(ennemi, new Silence(2), log);
                } 
            }
        }
                
        for (PersonnageBase al : equipeAlliee) if (al.estVivant()){
            al.ajouterRage(30);
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
            al.ajouterRage(30);
        }
        double soin = degatsTotaux * 0.30;
        if (soin > 0) {
            this.recevoirSoin(soin, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Barrière de Particules Magiques — Inflige 100% ATK à tout les ennemis et à 30% d'infliger sommeil à toutes l'équipes pendants 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation Mentale — Inflige 100% ATK à tout les ennemis et donne 30 de rage a tout les alliées et à 30% de silences les ennemis pendants 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Drain Magique Absolu — Inflige 125% ATK à tous les ennemis et se soigne de 30% des dégâts infligés et donne 30 de rage a tout les alliée.");
    }
}
