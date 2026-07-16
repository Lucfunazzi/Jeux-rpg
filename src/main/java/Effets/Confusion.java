package Effets;

import Personnage.PersonnageBase;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Confusion implements Effet {
    private int toursRestant;
    private final Random random = new Random();

    public Confusion(int toursRestant) {
        this.toursRestant = toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est confus pendant "
                + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) {
            toursRestant--;
        }
    }

    @Override
    public boolean estTermine() { 
        return toursRestant <= 0; 
    }

    @Override
    public String getNom() { 
        return "Confusion"; 
    }

    /**
     * Redirige une attaque vers un allié au hasard si le personnage est confus.
     * @param equipeAlliee La liste de l'équipe du personnage confus.
     * @param attaquant Le personnage qui subit l'effet de confusion.
     * @return La nouvelle cible alliée, ou null si aucun allié n'est disponible.
     */
    public PersonnageBase redirigerVersAllie(ArrayList<PersonnageBase> equipeAlliee, PersonnageBase attaquant) {
        ArrayList<PersonnageBase> ciblesPossibles = new ArrayList<>();
        
        for (PersonnageBase perso : equipeAlliee) {
            // Un personnage ne peut pas s'attaquer lui-même via cette redirection et la cible doit être vivante
            if (perso.estVivant() && perso != attaquant) {
                ciblesPossibles.add(perso);
            }
        }

        if (ciblesPossibles.isEmpty()) {
            // Tous les alliés sont KO ou le personnage est seul sur le terrain
            System.out.println(attaquant.getNom()
                    + " est confus mais aucun allié n'est disponible pour recevoir le coup !");
            return null;
        }

        // Sélection aléatoire parmi les alliés valides
        PersonnageBase cible = ciblesPossibles.get(random.nextInt(ciblesPossibles.size()));
        System.out.println("(!) " + attaquant.getNom() + " est confus et s'en prend à son allié "
                + cible.getNom() + " !");
        return cible;
    }

    public int getToursRestant() { 
        return toursRestant; 
    }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est confus pendant " + toursRestant + " tour(s) !");
    }

}
