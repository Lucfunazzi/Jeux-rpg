package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Ralentissement implements Effet {
    private int toursRestant;
    private double reductionRage; // ex: 5.0 = gagne 5 rage de moins par tour

    public Ralentissement(int toursRestant, double reductionRage) {
        this.toursRestant = toursRestant;
        this.reductionRage = reductionRage;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est ralenti ! Gain de rage réduit de "
                + reductionRage + " pendant " + toursRestant + " tour(s).");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) toursRestant--;
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Ralentissement"; }

    // Appelée dans ajouterRage() de PersonnageBase — déjà en place
    public double appliquerSurGainRage(double rageBrute) {
        return Math.max(0, rageBrute - reductionRage);
    }

    public int getToursRestant() { return toursRestant; }
    public double getReductionRage() { return reductionRage; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est ralenti ! Gain de rage reduit de "
                + reductionRage + " pendant " + toursRestant + " tour(s).");
    }

}
