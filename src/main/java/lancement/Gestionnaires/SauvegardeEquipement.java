package lancement.Gestionnaires;

import Equipement.Equipement;
import Equipement.Pierre;
import lancement.SauvegardeData;

/** Conversion Equipement <-> SauvegardeData.EquipementData, partagee entre plusieurs domaines de sauvegarde. */
public class SauvegardeEquipement {

    public static SauvegardeData.EquipementData versEquipementData(Equipement e, int quantite) {
        SauvegardeData.EquipementData ed = new SauvegardeData.EquipementData(
            e.getNom(), e.getSlot().name(), e.getRarete().name(), e.getTypeArme().name(),
            e.getBonusATKBase(), e.getBonusDEFBase(), e.getBonusPVBase(), e.getBonusVITBase(),
            quantite
        );
        ed.niveauFortification = e.getNiveauFortification();
        ed.niveauAffinage      = e.getNiveauAffinage();
        for (Pierre p : e.getPierres()) {
            ed.pierres.add(p != null ? new SauvegardeData.PierreData(p.getType().name(), p.getNiveau()) : null);
        }
        return ed;
    }

    public static Equipement versEquipement(SauvegardeData.EquipementData ed) {
        Equipement e = new Equipement(
            ed.nom,
            Equipement.Slot.valueOf(ed.slot),
            Equipement.Rarete.valueOf(ed.rarete),
            Equipement.TypeArme.valueOf(ed.typeArme),
            ed.bonusATK, ed.bonusDEF, ed.bonusPV, ed.bonusVIT
        );
        e.setNiveauFortification(ed.niveauFortification);
        e.setNiveauAffinage(ed.niveauAffinage);
        if (ed.pierres != null) {
            for (int i = 0; i < ed.pierres.size() && i < Equipement.NB_EMPLACEMENTS_PIERRES; i++) {
                SauvegardeData.PierreData pd = ed.pierres.get(i);
                if (pd != null) {
                    try {
                        e.insererPierre(i, new Pierre(Pierre.Type.valueOf(pd.type), pd.niveau));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
        return e;
    }
}
