package stellarnear.wedge_dealer.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Rolls.Dices.Dice;
import stellarnear.wedge_dealer.Tools;

public class DmgRoll {

    private Context mC;
    private SharedPreferences settings;
    private Boolean critConfirmed;
    private Integer critMultiplier;

    private int bonusDmg = 0;

    private DiceList allDiceList = new DiceList();

    private Tools tools = new Tools();

    public DmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit) {
        this.mC = mC;
        this.critConfirmed = critConfirmed;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("crit_science_myth_switch", mC.getResources().getBoolean(R.bool.crit_science_myth_switch_def))) {
            critMultiplier = 4;
        } else {
            critMultiplier = 3;
        }
        if (naturalCrit && settings.getBoolean("isillirit_switch", mC.getResources().getBoolean(R.bool.isillirit_switch_def))) {
            critMultiplier += 1;
        }

        Dice dice = new Dice(mA, mC, 8);
        if (critConfirmed) {
            dice.makeCritable();
        }
        allDiceList.add(dice);

        if (settings.getBoolean("feu_intense_switch", mC.getResources().getBoolean(R.bool.feu_intense_switch_def))) {
            allDiceList.add(new Dice(mA, mC, 6, "fire"));
            allDiceList.add(new Dice(mA, mC, 6, "fire"));
            if (critConfirmed) {
                switch (critMultiplier) {
                    case 5:
                        allDiceList.add(new Dice(mA, mC, 10, "fire"));
                    case 4:
                        allDiceList.add(new Dice(mA, mC, 10, "fire"));
                    case 3:
                        allDiceList.add(new Dice(mA, mC, 10, "fire"));
                        allDiceList.add(new Dice(mA, mC, 10, "fire"));
                }
            }
        }
        if (settings.getBoolean("foudre_intense_switch", mC.getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
            allDiceList.add(new Dice(mA, mC, 6, "shock"));
            allDiceList.add(new Dice(mA, mC, 6, "shock"));
            if (critConfirmed) {
                switch (critMultiplier) {
                    case 5:
                        allDiceList.add(new Dice(mA, mC, 10, "shock"));
                    case 4:
                        allDiceList.add(new Dice(mA, mC, 10, "shock"));
                    case 3:
                        allDiceList.add(new Dice(mA, mC, 10, "shock"));
                        allDiceList.add(new Dice(mA, mC, 10, "shock"));
                }
            }
        }
        if (settings.getBoolean("froid_intense_switch", mC.getResources().getBoolean(R.bool.froid_intense_switch_def))) {
            allDiceList.add(new Dice(mA, mC, 6, "frost"));
            allDiceList.add(new Dice(mA, mC, 6, "frost"));
            if (critConfirmed) {
                switch (critMultiplier) {
                    case 5:
                        allDiceList.add(new Dice(mA, mC, 10, "frost"));
                    case 4:
                        allDiceList.add(new Dice(mA, mC, 10, "frost"));
                    case 3:
                        allDiceList.add(new Dice(mA, mC, 10, "frost"));
                        allDiceList.add(new Dice(mA, mC, 10, "frost"));
                }
            }
        }
        this.bonusDmg = getBonusDmg();
    }

    public void setDmgRand() {
        for (Dice dice : allDiceList.getList()) {
            dice.rand();
        }
    }

    public int getBonusDmg() {
        int calcBonusDmg = 0;
        if (settings.getBoolean("viser", mC.getResources().getBoolean(R.bool.viser_switch_def))) {
            calcBonusDmg += 2 * tools.toInt(settings.getString("viser_val", String.valueOf(mC.getResources().getInteger(R.integer.viser_val_def))));
        }
        if (settings.getBoolean("thor_switch", mC.getResources().getBoolean(R.bool.thor_switch_def))) {
            calcBonusDmg += 3;
        }
        if (settings.getBoolean("neuf_m_switch", mC.getResources().getBoolean(R.bool.neuf_m_switch_def))) {
            calcBonusDmg += 1;
        }
        if (settings.getBoolean("magic_switch", mC.getResources().getBoolean(R.bool.magic_switch_def))) {
            calcBonusDmg += tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }
        if (settings.getBoolean("composite_switch", mC.getResources().getBoolean(R.bool.composite_switch_def))) {
            calcBonusDmg += 4;
        }
        if (settings.getBoolean("weapon_spe_switch", mC.getResources().getBoolean(R.bool.weapon_spe_switch_def))) {
            calcBonusDmg += 2;
        }
        if (settings.getBoolean("weapon_spe_sup_switch", mC.getResources().getBoolean(R.bool.weapon_spe_sup_switch_def))) {
            calcBonusDmg += 2;
        }
        if (settings.getBoolean("weapon_spe_epic_switch", mC.getResources().getBoolean(R.bool.weapon_spe_epic_switch_def))) {
            calcBonusDmg += 4;
        }

        calcBonusDmg += tools.toInt(settings.getString("epic_dmg_val", String.valueOf(mC.getResources().getInteger(R.integer.epic_dmg_val_def))));
        calcBonusDmg += tools.toInt(settings.getString("dmg_buff", String.valueOf(mC.getResources().getInteger(R.integer.dmg_buff_def))));
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
