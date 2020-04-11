package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public abstract class Roll {
    protected AtkRoll atkRoll;
    protected List<DmgRoll> dmgRollList = new ArrayList<>(); //on peut avoir plusieurs fleches de degat par jet d'attaque
    protected int nthAtkRoll;
    protected Activity mA;
    protected Context mC;
    protected SharedPreferences settings;
    protected Tools tools = Tools.getTools();
    protected Perso pj = PersoManager.getCurrentPJ();
    protected String mode;
    protected boolean biteBoosted = false;

    public Roll(Activity mA, Context mC, Integer atkBase) {
        this.mA = mA;
        this.mC = mC;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public AtkRoll getAtkRoll() {
        return this.atkRoll;
    }

    public void setMode(String mode) {
        atkRoll.setMode(mode);
        this.mode = mode; //les dmgroll sont pas encore présent
    }

    public abstract void setDmgRand(); //defini dans les deux sous type

    public List<DmgRoll> getDmgRollList() {
        return this.dmgRollList;
    }

    public Boolean isInvalid() {
        return atkRoll.isInvalid();
    }

    public void invalidated() { //roll invalidé par un fail précédent
        atkRoll.invalidated();
    }

    public boolean isFailed() {
        return atkRoll.isFailed();
    }

    public boolean isCrit() {
        return atkRoll.isCrit();
    }

    public boolean isHitConfirmed() {
        return atkRoll.isHitConfirmed();
    }

    public boolean isCritConfirmed() {
        return atkRoll.isCritConfirmed();
    }

    public Integer getPreRandValue() {
        return atkRoll.getPreRandValue();
    }

    public Dice getAtkDice() {
        return atkRoll.getAtkDice();
    }

    public View getImgAtk() {
        return atkRoll.getImgAtk();
    }

    public int getAtkValue() {
        return atkRoll.getValue();
    }

    public CheckBox getHitCheckbox() {
        return atkRoll.getHitCheckbox();
    }

    public CheckBox getCritCheckbox() {
        return atkRoll.getCritCheckbox();
    }

    public void isDelt() {
        atkRoll.isDelt();
    }

    public DiceList getDmgDiceListFromNface(int nFace) {
        DiceList diceList = new DiceList();
        for (DmgRoll dmgRoll : this.dmgRollList) {
            diceList.add(dmgRoll.getDmgDiceList().filterWithNface(nFace));
        }
        return diceList;
    }

    //partie dégat

    public DiceList getDmgDiceList() {
        DiceList diceList = new DiceList();
        for (DmgRoll dmgRoll : this.dmgRollList) {
            diceList.add(dmgRoll.getDmgDiceList());
        }
        return diceList;
    }

    public int getDmgSum(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum = 0;
        for (DmgRoll dmgRoll : this.dmgRollList) {
            if (biteBoosted) {
                sum += 2 * dmgRoll.getSumDmg(element);
            } else {
                sum += dmgRoll.getSumDmg(element);
            }
        }
        return sum;
    }

    public int getMaxDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum = 0;
        for (DmgRoll dmgRoll : this.dmgRollList) {
            if (biteBoosted) {
                sum += 2 * dmgRoll.getMaxDmg(element);
            } else {
                sum += dmgRoll.getMaxDmg(element);
            }

        }
        return sum;
    }

    public int getMinDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum = 0;
        for (DmgRoll dmgRoll : this.dmgRollList) {
            if (biteBoosted) {
                sum += 2 * dmgRoll.getMinDmg(element);
            } else {
                sum += dmgRoll.getMinDmg(element);
            }
        }

        return sum;
    }

    public boolean isMissed() {
        return atkRoll.isMissed();
    }

    public int getNthAtkRoll() {
        return nthAtkRoll;
    }

    public void setNthAtkRoll(int nthAtkRoll) {
        this.nthAtkRoll = nthAtkRoll;
    }

    public abstract void rangeMalus(int malus);

    public abstract void lynxEyeBoost();

    public void makeBiteBoosted() {
        this.biteBoosted = true;
    }
}
