package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class RangeAtkRoll extends AtkRoll {
    private boolean lynxBoosted=false;
    private int rangeMalus;
    public RangeAtkRoll(Activity mA, Context mC, Integer base) {
        super( mA, mC, base);
    }

    private int getBonusRangeAtk() {
       int bonusAtkRange=0;
        bonusAtkRange+= pj.getAbilityMod("ability_dexterite");

        if (  settings.getBoolean("thor_switch", mC.getResources().getBoolean(R.bool.thor_switch_def))) {
            bonusAtkRange+= 3;
        }
        if ( pj.getAllFeats().featIsActive("feat_predil")) {
            bonusAtkRange+= 1;
        }
        if ( pj.getAllFeats().featIsActive("feat_predil_sup")) {
            bonusAtkRange+= 1;
        }
        if ( pj.getAllFeats().featIsActive("feat_predil_epic")) {
            bonusAtkRange+= 2;
        }
        if ( pj.getAllFeats().featIsActive("feat_nine_m")) {
            bonusAtkRange+= 1;
        }
        if (settings.getBoolean("magic_arrow_switch", mC.getResources().getBoolean(R.bool.magic_arrow_switch_def))) {
            bonusAtkRange+= tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }
        if ( pj.getAllFeats().featIsActive("feat_aim")) {
            bonusAtkRange-=tools.toInt(settings.getString("feat_aim_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_aim_val_def))));
        }
        if (this.mode.equalsIgnoreCase("fullround") && pj.getAllFeats().featIsActive("feat_rapid_fire")) {
            bonusAtkRange-=2;
        }
        if(this.mode.equalsIgnoreCase("barrage_shot")){
            bonusAtkRange+=tools.toInt(settings.getString("mythic_tier", String.valueOf(mC.getResources().getInteger(R.integer.mythic_tier_def))));
        }
        if(lynxBoosted){
            bonusAtkRange+=pj.getAllCapacities().getCapacity("capacity_lynx_eye").getValue();
        }
        if(rangeMalus>0){
            bonusAtkRange-=rangeMalus;
        }
        return bonusAtkRange;
    }

    //getters

    public Integer getPreRandValue() {
        this.preRandValue = this.base + new CalculationAtk(mC).getBonusAtk() + getBonusRangeAtk();
        return preRandValue;
    }

    public void setAtkRand() {
        atkDice.rand(manualDice);
        if (manualDice) {
            atkDice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    calculAtk();
                    setCritAndFail();
                    if(mListener!=null){mListener.onEvent();}
                }
            });
        } else {
            calculAtk();
            setCritAndFail();
        }
    }

    private void setCritAndFail() {
        if (atkDice.getRandValue() == 1 && !pj.getAllMythicCapacities().mythicCapacityIsActive("mythiccapacity_still_a_chance")) { //si c'est un 1 et qu'on a pas le dons antifail
            this.fail = true;
            atkDice.getImg().setOnClickListener(null);
        }
        int critMin;
        if (pj.getAllFeats().featIsActive("feat_improved_crit")) {
            critMin = 18; // vient de oeil excercé arché sylvestre sui double 19-20 mais porte à 18 le cir tsi il connait l'ennemie
        } else {
            critMin = 20;
        }
        if (atkDice.getRandValue() >= critMin) { //c'est possiblement un crit
            this.crit = true;
        }
    }

    public void lynxEyeBoost() {
        lynxBoosted=true;
    }

    public void rangeMalus(int malus) {
        rangeMalus=malus;
    }
}
