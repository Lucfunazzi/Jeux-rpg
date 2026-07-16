package Effets;
import Personnage.PersonnageBase;
import java.util.ArrayList;

public class Transfert {

    /**
     * Transfère tous les effets negatifs de soi vers ennemi.
     * Passe par ajouterEffet() pour que appliquer() soit bien declenche sur la cible.
     */
    public void transfererEffetsNegatifs(PersonnageBase soi, PersonnageBase ennemi) {
        ArrayList<Effet> aTransferer = new ArrayList<>();

        for (Effet effet : new ArrayList<>(soi.getEffetsActifs())) {
            if (estEffetNegatif(effet)) {
                aTransferer.add(effet);
            }
        }

        if (aTransferer.isEmpty()) {
            System.out.println(soi.getNom() + " n'a aucun effet negatif a transferer.");
            return;
        }

        for (Effet effet : aTransferer) {
            soi.getEffetsActifs().remove(effet);
            ennemi.ajouterEffet(effet);
            System.out.println(effet.getNom() + " transfere sur " + ennemi.getNom() + " !");
        }

        System.out.println(aTransferer.size() + " effet(s) transfere(s) sur " + ennemi.getNom() + " !");
    }

    private boolean estEffetNegatif(Effet effet) {
        return effet instanceof Confusion
            || effet instanceof Etourdissement
            || effet instanceof Fragilite
            || effet instanceof Gel
            || effet instanceof Malediction
            || effet instanceof Marquage
            || effet instanceof Paralysie
            || effet instanceof Poison
            || effet instanceof Brulure
            || effet instanceof Saignement
            || effet instanceof Ralentissement
            || effet instanceof ReductionAttaque
            || effet instanceof ReductionDefense
            || effet instanceof ReductionVitesse
            || effet instanceof Sommeil
            || effet instanceof Silence
            || effet instanceof Trempe;
    }
}