package Personnage.FairyTail;

import Combat.Combat;
import Effets.Trempe;
import Effets.ReductionVitesse;
import Effets.BuffAttaque;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Jubia extends PersonnageBase {

    public perso_Jubia() {
        this.nom = "Jubia";
        this.type = "Mage";
        this.role = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.20;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Balle d'eau", "Torrent aquatique", "Monde de l'eau"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Balle d'eau !");
        double degats = this.getAttaque() * 0.90;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Trempe(2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Torrent aquatique !");
        // Trempe AoE sur tous les ennemis
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerEffet(this, ennemi, new Trempe(2), log);
            }
        }
        // Dégâts + ralentissement sur la cible principale
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.25, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia utilise Monde de l'eau !");
        // Dégâts + Trempe longue sur tous les ennemis
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Trempe(3), log);
            }
        }
        // Synergie Gray : boost son ATK s'il est allié vivant
        for (PersonnageBase allie : equipeAlliee) {
            if (allie instanceof perso_Gray && allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.30, 2), log);
                log.add("La synergie Eau & Glace s'active ! Gray gagne 30% ATK pendant 2 tours !");
                break;
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Balle d'eau — Inflige 90% ATK a la cible. Applique Trempe pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Torrent aquatique — Applique Trempe a tous les ennemis pendant 2 tours. "
                + "Inflige 110% ATK a la cible principale. "
                + "Reduit sa vitesse de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Monde de l'eau — Inflige 130% ATK a tous les ennemis. "
                + "Applique Trempe pendant 3 tours sur chaque cible. "
                + "Si Gray est allie : augmente son ATK de 30% pendant 2 tours.");
    }
}