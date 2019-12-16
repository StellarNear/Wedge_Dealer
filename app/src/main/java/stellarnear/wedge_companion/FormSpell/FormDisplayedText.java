package stellarnear.wedge_companion.FormSpell;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Spells.CalculationSpell;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Tools;

public class FormDisplayedText {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();
    private FormCalculationSpell calculationSpell =new FormCalculationSpell();

    public FormDisplayedText(){  }

    public String damageTxt(FormPower spell) {


        String dmg="";
        if(calculationSpell.nDice(spell)>0){dmg+=calculationSpell.nDice(spell)+"d"+ calculationSpell.diceType(spell);}

        if(spell.getFlat_dmg()>0){
            if(!dmg.equalsIgnoreCase("")){dmg+="+";}
            dmg+=String.valueOf(spell.getFlat_dmg());
        }

        if(spell.getDice_type().contains("lvl")){
            Integer dmg_int = calculationSpell.nDice(spell);
            dmg = String.valueOf(dmg_int);
            if(spell.getFlat_dmg()>0){dmg_int+=spell.getFlat_dmg();}
        }
        return dmg;
    }

    public String rangeTxt(FormPower spell) {
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



}
