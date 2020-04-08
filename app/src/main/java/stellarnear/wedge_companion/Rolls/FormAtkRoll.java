package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.CalculationAtk;

public class FormAtkRoll extends AtkRoll {

    public FormAtkRoll(Activity mA, Context mC, Integer base) {
        super(mA, mC, base);
    }

    public Integer getPreRandValue() {
        this.preRandValue = this.base + new CalculationAtk(mC).getBonusAtk() + getMeleeBonus();
        return preRandValue;
    }

    private Integer getMeleeBonus() {
        int bonusAtkMelee = 0;
        bonusAtkMelee += pj.getAbilityMod("ability_force");
        return bonusAtkMelee;
    }
}
