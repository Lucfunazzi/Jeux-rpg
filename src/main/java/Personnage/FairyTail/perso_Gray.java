package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Gray extends PersonnageBase {

    public perso_Gray() {
        this.nom = "Gray";
        this.type="Elementaliste";
        this.role = "DPS"; // ✅
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 120 * multiplicateurRarete;
        this.vitesse = 120 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lance de glace", "Marteau de glace", "Ultra geyser de glace"};
    }

    
  @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Gray utilise Lance de glace !");
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Marteau de glace !");
    double degats = this.getAttaque() * 1.20;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
    if (Math.random() < 0.30) {
        Combat.appliquerEffet(this, cible, new Gel(2), log);
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Ultra geyser de glace !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (Math.random() < 0.30) {
                Combat.appliquerEffet(this, ennemi, new Gel(2), log);
            }
        }
    }
    Combat.appliquerEffet(this, new BuffVitesse(0.20, 2), log);
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lance de glace — Inflige 120% ATK a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Marteau de glace — Inflige 120% ATK a la cible. "
                + "Reduit sa vitesse de 20% pendant 2 tours. "
                + "30% de chance de Gel pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Ultra geyser de glace — Inflige 120% ATK a tous les ennemis. "
                + "30% de chance de Gel sur chaque cible pendant 2 tours. "
                + "Augmente la vitesse de Gray de 20% pendant 2 tours.");
    }
}