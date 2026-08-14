package Equipement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Décrit une recette de forge : composants nécessaires et équipement produit. */
public final class ForgeRecette {
    private final String nom;
    private final Map<String, Integer> coutsMateriaux;
    private final Equipement resultat;

    public ForgeRecette(String nom, Equipement resultat, Map<String, Integer> coutsMateriaux) {
        this.nom = nom;
        this.resultat = resultat;
        this.coutsMateriaux = Collections.unmodifiableMap(new LinkedHashMap<>(coutsMateriaux));
    }

    public String getNom() { return nom; }
    public Equipement getResultat() { return resultat; }
    public Map<String, Integer> getCoutsMateriaux() { return coutsMateriaux; }

    public boolean estFabricable(Inventaire inventaire) {
        for (var cout : coutsMateriaux.entrySet()) {
            if (inventaire.getQuantiteMateriau(cout.getKey()) < cout.getValue()) return false;
        }
        return true;
    }

    /** Consomme les composants uniquement si la recette est réalisable. */
    public boolean fabriquer(Inventaire inventaire) {
        if (!estFabricable(inventaire)) return false;
        for (var cout : coutsMateriaux.entrySet()) {
            inventaire.retirerMateriau(cout.getKey(), cout.getValue());
        }
        inventaire.ajouterEquipement(resultat);
        return true;
    }
}
