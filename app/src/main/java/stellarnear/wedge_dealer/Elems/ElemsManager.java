package stellarnear.wedge_dealer.Elems;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_dealer.R;

public class ElemsManager {
    private Context mC;
    private static ElemsManager instance;

    private List<Elem> listElems;

    public ElemsManager(Context mC){
        listElems=new ArrayList<>();
        listElems.add(new Elem("","physique",mC.getColor(R.color.phy)));
        listElems.add(new Elem("fire","feu",mC.getColor(R.color.fire)));
        listElems.add(new Elem("shock","foudre",mC.getColor(R.color.shock)));
        listElems.add(new Elem("frost","froid",mC.getColor(R.color.frost)));
    }

    public static ElemsManager getInstance(Context context) {
        if (instance==null){instance=new ElemsManager(context);}
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

    private Elem getElementByKey(String elemKey) {
        Elem res=null;
        for (Elem elem : listElems){
            if(elem.getKey().equalsIgnoreCase(elemKey)){
                res=elem;
            }
        }
        return res;
    }
}
