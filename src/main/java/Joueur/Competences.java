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
 *   - competenceArbre3() : nouvelle spéciale débloquée via Arbre 3 (nœud 10)
 *   - competenceArbre5/6/7() : nouvelles spéciales débloquées via Arbres 5/6/7 (nœud 10) — a completer
 *   - ultimeArbre4()     : nouvel ultime débloqué via Arbre 4 (nœud 10) — a completer
 *   - ultimeArbre8()     : nouvel ultime débloqué via Arbre 8 (nœud 10) — a completer
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

    /** Nouvelle spéciale débloquée par l'Arbre 3 (nœud 10). */
    default void competenceArbre3(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre3() {
        System.out.println("Spéciale arbre 3 non implementee.");
    }

    // ── Arbres 5/6/7 (spéciales) et 4/8 (ultimes) — structure posee, contenu a completer ──

    /** Nouvelle spéciale débloquée par l'Arbre 5 (nœud 10). A completer. */
    default void competenceArbre5(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre5() {
        System.out.println("Spéciale arbre 5 non implementee.");
    }

    /** Nouvelle spéciale débloquée par l'Arbre 6 (nœud 10). A completer. */
    default void competenceArbre6(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre6() {
        System.out.println("Spéciale arbre 6 non implementee.");
    }

    /** Nouvelle spéciale débloquée par l'Arbre 7 (nœud 10). A completer. */
    default void competenceArbre7(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre7() {
        System.out.println("Spéciale arbre 7 non implementee.");
    }

    /** Nouvel ultime débloqué par l'Arbre 4 (nœud 10) — remplace ultime(), pas attaqueSpeciale(). A completer. */
    default void ultimeArbre4(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultime arbre non implementee pour cette classe.");
    }

    default void descriptionUltimeArbre4() {
        System.out.println("Ultime arbre 4 non implementee.");
    }

    /** Nouvel ultime débloqué par l'Arbre 8 (nœud 10). A completer. */
    default void ultimeArbre8(Personnage_principale utilisateur,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultime arbre non implementee pour cette classe.");
    }

    default void descriptionUltimeArbre8() {
        System.out.println("Ultime arbre 8 non implementee.");
    }
}