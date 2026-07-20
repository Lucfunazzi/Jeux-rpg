package Effets;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Immunité — protège la cible contre tous les effets négatifs pendant N tours.
 *
 * Tant que cet effet est actif, tout effet implémentant {@link EffetNegatif}
 * est bloqué dans {@code Combat.appliquerEffet} avant même d'être appliqué.
 *
 * Exemple d'utilisation :
 *   Combat.appliquerEffet(source, cible, new Immunite(2), log);
 *   // → la cible sera immune aux effets négatifs pendant 2 tours.
 */
public class Immunite implements Effet {

    private int toursRestants;

    public Immunite(int tours) {
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add("🛡 " + cible.getNom() + " est immunisé aux effets négatifs pendant "
                + toursRestants + " tour(s) !");
    }

    @Override public void appliquer(PersonnageBase cible) { /* géré par appliquer(cible, log) */ }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        if (toursRestants > 0) {
            toursRestants--;
            if (toursRestants > 0) {
                log.add("🛡 " + cible.getNom() + " : Immunité active (" + toursRestants + " tour(s) restant(s)).");
            } else {
                log.add("🛡 " + cible.getNom() + " : Immunité expirée.");
            }
        }
    }

    @Override public void tick(PersonnageBase cible) { if (toursRestants > 0) toursRestants--; }

    @Override public boolean estTermine()  { return toursRestants <= 0; }
    @Override public String  getNom()      { return "Immunite"; }

    public int getToursRestants() { return toursRestants; }
}
