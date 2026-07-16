package Joueur;

import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public interface Competences {

    void choix_competence1(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    void choix_competence2(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    void choix_competence3(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    void ultime(PersonnageBase utilisateur,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log);

    String[] getNomsCompetences();
    void descriptionCompetence1();
    void descriptionCompetence2();
    void descriptionCompetence3();
    void descriptionUltime();

    default void competenceArbre(Personnage_principale utilisateur,
            PersonnageBase cible,
            List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Competence arbre non implementee pour cette classe.");
    }

    default void descriptionCompetenceArbre() {
        System.out.println("Aucune description disponible.");
    }
    
    default void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
        List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie,
        List<String> log) {
    log.add("Competence arbre 2 non implementee.");
}

default void descriptionCompetenceArbre2() {
    System.out.println("Competence arbre 2 non implementee.");
}
}