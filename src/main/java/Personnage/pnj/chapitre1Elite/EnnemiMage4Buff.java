package Personnage.pnj.chapitre1Elite;



import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage4Buff extends PersonnageBase {

    public EnnemiMage4Buff() {
        this(14);
    }

    public EnnemiMage4Buff(int niveau) {
        this.nom    = "Sorcier de la prophétie";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 230.0 * niv;
        this.attaque =  92.0 * niv;
        this.defense =  25.0 * niv;
        this.vitesse =  80.0 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
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
        log.add(this.nom + " Meteorie " + cible.getNom() + " !");
        double degats = this.getAttaque() * 0.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        for (PersonnageBase e :equipeAlliee){
            if (e.estVivant() && e.getRole().equals("Support")){
                Combat.appliquerEffet(e, new BuffAttaque(0.10,2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Boost le taux critique de son équipe !");
        for (PersonnageBase e : equipeAlliee) {
            if (e.estVivant()){
                Combat.appliquerEffet(e, new BuffTauxCritique(0.15,2), log);
            }
            
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Eclair Maudit", "Explosion Arcanique", "Pluie de Maledictions"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Eclair Maudit : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Explosion Arcanique : inflige 160% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Pluie de Maledictions : inflige 135% ATK a toute l'equipe ennemie.");
    }
}
