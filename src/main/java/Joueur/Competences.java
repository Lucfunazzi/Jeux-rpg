package Joueur;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Interface de compétences du personnage principal.
 *
 * Chaque classe possède :
 *   - attaqueSpeciale()  : compétence de base (toujours disponible)
 *   - ultime()           : ultime de base (toujours disponible)
 *   - competenceArbre()  : nouvelle spéciale débloquée via Arbre 1 (nœud 10)
 *   - competenceArbre2() : nouvelle spéciale débloquée via Arbre 2 (nœud 10)
 */
public interface Competences {

    /** Attaque spéciale de base — toujours disponible dès le départ. */
    void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    /** Ultime de base — toujours disponible dès le départ. */
    void ultime(PersonnageBase utilisateur,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    /** Noms affichés : [0] = spéciale, [1] = ultime */
    String[] getNomsCompetences();

    void descriptionAttaqueSpeciale();
    void descriptionUltime();

    /** Nouvelle attaque spéciale débloquée par l'Arbre 1 (nœud 10). */
    default void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre() {
        System.out.println("Aucune description disponible.");
    }

    /** Nouvelle spéciale débloquée par l'Arbre 2 (nœud 10). */
    default void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre2() {
        System.out.println("Spéciale arbre 2 non implementee.");
    }
}