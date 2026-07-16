package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class ContreAttaque implements Effet {
    private int toursRestant;
    private double multiplicateurDegats; // ex: 0.50 = riposte à 50% de l'attaque

    public ContreAttaque(int toursRestant, double multiplicateurDegats) {
        this.toursRestant = toursRestant;
        this.multiplicateurDegats = multiplicateurDegats;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est prêt à riposter ("
                + (int)(multiplicateurDegats * 100) + "% des dégâts) pendant "
                + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) toursRestant--;
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "ContreAttaque"; }

    // Appelée dans attaquer() de Combat après que la cible reçoit des dégâts
    public void riposte(PersonnageBase soi, PersonnageBase attaquant, java.util.List<String> log) {
        if (attaquant.estVivant()) {
            double degatsRiposte = soi.getAttaque() * multiplicateurDegats;
            PersonnageBase.ResultatDegats resultat = attaquant.subirDegats(degatsRiposte);
            log.add(soi.getNom() + " riposte pour "
                    + String.format("%.1f", resultat.degatsAppliques) + " degats sur "
                    + attaquant.getNom() + " !");
            if (resultat.ko) log.add(attaquant.getNom() + " est KO !");
        }
    }

    public int getToursRestant() { return toursRestant; }
    public double getMultiplicateurDegats() { return multiplicateurDegats; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est pret a riposter ("
                + (int)(multiplicateurDegats * 100) + "% des degats) pendant "
                + toursRestant + " tour(s) !");
    }

}
