package lancement.Gestionnaires;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Personnage.PersonnageBase;
import lancement.RangJoueur;

/** Auto-équipement : remplace, slot par slot, l'équipement porté par la meilleure pièce disponible en inventaire. */
public class GestionnaireEquipement {

    private GestionnaireEquipement() {}

    /**
     * Pour chaque slot, compare la piece deja portee aux candidats de l'inventaire compatibles
     * avec la classe de {@code cible} et le rang joueur actuel, et equipe la meilleure des deux
     * ("meilleure" = somme des bonus actuels PV+ATK+DEF+VIT la plus elevee, fortification et
     * affinage deja pris en compte). L'ancienne piece remplacee est remise en inventaire.
     * @return un recapitulatif des changements, ou un message indiquant qu'il n'y a rien a ameliorer.
     */
    public static String equiperMeilleurDisponible(PersonnageBase cible, Inventaire inventaire, RangJoueur rangJoueur) {
        StringBuilder detail = new StringBuilder();
        int nbAmeliorations = 0;

        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement actuel = cible.getEquipement(slot);
            double meilleureValeur = actuel != null ? valeurPiece(actuel) : -1;
            Equipement meilleur = null;

            for (Equipement e : inventaire.getEquipements()) {
                if (e.getSlot() != slot) continue;
                if (!EquipementFactory.estCompatibleArme(cible.getType(), e)) continue;
                if (rangJoueur.getRang().ordinal() < EquipementFactory.rangJoueurRequisPourEquiper(e.getRarete())) continue;
                if (valeurPiece(e) > meilleureValeur) {
                    meilleureValeur = valeurPiece(e);
                    meilleur = e;
                }
            }

            if (meilleur != null) {
                inventaire.retirerEquipement(meilleur);
                if (actuel != null) inventaire.ajouterEquipement(actuel);
                // Copie independante : sinon plusieurs personnages equipant "le meme type" depuis
                // l'inventaire partageraient le meme objet, et fortifier l'un affecterait tous les autres.
                cible.equiper(meilleur.copier());
                detail.append("  ").append(meilleur.getNomSlot()).append(" : ")
                        .append(actuel != null ? actuel.getNomAffiche() + " -> " : "(vide) -> ")
                        .append(meilleur.getNomAffiche()).append("\n");
                nbAmeliorations++;
            }
        }

        if (nbAmeliorations == 0) {
            return cible.getNom() + " porte deja le meilleur equipement disponible dans l'inventaire.";
        }
        return nbAmeliorations + " piece(s) amelioree(s) sur " + cible.getNom() + " :\n" + detail.toString().trim();
    }

    private static double valeurPiece(Equipement e) {
        return e.getBonusATK() + e.getBonusDEF() + e.getBonusPV() + e.getBonusVIT();
    }
}
