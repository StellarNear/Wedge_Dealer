package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.R;

public class RangedRoll extends Roll {

    public RangedRoll(Activity mA, Context mC, Integer atkBase) {
        super(mA,mC,atkBase);
        this.atkRoll=new RangeAtkRoll(mA,mC,atkBase);
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()){
            int nthDmgRoll=1;
            this.dmgRollList.add(new RangeDmgRoll(mA,mC,atkRoll.isCritConfirmed(),atkRoll.getAtkDice().getRandValue()==20,this.nthAtkRoll,nthDmgRoll));
            if (pj.getAllFeats().featIsActive("feat_manyshot_suprem")) {
                int multiVal = tools.toInt(settings.getString("feat_manyshot_suprem_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_manyshot_suprem_val_def))));
                for(int i=1;i<multiVal;i++){
                    nthDmgRoll++;
                    this.dmgRollList.add(new RangeDmgRoll(mA,mC,false,false,this.nthAtkRoll,nthDmgRoll)); //seul l'attaque principale peut crit
                }
            }

            for(DmgRoll dmgRoll:this.dmgRollList){
                dmgRoll.setDmgRand();
            }
        }
    }

    public void lynxEyeBoost() {
            ((RangeAtkRoll) atkRoll).lynxEyeBoost();
    }

    public void rangeMalus(int malus) {
            ((RangeAtkRoll) atkRoll).rangeMalus(malus);
    }

}
