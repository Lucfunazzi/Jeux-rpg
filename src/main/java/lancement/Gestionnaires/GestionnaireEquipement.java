package lancement.Gestionnaires;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.Pierre;
import Personnage.PersonnageBase;
import java.util.List;
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

    /**
     * Pour chaque piece equipee par {@code cible}, remplit les emplacements de pierres encore
     * vides en suivant l'ordre de priorite des types associe au role du personnage (Tank : Contre,
     * Vie, Blocage, Esquive ; Support : Agilite, Force, Attaque S, Vie ; DPS/autre : Force,
     * Precision, Attaque S). Pour chaque type prioritaire absent de la piece, insere la pierre du
     * niveau le plus eleve disponible en inventaire. Ne touche pas aux emplacements deja occupes.
     * @return un recapitulatif des pierres inserees, ou un message indiquant qu'il n'y a rien a faire.
     */
    public static String autoEquiperPierres(PersonnageBase cible, Inventaire inventaire) {
        List<Pierre.Type> priorite = prioritePierresPourRole(cible.getRole());
        StringBuilder detail = new StringBuilder();
        int nbInsertions = 0;

        for (Equipement.Slot slot : Equipement.Slot.values()) {
            Equipement equip = cible.getEquipement(slot);
            if (equip == null) continue;

            for (Pierre.Type type : priorite) {
                boolean dejaPresent = false;
                int emplacementVide = -1;
                for (int i = 0; i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
                    Pierre p = equip.getPierre(i);
                    if (p != null && p.getType() == type) { dejaPresent = true; break; }
                    if (p == null && emplacementVide == -1) emplacementVide = i;
                }
                if (dejaPresent || emplacementVide == -1) continue;

                int meilleurNiveau = meilleurNiveauDisponible(inventaire, type);
                if (meilleurNiveau == -1) continue;

                Pierre pierre = new Pierre(type, meilleurNiveau);
                equip.insererPierre(emplacementVide, pierre);
                inventaire.retirerPierre(type, meilleurNiveau, 1);
                detail.append("  ").append(equip.getNomSlot()).append(" : ").append(pierre.getNom()).append("\n");
                nbInsertions++;
            }
        }

        if (nbInsertions == 0) {
            return cible.getNom() + " a deja les pierres prioritaires de son role inserees (ou stock insuffisant).";
        }
        return nbInsertions + " pierre(s) inseree(s) sur " + cible.getNom() + " :\n" + detail.toString().trim();
    }

    private static int meilleurNiveauDisponible(Inventaire inventaire, Pierre.Type type) {
        int meilleur = -1;
        for (int niveau = 1; niveau <= Pierre.NIVEAU_MAX; niveau++) {
            if (inventaire.getQuantitePierre(type, niveau) > 0) meilleur = niveau;
        }
        return meilleur;
    }

    private static List<Pierre.Type> prioritePierresPourRole(String role) {
        if (role == null) role = "";
        return switch (role) {
            case "Tank"    -> List.of(Pierre.Type.CONTRE, Pierre.Type.VIE, Pierre.Type.BLOCAGE, Pierre.Type.ESQUIVE);
            case "Support" -> List.of(Pierre.Type.AGILITE, Pierre.Type.FORCE, Pierre.Type.ATTAQUE_S, Pierre.Type.VIE);
            default        -> List.of(Pierre.Type.FORCE, Pierre.Type.PRECISION, Pierre.Type.ATTAQUE_S); // DPS
        };
    }
}
