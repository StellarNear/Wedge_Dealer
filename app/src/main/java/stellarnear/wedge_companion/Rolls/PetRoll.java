package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

public class PetRoll extends Roll {

    public PetRoll(Activity mA, Context mC, Integer atkBase) {
        super(mA, mC, atkBase);
        this.atkRoll = new PetAtkRoll(mA, mC, atkBase);
        //
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()) {
            this.dmgRollList.add(new PetDmgRoll(mA, mC, atkRoll.isCritConfirmed(), atkRoll.getAtkDice().getRandValue() == 20));
            for (DmgRoll dmgRoll : this.dmgRollList) {
                dmgRoll.setMode(mode);
                dmgRoll.setDmgRand();
            }
        }
    }

    public void lynxEyeBoost() {  //rien à faire en cac
    }

    public void rangeMalus(int malus) { //rien à faire en cac
    }
}
