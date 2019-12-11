package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Attack.Attack;
import stellarnear.wedge_companion.Elems.Elem;
import stellarnear.wedge_companion.Elems.SpellsElemsManager;

/**
 * Created by jchatron on 04/01/2018.
 */

public class Form {
    private String name;
    private String type;
    private String size;
    private String descr;
    private String id;
    private List<Attack> allAtks=new ArrayList<>();
    private List<FormCapacity> listPassiveCapacities= new ArrayList<>();
    private List<FormCapacity> listActivesCapacities= new ArrayList<>();
    private Map<String,Integer> mapElementIDResist =new HashMap<>();
    private String vulnerability="";
    private SpellsElemsManager elementManager;

    private Context mC;

    public Form(String name, String type,String size, String descr,String vulnerability,String resistance, String id, Context mC){
        this.name=name;
        this.type=type;
        this.size=size;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
        this.elementManager=SpellsElemsManager.getInstance(mC);

        if(!resistance.equalsIgnoreCase("")){
            for(String elemKey:resistance.split(",")){
                mapElementIDResist.put(elemKey,20);
            }
        }
        this.vulnerability=vulnerability;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTypeTxt() {
        String typeTxt="";
        switch (type){
            case "animal":
                typeTxt="Animale";
                break;
            case "magic":
                typeTxt="Magique";
                break;
            case "vegetal":
                typeTxt="Végétale";
                break;
            case "elemental_air":
            case "elemental_water":
            case "elemental_fire":
            case "elemental_earth":
                typeTxt="Elémentaire";
                break;
        }
        return typeTxt;
    }

    public String getDescr() {
        return descr;
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public void setListActivesCapacities(List<FormCapacity> listActivesCapacities) {
        this.listActivesCapacities = listActivesCapacities;
    }

    public void setListPassiveCapacities(List<FormCapacity> listPassiveCapacities) {
        this.listPassiveCapacities = listPassiveCapacities;
    }

    public List<FormCapacity> getListActivesCapacities() {
        return listActivesCapacities;
    }

    public List<FormCapacity> getListPassiveCapacities() {
        return listPassiveCapacities;
    }

    public String getVulnerability() {
        String res="";
        try {
            res=elementManager.getName(vulnerability);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Map<String, Integer> getMapElementIDResist() {
        return mapElementIDResist;
    }

    public int getElementResist(String elemKey) {
        int val=0;
        try {
            val=mapElementIDResist.get(elemKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public int getMaxElementResist() {
        int val =0;
        for(Elem elem : elementManager.getElems()){
            try {
                int elemval = mapElementIDResist.get(elem.getKey());
                if(elemval>val){val=elemval;}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return val;
    }

    public Spanned getResistsValueLongFormat() {
        Spanned result= new SpannableString("");
        for(Elem elem : SpellsElemsManager.getInstance(mC).getElems()){

            int valueFromForm = getElementResist(elem.getKey());
            if(valueFromForm>0) {
                if(result.length()>0){result=(Spanned) TextUtils.concat(result,"/");}
                Spannable span = new SpannableString(String.valueOf(valueFromForm)+" "+elem.getName());
                span.setSpan(new ForegroundColorSpan(elem.getColorIdDark()), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                result = (Spanned) TextUtils.concat(result, span);
            }
        }
        return result;
    }

    public String getAtkTxt() {
        String val="";
        for(Attack attack : allAtks){
            if(!val.equalsIgnoreCase("")){val+=", ";}
            val+=attack.getName()+" "+attack.getDmgTxt();
        }
        return val;
    }

    public void setListAttacks(List<Attack> allAtks) {
        this.allAtks=allAtks;
    }

    public List<Attack> getAllAtks() {
        return allAtks;
    }

    public boolean hasCapacity(String capaId) {
        boolean val=false;
        for(FormCapacity passive:listPassiveCapacities){
         if(passive.getId().equalsIgnoreCase(capaId))   {
             val=true;
         }
        }
        for(FormCapacity active:listActivesCapacities){
            if(active.getId().equalsIgnoreCase(capaId))   {
                val=true;
            }
        }
        return val;
    }
}

