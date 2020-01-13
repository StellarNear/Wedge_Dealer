package stellarnear.wedge_companion.Spells;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class DisplayedText {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();
    private CalculationSpell calculationSpell=new CalculationSpell();

    public DisplayedText(){ }

    public String damageTxt(Spell spell) {
        String dmg= calculationSpell.nDice(spell)+"d"+ calculationSpell.diceType(spell);
        if(calculationSpell.getFlatDmg(spell)>0){dmg+="+"+calculationSpell.getFlatDmg(spell);}
        if(spell.getDice_type().contains("lvl")){
            Integer dmg_int = calculationSpell.nDice(spell);
            if(calculationSpell.getFlatDmg(spell)>0){dmg_int+=calculationSpell.getFlatDmg(spell);}
            dmg = String.valueOf(dmg_int);
        } else  if(spell.getMetaList().metaIdIsActive("meta_max")) {
            Integer dmg_int = calculationSpell.nDice(spell) * calculationSpell.diceType(spell);
            if(calculationSpell.getFlatDmg(spell)>0){dmg_int+=calculationSpell.getFlatDmg(spell);}
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

    public String compoTxt(Spell spell) {
        List<String> valList=new ArrayList<>(spell.getCompoList());
        if(valList.contains("M") && pj.getAllFeats().featIsActive("feat_materiel"))  {
            valList.remove("M");
        }
        if(valList.contains("M+") && pj.getAllFeats().featIsActive("feat_materiel_epic"))  {
            valList.remove("M+");
        }
        if(valList.contains("V")&&spell.getMetaList().metaIdIsActive("meta_silent")){
            valList.remove("V");
        }
        if(valList.contains("G")&&spell.getMetaList().metaIdIsActive("meta_static")){
            valList.remove("G");
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
            Double result= tools.toDouble(dura.replaceAll("[^.^0-9?!]",""));
            String duration_unit = dura.replaceAll("[.0-9?!]","");
            if(dura.contains("/lvl")){
                Integer lvl =  calculationSpell.casterLevel();
                result = result * lvl;
                duration_unit = dura.replaceAll("/lvl","").replaceAll("[.0-9?!]","");
            }
            dura = result.intValue()+duration_unit;
            if(result>1){dura+="s";}
        }
        return dura;
    }


    public String areaTxt(Spell spell) {
        String valBase=spell.getArea();
        String valResult=valBase;
        String unit="";
        if(valBase.contains("/lvl")){
            Double result= tools.toDouble(valBase.replaceAll("[^.^0-9?!]",""));
            Integer lvl =  calculationSpell.casterLevel();
            result = result * lvl;
            unit = valBase.replaceAll("/lvl","").replaceAll("[.0-9?!]","");
            valResult = result.intValue()+unit;
            if(result>1){valResult+="s";}
        }

        return valResult;
    }
}
