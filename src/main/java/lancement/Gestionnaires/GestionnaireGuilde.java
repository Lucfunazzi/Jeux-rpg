package lancement.Gestionnaires;

import Equipement.BoiteGuilde;
import Equipement.CarteOr;
import Equipement.CleCoffreGuilde;
import Equipement.Inventaire;
import Equipement.ParcheminAptitude;
import Equipement.PotionEnergie;
import Equipement.SceauDeRang;
import Joueur.Personnage_principale;
import lancement.Menus.MenuExamenS;
import lancement.Menus.MenuTirage_recrutement;
import lancement.RangJoueur;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Recompenses "Guilde" issues des quetes journalieres :
 *   - Boite Mysterieuse : ouverture directe, lot aleatoire.
 *   - Cle de Coffre : cumulative, {@link CleCoffreGuilde#SEUIL_OUVERTURE} cles (une semaine) ouvrent
 *     le grand Coffre de Guilde, dont la recompense depend du Rang Joueur (C a UR).
 */
public final class GestionnaireGuilde {

    private static final Random RNG = new Random();

    private GestionnaireGuilde() {}

    /** Ouvre 1 Boite Mysterieuse de la Guilde : lot aleatoire parmi or / cartes d'or / pierres d'affinage / potions. */
    public static String ouvrirBoiteMysterieuse(Inventaire inventaire, Personnage_principale joueur) {
        if (!inventaire.retirerMateriau(BoiteGuilde.NOM, 1)) return "Aucune " + BoiteGuilde.NOM + " en stock.";

        return switch (RNG.nextInt(4)) {
            case 0 -> { joueur.ajouterOr(2000); yield "Vous obtenez 2 000 or !"; }
            case 1 -> { inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 10); yield "Vous obtenez 10x " + CarteOr.NIVEAU_1.nom + " !"; }
            case 2 -> { inventaire.ajouterMateriau("Pierre d'affinage", 15); yield "Vous obtenez 15x Pierre d'affinage !"; }
            default -> { inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 3); yield "Vous obtenez 3x " + PotionEnergie.MOYENNE.nom + " !"; }
        };
    }

    // ── Grand Coffre de Guilde (7 cles, recompense selon le Rang Joueur) ────

    /** Ouvre le grand Coffre de Guilde : consomme SEUIL_OUVERTURE cles, donne un lot selon le Rang Joueur (C a UR). */
    public static String ouvrirCoffreGuilde(Inventaire inv, Personnage_principale joueur,
                                             MenuTirage_recrutement menuTirage, RangJoueur.Rang rang) {
        int stock = inv.getQuantiteMateriau(CleCoffreGuilde.NOM);
        if (stock < CleCoffreGuilde.SEUIL_OUVERTURE) {
            return "Cles insuffisantes : " + stock + " / " + CleCoffreGuilde.SEUIL_OUVERTURE + ".";
        }
        inv.retirerMateriau(CleCoffreGuilde.NOM, CleCoffreGuilde.SEUIL_OUVERTURE);

        List<String> parts = new ArrayList<>();
        int coupons = 0;
        int parcheminElite = 0;

        switch (rang) {
            case C -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_1, 10));
                ajouter(parts, item(inv, PotionEnergie.MOYENNE.nom, 2));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 3));
                coupons = 20; parcheminElite = 1;
            }
            case B -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_2, 10));
                ajouter(parts, item(inv, PotionEnergie.MOYENNE.nom, 3));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 5));
                coupons = 40; parcheminElite = 3;
            }
            case A -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_3, 10));
                ajouter(parts, item(inv, PotionEnergie.GRANDE.nom, 2));
                ajouter(parts, boitePierre(inv, 2, 3));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 5));
                coupons = 60; parcheminElite = 5;
            }
            case S -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_4, 10));
                ajouter(parts, item(inv, PotionEnergie.GRANDE.nom, 3));
                ajouter(parts, boitePierre(inv, 3, 3));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 20));
                coupons = 120; parcheminElite = 10;
            }
            case SS -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_4, 50));
                ajouter(parts, item(inv, PotionEnergie.GRANDE.nom, 10));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 100));
                ajouter(parts, sceau(inv, SceauDeRang.S, 10));
                ajouter(parts, sceau(inv, SceauDeRang.SS, 5));
                coupons = 200; parcheminElite = 15;
            }
            case SSS -> {
                ajouter(parts, boitePierre(inv, 6, 5));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 350));
                ajouter(parts, sceau(inv, SceauDeRang.S, 20));
                ajouter(parts, sceau(inv, SceauDeRang.SS, 10));
                ajouter(parts, sceau(inv, SceauDeRang.SSS, 5));
                ajouter(parts, item(inv, PotionEnergie.GRANDE.nom, 20));
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_5, 80));
                coupons = 350; parcheminElite = 25;
            }
            case UR -> {
                ajouter(parts, carteOr(inv, CarteOr.NIVEAU_5, 250));
                ajouter(parts, boitePierre(inv, 6, 10));
                ajouter(parts, item(inv, ParcheminAptitude.NOM, 700));
                ajouter(parts, item(inv, PotionEnergie.GRANDE.nom, 30));
                ajouter(parts, sceau(inv, SceauDeRang.S, 25));
                ajouter(parts, sceau(inv, SceauDeRang.SS, 20));
                ajouter(parts, sceau(inv, SceauDeRang.SSS, 15));
                ajouter(parts, sceau(inv, SceauDeRang.UR, 5));
                coupons = 500; parcheminElite = 50;
            }
        }

        if (coupons > 0) {
            joueur.setCoupons(joueur.getCoupons() + coupons);
            parts.add(coupons + " coupons");
        }
        if (parcheminElite > 0) {
            menuTirage.setParcheminElite(menuTirage.getParcheminElite() + parcheminElite);
            parts.add(parcheminElite + "x Parchemin de Tirage Elite");
        }

        return "Coffre de Guilde [" + rang + "] ouvert !\n" + String.join(", ", parts);
    }

    // ── Utilitaires de construction (ajoutent a l'inventaire + decrivent) ──

    private static void ajouter(List<String> parts, String description) {
        if (description != null) parts.add(description);
    }

    private static String item(Inventaire inv, String nom, int qte) {
        if (qte <= 0) return null;
        inv.ajouterMateriau(nom, qte);
        return qte + "x " + nom;
    }

    private static String carteOr(Inventaire inv, CarteOr carte, int qte) {
        if (qte <= 0) return null;
        inv.ajouterCartesOr(carte, qte);
        return qte + "x " + carte.nom;
    }

    private static String boitePierre(Inventaire inv, int niveau, int qte) {
        if (qte <= 0) return null;
        String nom = MenuExamenS.nomBoite(niveau);
        inv.ajouterMateriau(nom, qte);
        return qte + "x " + nom;
    }

    private static String sceau(Inventaire inv, SceauDeRang s, int qte) {
        if (qte <= 0) return null;
        inv.ajouterMateriau(s.nom, qte);
        return qte + "x " + s.nom;
    }
}
