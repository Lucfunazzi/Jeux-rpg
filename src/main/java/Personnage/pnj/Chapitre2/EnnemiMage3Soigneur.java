package Personnage.pnj.Chapitre2;


import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Purification;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage3Soigneur extends PersonnageBase {

    public EnnemiMage3Soigneur() {
        this(15);
    }

    public EnnemiMage3Soigneur(int niveau) {
        this.nom    = "Prêtre de la révérance";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 250.0 * niv;
        this.attaque =  65.0 * niv;
        this.defense =  22.0 * niv;
        this.vitesse =  72.0 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Soigne" + cible.getNom() + " !");
                    PersonnageBase cibleSoin = null;
            for (PersonnageBase allie : equipeAlliee) {
                if (allie.estVivant()) {
                    if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                        cibleSoin = allie;
                }
            }
            if (cibleSoin != null) {
                double soin = this.getAttaque() * 0.70; 
                cibleSoin.recevoirSoin(soin, log);
                Purification.purifier(cibleSoin, 2, log);
            }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Soigne toute l'équipe !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.50;
               allie.recevoirSoin(soin, log);
               Purification.purifier(allie, 1, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Salve Magique", "Orbe de Tenebres", "Tempete Magique"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Salve Magique : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Orbe de Tenebres : inflige 150% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempete Magique : inflige 130% ATK a toute l'equipe ennemie.");
    }
}
