package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Elems.Elem;
import stellarnear.wedge_companion.Elems.SpellsElemsManager;


/**
 * Created by jchatron on 04/01/2018.
 */

public class ElementalReducAbility extends Ability{
    private Map<String,Integer> mapElemReduc=new HashMap<>();
    private SpellsElemsManager elems;
    public ElementalReducAbility(String name, String shortname, String type, String descr, Boolean testable, Boolean focusable, String id, Context mC) {
        super(name, shortname,  type,  descr,  testable,  focusable,  id, mC);
        elems=new SpellsElemsManager(mC);
    }

    public void resetModifs(){
        for(Elem elem : elems.getElems()){
            mapElemReduc.put(elem.getKey(),value);  //on attribue la valeur à tout les elements
        }

    }

    public void setValue(int value) {
        this.value=value; //on store la valeur de base
        for(Elem elem : elems.getElems()){
            mapElemReduc.put(elem.getKey(),value);  //on attribue la valeur à tout les elements
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

