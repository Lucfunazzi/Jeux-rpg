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
        return new String[]{"Lame de Vent Tranchante", "Mur Tempête", "Mur de Vent — Prison de Tornades"};
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
        log.add("Eligoal érige un Mur Tempête — son bouclier de vent dévaste " + cible.getNom() + " !");
        Combat.appliquerEffet(this, new BuffDefense(0.25, 2), log);
        double degats = this.getAttaque() * 1.25;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal libère le Mur de Vent — une prison de tornades piège toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 0.90) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Silence(2), log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.25, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Lame de Vent Tranchante — 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Mur Tempête — +25% DEF, inflige 125% ATK, réduit ATK cible de 20%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Mur de Vent — 90% ATK à tous (x rage), silence 2 tours, réduit VIT de 25%.");
    }
}
