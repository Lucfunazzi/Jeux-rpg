package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Brulure implements Effet {
    private int toursRestant;
    private double pourcentageDegats;

    /**
     * @param toursRestant      Nombre de tours pendant lesquels l'effet dure.
     * @param pourcentageDegats Pourcentage des PV actuels retiré à chaque tour (ex: 0.05 pour 5%).
     */
    public Brulure(int toursRestant, double pourcentageDegats) {
        this.toursRestant = toursRestant;
        this.pourcentageDegats = pourcentageDegats;
    }

   @Override
public void appliquer(PersonnageBase cible) {
    // Vide — le log dans Combat.appliquerEffet() affiche déjà "applique [Brulure]"
    // Ne pas println ici sinon l'affichage s'intercale avant les dégâts
}

@Override
public void tick(PersonnageBase cible) {
    if (toursRestant > 0) {
        double degats = cible.getVie() * pourcentageDegats;
        toursRestant--;
        cible.retirerVie(degats); // ✅ bypass défense — c'est un DoT
        System.out.println(cible.getNom() + " perd " + String.format("%.1f", degats)
                + " PV (brûlure, " + toursRestant + " tours restants)");
    }
}

    @Override
    public boolean estTermine() {
        return toursRestant <= 0;
    }

    @Override
    public String getNom() {
        return "Brulure";
    }

    public int getToursRestant() {
        return toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        // Le message d'application est géré par Combat.appliquerEffet()
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestant > 0) {
            double degats = cible.getVie() * pourcentageDegats;
            toursRestant--;
            cible.retirerVie(degats);
            log.add(cible.getNom() + " perd " + String.format("%.1f", degats)
                    + " PV (brulure, " + toursRestant + " tours restants)");
        }
    }

}
