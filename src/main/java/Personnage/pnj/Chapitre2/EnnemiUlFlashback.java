package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ul Milkovich telle qu'elle apparait dans le flashback du Chapitre 2, stage 9
 * ("Le passe de Gray"). Distincte du personnage jouable Personnage.FairyTail.perso_Ul :
 * son ultime scelle Deliora d'un coup (Iced Shell), ce qui serait casse sur une
 * version recrutable. Utilisee uniquement par Chapitre2.lancerStage9AvecUl.
 */
public class EnnemiUlFlashback extends PersonnageBase {

    public EnnemiUlFlashback() {
        this.nom    = "Ul Milkovich";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;

        double multiplicateurRarete = 1.75;
        this.vie     = 700 * multiplicateurRarete;
        this.attaque = 240 * multiplicateurRarete;
        this.defense = 140 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Ice-Make : Lance", "Ice-Make : Prison de Glace", "Glace Absolue (Iced Shell)"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " utilise Ice-Make : Lance sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " emprisonne " + cible.getNom() + " dans une Prison de Glace, mais sa resistance en annule presque tout l'effet !");
        double degats = this.getAttaque() * 0.25;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " sacrifie tout et lance Glace Absolue : Iced Shell !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                log.add(ennemi.getNom() + " est scelle a jamais dans la glace eternelle !");
                ennemi.retirerVie(ennemi.getVie(), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Lance — inflige 100% ATK a la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Prison de Glace — inflige seulement 25% ATK, quasi sans effet face a une resistance extreme.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Glace Absolue (Iced Shell) — scelle instantanement tous les ennemis, quels que soient leurs PV ou leur defense.");
    }
}
