package stellarnear.wedge_companion.FormSpell;

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
        return pj.getAbilityScore("ability_lvl");
    }

    public String range(FormPower spell) {
        return  spell.getRange();
    }


}
