package lancement.Gestionnaires;

import Equipement.CarteOr;
import Equipement.Equipement;
import Equipement.Inventaire;
import Equipement.Materiau;
import Equipement.ParcheminXP;
import Equipement.Pierre;
import lancement.GameContext;
import lancement.SauvegardeData;

/** Sauvegarde/restauration de l'inventaire (equipements, materiaux, parchemins XP, cartes d'or, pierres). */
public class SauvegardeInventaire {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        for (Inventaire.StackEquipement s : ctx.inventaire.getStacks())
            data.inventaireEquipements.add(SauvegardeEquipement.versEquipementData(s.getEquipement(), s.getQuantite()));

        for (Materiau m : ctx.inventaire.getMateriaux())
            data.materiaux.add(new SauvegardeData.MateriauData(m.getNom(), m.getQuantite()));

        for (Inventaire.StackParchemin s : ctx.inventaire.getParchemins())
            data.inventaireParcheminsXP.add(
                new SauvegardeData.ParcheminXPData(s.getRarete().name(), s.getQuantite()));

        for (Inventaire.StackCarteOr s : ctx.inventaire.getCartesOr())
            data.inventaireCartesOr.add(
                new SauvegardeData.CarteOrData(s.getCarte().name(), s.getQuantite()));

        for (Inventaire.StackPierre s : ctx.inventaire.getPierres())
            data.inventairePierres.add(
                new SauvegardeData.PierreStackData(s.getType().name(), s.getNiveau(), s.getQuantite()));
    }

    public static void restaurer(Inventaire inventaire, SauvegardeData data) {
        if (data.inventaireEquipements != null)
            for (SauvegardeData.EquipementData ed : data.inventaireEquipements) {
                Equipement e = SauvegardeEquipement.versEquipement(ed);
                int q = ed.quantite > 0 ? ed.quantite : 1;
                for (int i = 0; i < q; i++) inventaire.ajouterEquipement(e);
            }
        if (data.materiaux != null)
            for (SauvegardeData.MateriauData md : data.materiaux)
                inventaire.ajouterMateriau(md.nom, md.quantite);

        if (data.inventaireParcheminsXP != null)
            for (SauvegardeData.ParcheminXPData pd : data.inventaireParcheminsXP)
                inventaire.ajouterParcheminXP(
                    ParcheminXP.Rarete.valueOf(pd.rarete), pd.quantite);

        if (data.inventaireCartesOr != null)
            for (SauvegardeData.CarteOrData cd : data.inventaireCartesOr) {
                try {
                    CarteOr niveau = CarteOr.valueOf(cd.niveau);
                    inventaire.ajouterCartesOr(niveau, cd.quantite);
                } catch (IllegalArgumentException ignored) {}
            }

        if (data.inventairePierres != null)
            for (SauvegardeData.PierreStackData pd : data.inventairePierres) {
                try {
                    inventaire.ajouterPierre(Pierre.Type.valueOf(pd.type), pd.niveau, pd.quantite);
                } catch (IllegalArgumentException ignored) {}
            }
    }
}
