package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class PetDmgRoll extends DmgRoll {

    public PetDmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit) {
        super(mA, mC, critConfirmed, naturalCrit);
    }

    public void setDmgRand() {
        setCritMultiplier();
        addDmgDices();
        this.bonusDmg = getBonusDmg();
        for (Dice dice : allDiceList.getList()) {
            dice.rand(manualDiceDmg);
        }
        if(pj.getAllCapacities().capacityIsActive("capacity_grip")) {
            tools.customToast(mC, "Tu peux faire une étreinte avec un test BMO","center");
        }
    }

    private void setCritMultiplier() {
        if (pj.getAllMythicFeats()!=null && pj.getAllMythicFeats().mythicFeatsIsActive("mythicfeat_crit_science")) {
            critMultiplier = 3;
        } else {
            critMultiplier = 2; //de base c'est *2
        }
    }

    private void addDmgDices() {
        if (mode.contains("claw")){
            if(pj.getAllFeats().featIsActive("feat_natural_weapon_sup_claw")){
                Dice dice = new Dice(mA, mC, 8);
                if (critConfirmed) {
                    dice.makeCritable();
                }
                allDiceList.add(dice);
            } else {
                Dice dice = new Dice(mA, mC, 6);
                if (critConfirmed) {
                    dice.makeCritable();
                }
                allDiceList.add(dice);
            }
        } else if(mode.contains("bite")){
            if(pj.getAllFeats().featIsActive("feat_natural_weapon_sup_bite")){
                Dice dice = new Dice(mA, mC, 6);
                Dice dice2 = new Dice(mA, mC, 6);
                if (critConfirmed) {
                    dice.makeCritable();
                    dice2.makeCritable();
                }
                allDiceList.add(dice);
                allDiceList.add(dice2);
            } else {
                Dice dice = new Dice(mA, mC, 8);
                if (critConfirmed) {
                    dice.makeCritable();
                }
                allDiceList.add(dice);
            }
        }
    }

    public int getBonusDmg() { //par defaut aucun bonus
        int calcBonusDmg = 0;
        calcBonusDmg+= pj.getAbilityMod("ability_force");
        if (mode.contains("claw") && pj.getAllCapacities().capacityIsActive("capacity_magic_suprem_bite_claw")) {
            calcBonusDmg+=5;
        }
        if (mode.contains("bite") && pj.getAllCapacities().capacityIsActive("capacity_magic_suprem_bite_bite")) {
            calcBonusDmg+=5;
        }
        if ( pj.getAllFeats().featIsActive("feat_power_atk")) {
            int defValID= mC.getResources().getIdentifier("feat_power_atk_val_def"+PersoManager.getPJSuffix(),"integer",mC.getPackageName());
            calcBonusDmg+=2*tools.toInt(settings.getString("feat_power_atk_val", String.valueOf(mC.getResources().getInteger(defValID))));
        }
        calcBonusDmg += tools.toInt(settings.getString("bonus_global_dmg_temp", String.valueOf(0)));
        calcBonusDmg += tools.toInt(settings.getString("bonus_dmg_temp"+PersoManager.getPJSuffix(), String.valueOf(0)));
        return calcBonusDmg;
    }

}
