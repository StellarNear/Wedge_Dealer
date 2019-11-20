package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.CalculationSpell;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class DisplayedText {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();
    private CalculationSpell calculationSpell =new CalculationSpell();

    public DisplayedText(){  }

    public String damageTxt(Spell spell) {

        String dmg= calculationSpell.nDice(spell)+"d"+ calculationSpell.diceType(spell);
        if(spell.getFlat_dmg()>0){dmg+="+"+spell.getFlat_dmg();}

        if(spell.getDice_type().contains("lvl")){
            Integer dmg_int = calculationSpell.nDice(spell);
            dmg = String.valueOf(dmg_int);
            if(spell.getFlat_dmg()>0){dmg_int+=spell.getFlat_dmg();}
        } else  if(spell.getMetaList().metaIdIsActive("meta_perfect")){
            Integer dmg_int = calculationSpell.nDice(spell) * calculationSpell.diceType(spell) *2;
            if(spell.getFlat_dmg()>0){dmg_int+=spell.getFlat_dmg();}
            dmg = String.valueOf(dmg_int);
        }else  if(spell.getMetaList().metaIdIsActive("meta_max")) {
            Integer dmg_int = calculationSpell.nDice(spell) * calculationSpell.diceType(spell);
            if(spell.getFlat_dmg()>0){dmg_int+=spell.getFlat_dmg();}
            dmg = String.valueOf(dmg_int);
        }
        return dmg;
    }

    public String rangeTxt(Spell spell) {
        Double rangeDouble= calculationSpell.range(spell);
        String range ="";
        if(rangeDouble==0.0){
            range="contact";
        } else if (rangeDouble<0.0){ //si on a pas pu calculer c'est un truc fixe
            range = spell.getRange();
        } else {
            range=String.valueOf(rangeDouble)+"m";
        }
        return range;
    }

    public String compoTxt(Context mC,Spell spell) {
        List<String> valList=new ArrayList<>(spell.getCompoList());
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if(valList.contains("M") && settings.getBoolean("materiel_switch",mC.getResources().getBoolean(R.bool.materiel_switch_def)))  {
            valList.remove("M");
        }
        if(valList.contains("M+") && settings.getBoolean("materiel_epic_switch",mC.getResources().getBoolean(R.bool.materiel_epic_switch_def)))  {
            valList.remove("M+");
        }
        if(valList.contains("V")&&spell.getMetaList().metaIdIsActive("meta_silent")){
            valList.remove("V");
        }
        String txt ="";
        for (String str : valList){
            txt+=str+", ";
        }

        if(txt.length()>2) {
            txt = txt.substring(0, txt.length() - 2);
        }
        return txt;
    }

    public String durationTxt(Spell spell) {
        String dura=spell.getDuration();

        if(!dura.equalsIgnoreCase("permanente")){
            Integer result= tools.toInt(dura.replaceAll("[^0-9?!]",""));
            String duration_unit = dura.replaceAll("[0-9?!]","");
            if(dura.contains("/lvl")){
                Integer lvl =  calculationSpell.casterLevel(spell);
                result = result * lvl;
                duration_unit = dura.replaceAll("/lvl","").replaceAll("[0-9?!]","");
            }
            if(spell.getMetaList().metaIdIsActive("meta_duration")){ result=result*2; }
            dura = result+duration_unit;
        }
        return dura;
    }


}
