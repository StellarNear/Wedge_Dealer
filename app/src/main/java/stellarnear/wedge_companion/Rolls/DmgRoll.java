package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class DmgRoll {
    protected Activity mA;
    protected Context mC;
    protected SharedPreferences settings;
    protected Boolean critConfirmed;
    protected Boolean naturalCrit;
    protected Integer critMultiplier;
    protected boolean manualDiceDmg;
    protected String mode;

    protected int bonusDmg = 0;

    protected DiceList allDiceList = new DiceList();

    protected Tools tools = new Tools();
    protected Perso pj = PersoManager.getCurrentPJ();

    public DmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit) {
        this.mA=mA;
        this.mC = mC;
        this.critConfirmed = critConfirmed;
        this.naturalCrit=naturalCrit;

        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.manualDiceDmg = settings.getBoolean("switch_manual_diceroll_damage", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));
    }

    public void setMode(String mode) {
        this.mode=mode;
    }

    public void setDmgRand() {
        setCritMultiplier();
        addDmgDices();
        this.bonusDmg = getBonusDmg();
        for (Dice dice : allDiceList.getList()) {
            dice.rand(manualDiceDmg);
        }
    }

    private void setCritMultiplier() {
        if (pj.getAllMythicFeats().mythicFeatsIsActive("mythicfeat_crit_science")) {
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

    // Getters

    public DiceList getDmgDiceList() {
        return allDiceList;
    }

    public int getSumDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int sumDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                sumDmg += dice.getRandValue() * critMultiplier;
            } else {
                sumDmg += dice.getRandValue();
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            sumDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            sumDmg += bonusDmg;
        }
        return sumDmg;
    }

    public int getMaxDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int maxDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                maxDmg += dice.getnFace() * critMultiplier;
            } else {
                maxDmg += dice.getnFace();
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            maxDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            maxDmg += bonusDmg;
        }
        return maxDmg;
    }

    public int getMinDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        int minDmg = 0;
        for (Dice dice : allDiceList.filterWithElement(element).getList()) {
            if (dice.canCrit() && critConfirmed) {
                minDmg += 1 * critMultiplier;
            } else {
                minDmg += 1;
            }
        }

        if (element.equalsIgnoreCase("") && critConfirmed) {
            minDmg += bonusDmg * critMultiplier;
        } else if (element.equalsIgnoreCase("")) {
            minDmg += bonusDmg;
        }
        return minDmg;
    }

    public Integer getCritMultiplier() {
        return critMultiplier;
    }


}
