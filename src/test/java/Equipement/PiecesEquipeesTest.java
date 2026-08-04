package Equipement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verrouille PiecesEquipees (extrait de PersonnageBase) : sommes de bonus bruts, seuils de
 * bonus de set (4/5/6 pieces) et bonus special 6/6 reserve a SSS/UR.
 */
class PiecesEquipeesTest {

    private Equipement creerPiece(Equipement.Slot slot, Equipement.Rarete rarete,
                                   double atk, double def, double pv, double vit) {
        return new Equipement("Test " + slot, slot, rarete, Equipement.TypeArme.AUCUN, atk, def, pv, vit);
    }

    @Test
    void sommeLesBonusBrutsDesPiecesEquipees() {
        PiecesEquipees pieces = new PiecesEquipees();
        pieces.equiper(creerPiece(Equipement.Slot.ARME, Equipement.Rarete.C, 50, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.TORSE, Equipement.Rarete.C, 0, 30, 100, 0));

        assertEquals(50.0, pieces.bonusATK(), 0.001);
        assertEquals(30.0, pieces.bonusDEF(), 0.001);
        assertEquals(100.0, pieces.bonusPV(), 0.001);
        assertEquals(0.0, pieces.bonusVIT(), 0.001);
    }

    @Test
    void desequiperRetireLeBonusEtRenvoieLAncienneNiece() {
        PiecesEquipees pieces = new PiecesEquipees();
        Equipement arme = creerPiece(Equipement.Slot.ARME, Equipement.Rarete.C, 50, 0, 0, 0);
        pieces.equiper(arme);

        Equipement retiree = pieces.desequiper(Equipement.Slot.ARME);

        assertEquals(arme, retiree);
        assertEquals(0.0, pieces.bonusATK(), 0.001);
        assertNull(pieces.desequiper(Equipement.Slot.ARME));
    }

    @Test
    void compterPiecesParRarete() {
        PiecesEquipees pieces = new PiecesEquipees();
        pieces.equiper(creerPiece(Equipement.Slot.ARME, Equipement.Rarete.B, 0, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.TORSE, Equipement.Rarete.B, 0, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.MAINS, Equipement.Rarete.B, 0, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.BOTTES, Equipement.Rarete.C, 0, 0, 0, 0));

        assertEquals(3, pieces.compterPieces(Equipement.Rarete.B));
        assertEquals(1, pieces.compterPieces(Equipement.Rarete.C));
        assertEquals(0, pieces.compterPieces(Equipement.Rarete.A));
        assertEquals(Equipement.Rarete.B, pieces.rareteSetDominante());
    }

    @Test
    void bonusSetPVSeulementAPartirDe4PiecesDeMemeRarete() {
        PiecesEquipees pieces = new PiecesEquipees();
        pieces.equiper(creerPiece(Equipement.Slot.ARME, Equipement.Rarete.A, 0, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.TORSE, Equipement.Rarete.A, 0, 0, 0, 0));
        pieces.equiper(creerPiece(Equipement.Slot.MAINS, Equipement.Rarete.A, 0, 0, 0, 0));
        assertEquals(0.0, pieces.bonusSetPV(), 0.001);

        pieces.equiper(creerPiece(Equipement.Slot.BOTTES, Equipement.Rarete.A, 0, 0, 0, 0));
        assertEquals(BonusSet.palier(Equipement.Rarete.A).bonusPV(), pieces.bonusSetPV(), 0.001);
    }

    @Test
    void bonusSetVitesseEtAttaqueAuxSeuils5Et6Pieces() {
        PiecesEquipees pieces = new PiecesEquipees();
        for (Equipement.Slot slot : new Equipement.Slot[]{
                Equipement.Slot.ARME, Equipement.Slot.TORSE, Equipement.Slot.MAINS,
                Equipement.Slot.JAMBIERES, Equipement.Slot.BOTTES}) {
            pieces.equiper(creerPiece(slot, Equipement.Rarete.S, 0, 0, 0, 0));
        }

        assertEquals(BonusSet.palier(Equipement.Rarete.S).bonusVitessePct(), pieces.bonusSetVitessePct(), 0.0001);
        assertEquals(0.0, pieces.bonusSetAttaquePct(), 0.0001);

        pieces.equiper(creerPiece(Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.S, 0, 0, 0, 0));
        assertEquals(BonusSet.palier(Equipement.Rarete.S).bonusAttaquePct(), pieces.bonusSetAttaquePct(), 0.0001);
    }

    @Test
    void bonusSpecial6sur6ReserveAuxRaretesSSSEtUR() {
        PiecesEquipees piecesA = new PiecesEquipees();
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            piecesA.equiper(creerPiece(slot, Equipement.Rarete.A, 0, 0, 0, 0));
        }
        assertNull(piecesA.bonusSetSpecialActif());

        PiecesEquipees piecesSSS = new PiecesEquipees();
        for (Equipement.Slot slot : Equipement.Slot.values()) {
            piecesSSS.equiper(creerPiece(slot, Equipement.Rarete.SSS, 0, 0, 0, 0));
        }
        assertNotNull(piecesSSS.bonusSetSpecialActif());
        assertEquals(BonusSet.special(Equipement.Rarete.SSS), piecesSSS.bonusSetSpecialActif());
    }

    @Test
    void bonusPierreSommeLesPierresDeToutesLesPiecesEquipees() {
        // Une piece n'accepte qu'une pierre d'un type donne : on verifie donc la somme
        // a travers deux pieces differentes, pas deux pierres du meme type sur une seule piece.
        PiecesEquipees pieces = new PiecesEquipees();
        Equipement arme = creerPiece(Equipement.Slot.ARME, Equipement.Rarete.C, 0, 0, 0, 0);
        arme.insererPierre(0, new Pierre(Pierre.Type.FORCE, 1));  // +1.5%
        Equipement torse = creerPiece(Equipement.Slot.TORSE, Equipement.Rarete.C, 0, 0, 0, 0);
        torse.insererPierre(0, new Pierre(Pierre.Type.FORCE, 1)); // +1.5%
        pieces.equiper(arme);
        pieces.equiper(torse);

        assertEquals(3.0, pieces.bonusPierrePoints(Pierre.Type.FORCE), 0.001);
        assertEquals(0.03, pieces.bonusPierreFraction(Pierre.Type.FORCE), 0.0001);
        assertEquals(0.0, pieces.bonusPierrePoints(Pierre.Type.AGILITE), 0.001);
    }
}
