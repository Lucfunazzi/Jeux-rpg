package Equipement;

import java.util.LinkedHashMap;
import java.util.Map;

/** Catalogue des recettes disponibles à la forge. */
public final class ForgeFactory {
    private ForgeFactory() {}

    public static ForgeRecette batonC() {
        Map<String, Integer> cout = new LinkedHashMap<>();
        cout.put("Parchemin d'arme C", 1);
        cout.put("Poussière", 5);
        cout.put("Gemme lumineuse", 1);
        return new ForgeRecette("Bâton de bois — Rang C", EquipementFactory.batonC(), cout);
    }
}
