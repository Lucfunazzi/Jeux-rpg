package Effets;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class Transfert {

    /**
     * Transfere tous les effets negatifs de soi vers ennemi.
     */
    public void transfererEffetsNegatifs(PersonnageBase soi, PersonnageBase ennemi, List<String> log) {
        ArrayList<Effet> aTransferer = new ArrayList<>();

        for (Effet effet : new ArrayList<>(soi.getEffetsActifs())) {
            if (estEffetNegatif(effet)) {
                aTransferer.add(effet);
            }
        }

        if (aTransferer.isEmpty()) {
            log.add(soi.getNom() + " n'a aucun effet negatif a transferer.");
            return;
        }

        for (Effet effet : aTransferer) {
            soi.getEffetsActifs().remove(effet);
            Combat.appliquerEffet(soi, ennemi, effet, log);
        }

        log.add(aTransferer.size() + " effet(s) transfere(s) sur " + ennemi.getNom() + " !");
    }

    /** Surcharge sans log pour compatibilite. */
    public void transfererEffetsNegatifs(PersonnageBase soi, PersonnageBase ennemi) {
        transfererEffetsNegatifs(soi, ennemi, new ArrayList<>());
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
