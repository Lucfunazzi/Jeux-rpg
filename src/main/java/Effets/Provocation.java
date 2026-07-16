package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Provocation implements Effet {
    private int toursRestant;
    private PersonnageBase source;

    /**
     * @param toursRestant Nombre de tours pendant lesquels la cible est provoquee.
     * @param source       Le personnage qui a lance la provocation.
     */
    public Provocation(int toursRestant, PersonnageBase source) {
        this.toursRestant = toursRestant;
        this.source = source;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est provoque par " + source.getNom()
                + " pendant " + toursRestant + " tours !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("La provocation sur " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() {
        return toursRestant <= 0 || !source.estVivant();
    }

    @Override
    public String getNom() { return "Provocation"; }

    public PersonnageBase getSource() { return source; }
    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est provoque par " + source.getNom()
                + " pendant " + toursRestant + " tours !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("La provocation sur " + cible.getNom() + " se termine !");
        }
    }

}
