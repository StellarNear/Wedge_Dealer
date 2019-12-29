package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class RangeDmgRoll extends DmgRoll {

    private int nthAtkRoll=0;
    private int nthDmgRoll=0;

    public RangeDmgRoll(Activity mA, Context mC, Boolean critConfirmed, Boolean naturalCrit, int nthAtkRoll, int nthDmgRoll) {
        super(mA, mC, critConfirmed, naturalCrit);

        this.nthAtkRoll=nthAtkRoll;
        this.nthDmgRoll=nthDmgRoll;
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
            critMultiplier = 4;
        } else {
            critMultiplier = 3;  //de base l'arc c'est *3
        }
        if (naturalCrit && settings.getBoolean("isillirit_switch", mC.getResources().getBoolean(R.bool.isillirit_switch_def))) {
            critMultiplier += 1;
        }
    }

    private void addDmgDices() {
        Dice dice = new Dice(mA, mC, 8);
        if (critConfirmed) {
            dice.makeCritable();
        }
        allDiceList.add(dice);

        if (settings.getBoolean("fire_blast_switch", mC.getResources().getBoolean(R.bool.fire_blast_switch_def))) {
            allDiceList.add(new Dice(mA, mC, 6, "fire"));
            allDiceList.add(new Dice(mA, mC, 6, "fire"));
            allDiceList.add(new Dice(mA, mC, 6, "fire"));
            if (critConfirmed) {
                switch (critMultiplier) {
                    case 5:
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                    case 4:
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                    case 3:
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
                        allDiceList.add(new Dice(mA, mC, 6, "fire"));
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
        if (settings.getBoolean("froid_intense_switch", mC.getResources().getBoolean(R.bool.ice_blast_switch_def))) {
            allDiceList.add(new Dice(mA, mC, 6, "frost"));
            allDiceList.add(new Dice(mA, mC, 6, "frost"));
            allDiceList.add(new Dice(mA, mC, 6, "frost"));
            if (critConfirmed) {
                switch (critMultiplier) {
                    case 5:
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                    case 4:
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                    case 3:
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                        allDiceList.add(new Dice(mA, mC, 6, "frost"));
                }
            }
        }
    }

    public int getBonusDmg() {
        int calcBonusDmg = 0;
        if (pj.getAllFeats().featIsActive("feat_aim")) {
            calcBonusDmg += 2 * tools.toInt(settings.getString("feat_aim_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_aim_val_def))));
        }
        if (settings.getBoolean("thor_switch", mC.getResources().getBoolean(R.bool.thor_switch_def))) {
            calcBonusDmg += 3;
        }
        if (pj.getAllFeats().featIsActive("feat_nine_m")) {
            calcBonusDmg += 1;
        }
        if (settings.getBoolean("magic_switch", mC.getResources().getBoolean(R.bool.magic_arrow_switch_def))) {
            calcBonusDmg += tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }
        if (settings.getBoolean("composite_switch", mC.getResources().getBoolean(R.bool.composite_switch_def))) {
            calcBonusDmg += 4;
        }
        if (pj.getAllFeats().featIsActive("feat_weapon_spe")) {
            calcBonusDmg += 2;
        }
        if (pj.getAllFeats().featIsActive("feat_weapon_spe_sup")) {
            calcBonusDmg += 2;
        }
        if (pj.getAllFeats().featIsActive("feat_weapon_spe_epic")) {
            calcBonusDmg += 4;
        }
        if (pj.getAllFeats().featIsActive("feat_hammer_gap")) {
            int multiVal = tools.toInt(settings.getString("feat_manyshot_suprem_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_manyshot_suprem_val_def))));
            calcBonusDmg += ((nthAtkRoll-1)*multiVal)+nthDmgRoll-1;
        }

        calcBonusDmg += tools.toInt(settings.getString("epic_dmg_val", String.valueOf(mC.getResources().getInteger(R.integer.attack_dmg_epic_DEF))));
        calcBonusDmg += tools.toInt(settings.getString("bonus_global_dmg_temp", String.valueOf(0)));
        calcBonusDmg += tools.toInt(settings.getString("bonus_dmg_temp"+ PersoManager.getPJSuffix(), String.valueOf(0)));
        return calcBonusDmg;
    }
}
