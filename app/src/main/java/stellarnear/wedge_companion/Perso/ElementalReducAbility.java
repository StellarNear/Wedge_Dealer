package stellarnear.wedge_companion.Perso;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Elems.Elem;
import stellarnear.wedge_companion.Elems.ElemsManager;


/**
 * Created by jchatron on 04/01/2018.
 */

public class ElementalReducAbility extends Ability{
    private Map<String,Integer> mapElemReduc=new HashMap<>();
    private ElemsManager elems;
    public ElementalReducAbility(String name, String shortname, String type, String descr, Boolean testable, Boolean focusable, String id, Context mC) {
        super(name, shortname,  type,  descr,  testable,  focusable,  id, mC);
        elems=new ElemsManager(mC);
    }

    public void resetModifs(){
        for(String elemId : elems.getResistElems()){
            mapElemReduc.put(elemId,value);  //on attribue la valeur à tout les elements
        }

    }

    public void setValue(int value) {
        this.value=value; //on store la valeur de base
        for(String elemId : elems.getResistElems()){
            mapElemReduc.put(elemId,value);  //on attribue la valeur à tout les elements
        }
    }

    public void modifResistance(String elemId,int modif){
        try {
            mapElemReduc.put(elemId,mapElemReduc.get(elemId)+modif);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getValue() {
        int max=0;
        for(Map.Entry<String,Integer> entry:mapElemReduc.entrySet()){
            if (entry.getValue()>max){
                max=entry.getValue();
            }
        }
        return max;
    }

    public Map<String, Integer> getMapElemReduc() {
        return mapElemReduc;
    }
}

