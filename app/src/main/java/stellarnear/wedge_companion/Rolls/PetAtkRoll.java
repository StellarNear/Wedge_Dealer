package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.Perso.PersoManager;

public class PetAtkRoll extends AtkRoll {

    public PetAtkRoll(Activity mA, Context mC, Integer base) {
        super(mA, mC, base);
    }

    public Integer getPreRandValue() {
        this.preRandValue = this.base + new CalculationAtk(mC).getBonusAtk() + getMeleeBonus();
        return preRandValue;
    }

    private Integer getMeleeBonus() {
        int bonusAtkMelee=0;
        bonusAtkMelee+= pj.getAbilityMod("ability_force");
        if (mode.contains("claw") && pj.getAllCapacities().capacityIsActive("capacity_magic_suprem_bite_claw")) {
            bonusAtkMelee+=5;
        }
        if (mode.contains("bite") && pj.getAllCapacities().capacityIsActive("capacity_magic_suprem_bite_bite")) {
            bonusAtkMelee+=5;
        }
        if(mode.contains("claw")&&pj.getAllFeats().featIsActive("feat_predil_claw")){
            bonusAtkMelee+=1;
        }
        if(mode.contains("claw")&&pj.getAllFeats().featIsActive("feat_predil_sup_claw")){
            bonusAtkMelee+=1;
        }
        if(mode.contains("claw")&&pj.getAllFeats().featIsActive("feat_predil_epic_claw")){
            bonusAtkMelee+=2;
        }
        if ( pj.getAllFeats().featIsActive("feat_power_atk")) {
            int defValID= mC.getResources().getIdentifier("feat_power_atk_val_def"+PersoManager.getPJSuffix(),"integer",mC.getPackageName());
            bonusAtkMelee-=tools.toInt(settings.getString("feat_power_atk_val", String.valueOf(mC.getResources().getInteger(defValID))));
        }
        if(mode.contains("leap")){
            bonusAtkMelee+=2;
        }
        return bonusAtkMelee;
    }
}
