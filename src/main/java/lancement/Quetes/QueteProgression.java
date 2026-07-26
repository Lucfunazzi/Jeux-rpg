/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lancement.Quetes;

/**
 *
 * @author Lucas
 */


public class QueteProgression extends Quete {
    private final int chapitreReqis;
    private final int stageRequis;
    private final boolean estElite;
    private boolean acceptee = false;

    public QueteProgression(String id, String titre, String description,
                            int chapitreRequis, int stageRequis, boolean estElite,
                            int recompenseXP, int recompenseOr, int recompenseParcheminC) {
        super(id, titre, description, recompenseXP, recompenseOr, recompenseParcheminC);
        this.chapitreReqis = chapitreRequis;
        this.stageRequis   = stageRequis;
        this.estElite      = estElite;
    }

    public int getChapitreRequis() { return chapitreReqis; }
    public int getStageRequis()    { return stageRequis; }
    public boolean isElite()       { return estElite; }

    /** Vrai une fois que le joueur a accepte cette quete depuis le menu Quetes — c'est ce qui debloque le stage associe. */
    public boolean isAcceptee()        { return acceptee; }
    public void setAcceptee(boolean v) { acceptee = v; }

    @Override
    public String getEtat() {
        if (reclamee)  return "[FAIT]";
        if (completee) return "[OK]  ";
        if (acceptee)  return "[EN COURS]";
        return          "[A ACCEPTER]";
    }

    @Override
    public String getProgression() {
        if (completee) return "Termine";
        if (acceptee)  return "En cours";
        return "A accepter";
    }
}
