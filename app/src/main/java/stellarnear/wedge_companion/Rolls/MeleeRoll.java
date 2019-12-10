package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class MeleeRoll extends Roll{

    public MeleeRoll(Activity mA, Context mC, Integer atkBase) {
       super(mA, mC, atkBase);
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()){
            this.dmgRollList.add(new MeleeDmgRoll(mA,mC,atkRoll.isCritConfirmed(),atkRoll.getAtkDice().getRandValue()==20));
            for(DmgRoll dmgRoll:this.dmgRollList){
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
