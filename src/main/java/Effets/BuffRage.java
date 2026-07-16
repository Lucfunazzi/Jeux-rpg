package Effets;

import Personnage.PersonnageBase;
import java.util.ArrayList;

public class BuffRage {
    private double quantiteRage;

    /**
     * @param quantiteRage Quantite de rage ajoutee (ex: 30).
     */
    public BuffRage(double quantiteRage) {
        this.quantiteRage = quantiteRage;
    }

    public void appliquer(PersonnageBase cible) {
        cible.ajouterRage(quantiteRage);
        System.out.println(cible.getNom() + " gagne " + quantiteRage + " rage !");
    }

    public void appliquerSurEquipe(ArrayList<PersonnageBase> equipe) {
        for (PersonnageBase perso : equipe) {
            if (perso.estVivant()) {
                perso.ajouterRage(quantiteRage);
                System.out.println(perso.getNom() + " gagne " + quantiteRage + " rage !");
            }
        }
    }

    public double getQuantiteRage() { return quantiteRage; }
}