package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Malediction implements Effet {
    private int toursRestant;
    private double reductionSoins;

    /**
     * @param toursRestant  Nombre de tours pendant lesquels la cible est maudite.
     * @param reductionSoins Pourcentage de reduction des soins recus (ex: 0.50 pour -50%).
     */
    public Malediction(int toursRestant, double reductionSoins) {
        this.toursRestant = toursRestant;
        this.reductionSoins = reductionSoins;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est maudit ! Soins reduits de "
                + (int)(reductionSoins * 100) + "% pendant " + toursRestant + " tours.");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("La malediction de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Malediction"; }

    public double appliquerSurSoin(double soinsBruts) {
        return soinsBruts * (1.0 - reductionSoins);
    }

    public int getToursRestant() { return toursRestant; }
    public double getReductionSoins() { return reductionSoins; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est maudit ! Soins reduits de "
                + (int)(reductionSoins * 100) + "% pendant " + toursRestant + " tours.");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("La malediction de " + cible.getNom() + " se termine !");
        }
    }

}
