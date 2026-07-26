package Equipement;

import java.util.List;

/**
 * Boite d'equipement — a l'ouverture, donne un exemplaire de chaque piece d'un set complet
 * du rang correspondant : les 4 armes (une par classe) + les 5 pieces d'armure.
 */
public enum BoiteEquipement {

    C(Equipement.Rarete.C), B(Equipement.Rarete.B), A(Equipement.Rarete.A),
    S(Equipement.Rarete.S), SS(Equipement.Rarete.SS), SSS(Equipement.Rarete.SSS), UR(Equipement.Rarete.UR);

    public final Equipement.Rarete rarete;
    public final String nom;

    BoiteEquipement(Equipement.Rarete rarete) {
        this.rarete = rarete;
        this.nom    = "Boite d'equipement [" + rarete + "]";
    }

    /** Ouvre 1 boite : retire 1 du stock, ajoute les 9 pieces du set complet a l'inventaire. */
    public String ouvrir(Inventaire inventaire) {
        if (!inventaire.retirerMateriau(nom, 1)) return "Aucune " + nom + " en stock.";
        List<Equipement> pieces = EquipementFactory.piecesSetComplet(rarete);
        for (Equipement e : pieces) inventaire.ajouterEquipement(e);
        return nom + " ouverte ! Vous recevez le set complet [" + rarete + "] : 4 armes + 5 pieces d'armure.";
    }

    @Override
    public String toString() { return nom; }
}
