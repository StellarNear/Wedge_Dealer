package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.Attack.Attack;

public class FormRoll extends Roll {
    private Attack attack;

    public FormRoll(Activity mA, Context mC, Integer atkBase, Attack atk) {
        super(mA, mC, atkBase);
        this.atkRoll = new FormAtkRoll(mA, mC, atkBase);
        this.attack = atk;
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()) {
            this.dmgRollList.add(new FormDmgRoll(mA, mC, attack, atkRoll.isCritConfirmed(), atkRoll.getAtkDice().getRandValue() == 20));
            for (DmgRoll dmgRoll : this.dmgRollList) {
                dmgRoll.setDmgRand();
            }
        }
    }

    public void lynxEyeBoost() {  //rien à faire en cac
    }

    public void rangeMalus(int malus) { //rien à faire en cac
    }
}
