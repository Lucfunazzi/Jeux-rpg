package Effets;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Peur : la cible evite d'attaquer la source de la peur pendant sa duree
 * (oppose de Provocation). Branche dans Combat.choisirCible().
 */
public class Peur implements Effet, EffetNegatif {
    private int toursRestant;
    private final PersonnageBase source;

    /**
     * @param toursRestant Nombre de tours pendant lesquels la cible fuit la source.
     * @param source       Le personnage qui inspire la peur.
     */
    public Peur(int toursRestant, PersonnageBase source) {
        this.toursRestant = toursRestant;
        this.source = source;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est terrorise par " + source.getNom()
                + " et evitera de l'attaquer pendant " + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("La peur de " + cible.getNom() + " se dissipe !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0 || !source.estVivant(); }

    @Override
    public String getNom() { return "Peur"; }

    public PersonnageBase getSource() { return source; }
    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est terrorise par " + source.getNom()
                + " et evitera de l'attaquer pendant " + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("La peur de " + cible.getNom() + " se dissipe !");
        }
    }
}
