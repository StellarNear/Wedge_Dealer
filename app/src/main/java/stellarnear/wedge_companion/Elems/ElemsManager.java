package stellarnear.wedge_companion.Elems;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.R;

public class ElemsManager {
    private Context mC;
    private static ElemsManager instance;

    private List<Elem> listElems;

    public ElemsManager(Context mC){
        listElems=new ArrayList<>();
        listElems.add(new Elem("","physique",mC.getColor(R.color.phy),mC.getColor(R.color.recent_phy),R.drawable.phy_logo));
        listElems.add(new Elem("fire","feu",mC.getColor(R.color.fire),mC.getColor(R.color.recent_fire),R.drawable.fire_logo));
        listElems.add(new Elem("shock","foudre",mC.getColor(R.color.shock),mC.getColor(R.color.recent_shock),R.drawable.shock_logo));
        listElems.add(new Elem("frost","froid",mC.getColor(R.color.frost),mC.getColor(R.color.recent_frost),R.drawable.frost_logo));
    }

    public static ElemsManager getInstance(Context context) {
        if (instance==null){instance=new ElemsManager(context);}
        return instance;
    }

    public static ElemsManager getInstance() {
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

    public int getColorIdRecent(String elemKey){
        return getElementByKey(elemKey).getColorIdRecent();
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
