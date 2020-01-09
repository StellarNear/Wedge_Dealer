package stellarnear.wedge_companion.FormSpell;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class FormCalculationSpell {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();

    public FormCalculationSpell(){  }

    public int saveVal(FormPower spell){
        int val=10;
        val+= pj.getAbilityMod("ability_charisme");
        return val;
    }

    public int nDice(FormPower spell){
        int val=0;
        if(spell.getN_dice_per_lvl()==0.0){
            val = spell.getCap_dice();
        }else {
                val = (int) (getlevel() * spell.getN_dice_per_lvl());
        }
        return val;
    }

    public int diceType(FormPower spell){
        return tools.toInt(spell.getDice_type());
    }

    public int getlevel(){
        return pj.getCasterLevel();
    }

    public Double range(FormPower spell) {
        String range=spell.getRange();
        List<String> rangesLvl = Arrays.asList("contact", "courte", "moyenne", "longue" );

        Double distDouble =-1.0;
        int indexRange = rangesLvl.indexOf(range);
        if (indexRange>=0) {
            Integer lvl = getlevel();
            if(indexRange>=rangesLvl.size()){indexRange=rangesLvl.size()-1;}
            switch(rangesLvl.get(indexRange)) {
                case ("contact"):
                    distDouble=0.0;
                    break;
                case ("courte"):
                    distDouble=7.5+1.5*(lvl/2.0);
                    break;
                case ("moyenne"):
                    distDouble=30.0+3.0*lvl;
                    break;
                case ("longue"):
                    distDouble=120.0+lvl*12.0;
                    break;
            }
        }
        return distDouble;
    }


}
