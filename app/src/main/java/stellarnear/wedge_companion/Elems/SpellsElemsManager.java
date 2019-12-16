package stellarnear.wedge_companion.Elems;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.R;

public class SpellsElemsManager {
    private Context mC;
    private static SpellsElemsManager instance;

    private List<Elem> listElems;

    public SpellsElemsManager(Context mC){
        listElems=new ArrayList<>();
        listElems.add(new Elem("acid","acide",mC.getColor(R.color.acide),mC.getColor(R.color.acide_dark)));
        listElems.add(new Elem("fire","feu",mC.getColor(R.color.feu),mC.getColor(R.color.feu_dark)));
        listElems.add(new Elem("shock","foudre",mC.getColor(R.color.foudre),mC.getColor(R.color.foudre_dark)));
        listElems.add(new Elem("frost","froid",mC.getColor(R.color.frost),mC.getColor(R.color.recent_frost)));
        listElems.add(new Elem("sound","son",mC.getColor(R.color.aucun),mC.getColor(R.color.aucun_dark)));
    }

    public static SpellsElemsManager getInstance(Context context) {
        if (instance==null){instance=new SpellsElemsManager(context);}
        return instance;
    }

    public static SpellsElemsManager getInstance() {
        return instance;
    }

    public List<Elem> getElems(){
        return this.listElems;
    }

    public List<String> getListKeys() {
        List <String> list=new ArrayList<>();
        for(Elem elem: listElems){
            list.add(elem.getKey());
        }
        return list;
    }

    public String getName(String elemKey) {
        return getElementByKey(elemKey).getName();
    }

    public int getColorId(String elemKey) {
        return getElementByKey(elemKey).getColorId();
    }

    public int getColorIdDark(String elemKey){
        return getElementByKey(elemKey).getColorIdDark();
    }

    private Elem getElementByKey(String elemKey) {
        Elem res=null;
        for (Elem elem : listElems){
            if(elem.getKey().equalsIgnoreCase(elemKey)){
                res=elem;
            }
        }
        return res;
    }

    public int getDrawableId(String key) {
        return getElementByKey(key).getDrawableId();
    }
}
