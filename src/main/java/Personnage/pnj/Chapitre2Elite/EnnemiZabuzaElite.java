package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffTauxEsquive;
import Effets.Gel;
import Effets.Marquage;
import Effets.Saignement;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiZabuzaElite extends PersonnageBase {

    public EnnemiZabuzaElite() {
        this(26);
    }

    public EnnemiZabuzaElite(int niveau) {
        this.nom    = "Zabuza";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 300.2 * mult * niv;
        this.attaque =  120.3 * mult * niv;
        this.defense =  60.5 * mult * niv;
        this.vitesse =  80.2 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Tranchant de la Decapiteuse", "Technique du Camouflage dans la Brume", "Execution Silencieuse"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " assene un coup lourd avec sa Decapiteuse !");
        double degats = this.getAttaque() * 1.00;
        boolean synergieActive = cible.getEffet(Gel.class) != null || cible.getEffet(Marquage.class) != null;
        if (synergieActive) {
            degats *= 1.30;
            log.add("Synergie activee ! Zabuza profite de l'ouverture pour frapper plus fort !");
        }
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (synergieActive) {
            Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " utilise la Brume Clignotante pour disparaitre !");
        Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.05, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " surgit de la brume pour une Execution Silencieuse !");
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (ennemi.getEffet(Gel.class) != null || ennemi.getEffet(Marquage.class) != null) {
                    cible = ennemi; break;
                }
            }
        }
        if (cible == null) {
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (ennemi.estVivant()) { cible = ennemi; break; }
            }
        }
        if (cible == null) return;
        double multiplicateurDegats = 1.50;
        if (cible.getEffet(Gel.class) != null) {
            multiplicateurDegats = 1.80;
            log.add("Combo Glace & Brume ! L'ennemi est immobile, Zabuza l'execute sans pitie !");
        }
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        double degats = (this.getAttaque() * multiplicateurDegats) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (cible.estVivant()) Combat.appliquerEffet(this, cible, new Saignement(3, 0.10), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Tranchant de la Decapiteuse — Inflige 100% ATK. Si la cible est Gelee ou Marquee, inflige +30% et applique Saignement (5% PV/tour) 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Technique du Camouflage dans la Brume — Augmente son ATK de 10% et son Esquive de 5% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Execution Silencieuse — Inflige 150% ATK a une cible (180% si Gelee). Applique Saignement (10% PV/tour) 3 tours.");
    }
}