package stellarnear.wedge_companion.Elems;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.R;

public class ElemsManager {
    private static ElemsManager instance;
    private Context mC;
    private List<Elem> listElems;

    public ElemsManager(Context mC) {
        listElems = new ArrayList<>();
        listElems.add(new Elem("", "physique", mC.getColor(R.color.phy), mC.getColor(R.color.recent_phy), R.drawable.phy_logo));
        listElems.add(new Elem("none", "aucun", mC.getColor(R.color.phy), mC.getColor(R.color.recent_phy), R.drawable.none_logo));
        listElems.add(new Elem("acid", "acide", mC.getColor(R.color.acide), mC.getColor(R.color.acide_dark), R.drawable.acid_logo));
        listElems.add(new Elem("fire", "feu", mC.getColor(R.color.feu), mC.getColor(R.color.feu_dark), R.drawable.fire_logo));
        listElems.add(new Elem("shock", "foudre", mC.getColor(R.color.foudre), mC.getColor(R.color.foudre_dark), R.drawable.shock_logo));
        listElems.add(new Elem("frost", "froid", mC.getColor(R.color.frost), mC.getColor(R.color.recent_frost), R.drawable.frost_logo));
        listElems.add(new Elem("sound", "son", mC.getColor(R.color.aucun), mC.getColor(R.color.aucun_dark)));
        listElems.add(new Elem("heal", "soin", mC.getColor(R.color.colorPrimaryhalda), mC.getColor(R.color.colorPrimaryDarkhalda), R.drawable.ic_heal_symbol_draw));
    }

    public static ElemsManager getInstance(Context context) {
        if (instance == null) {
            instance = new ElemsManager(context);
        }
        return instance;
    }

    public static ElemsManager getInstance() {
        return instance;
    }

    public List<String> getResistElems() {
        List<String> elems = Arrays.asList("acid", "fire", "shock", "frost", "sound");
        return elems;
    }

    public List<String> getListKeysWedgeDamage() {
        List<String> wedgeKeys = Arrays.asList("", "fire", "shock", "frost");
        return wedgeKeys;
    }

    public List<String> getListSpellsKeys() {
        List<String> elemsSpells = Arrays.asList("none", "acid", "fire", "shock", "frost");
        return elemsSpells;
    }

    public String getName(String elemKey) {
        String val = "";
        try {
            val = getElementByKey(elemKey).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public int getColorId(String elemKey) {
        return getElementByKey(elemKey).getColorId();
    }

    public int getColorIdDark(String elemKey) {
        return getElementByKey(elemKey).getColorIdDark();
    }

    private Elem getElementByKey(String elemKey) {
        Elem res = null;
        for (Elem elem : listElems) {
            if (elem.getKey().equalsIgnoreCase(elemKey)) {
                res = elem;
            }
        }
        return res;
    }

    public int getDrawableId(String key) {
        return getElementByKey(key).getDrawableId();
    }


}
