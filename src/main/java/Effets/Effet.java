package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public interface Effet {
    void appliquer(PersonnageBase cible);
    void tick(PersonnageBase cible);
    boolean estTermine();
    String getNom();

    /** Surcharge avec log — chaque impl peut la redéfinir pour logguer ses messages. */
    default void appliquer(PersonnageBase cible, List<String> log) {
        appliquer(cible); // fallback : comportement hérité sans log
    }

    /** Surcharge avec log — chaque impl peut la redéfinir pour logguer ses messages. */
    default void tick(PersonnageBase cible, List<String> log) {
        tick(cible); // fallback : comportement hérité sans log
    }
}
