package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class Roll {
    protected AtkRoll atkRoll;
    protected List<DmgRoll> dmgRollList; //on peut avoir plusieurs fleches de degat par jet d'attaque
    protected int nthAtkRoll;
    protected Activity mA;
    protected Context mC;
    protected SharedPreferences settings;
    protected Tools tools=new Tools();
    protected Perso pj = PersoManager.getCurrentPJ();
    protected String mode;

    public Roll(Activity mA, Context mC, Integer atkBase) {
        this.mA=mA;
        this.mC=mC;
        this.atkRoll=new AtkRoll(mA,mC,atkBase);
        this.dmgRollList=new ArrayList<>();
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public AtkRoll getAtkRoll(){
        return this.atkRoll;
    }

    public void setNthAtkRoll(int nthAtkRoll) {
        this.nthAtkRoll = nthAtkRoll;
    }

    public void setMode(String mode) {
        atkRoll.setMode(mode);
        this.mode=mode; //les dmgroll sont pas encore présent
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()){
            this.dmgRollList.add(new DmgRoll(mA,mC,atkRoll.isCritConfirmed(),atkRoll.getAtkDice().getRandValue()==20));
            for(DmgRoll dmgRoll:this.dmgRollList){
                dmgRoll.setMode(mode);
                dmgRoll.setDmgRand();
            }
        }
    }


    public List<DmgRoll> getDmgRollList(){
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
    public ImageView getImgAtk() {
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

    //partie dégat

    public DiceList getDmgDiceListFromNface(int nFace) {
        DiceList diceList=new DiceList();
        for(DmgRoll dmgRoll:this.dmgRollList){
            diceList.add(dmgRoll.getDmgDiceList().filterWithNface(nFace));
        }

        return diceList;
    }

    public DiceList getDmgDiceList() {
        DiceList diceList=new DiceList();
        for(DmgRoll dmgRoll:this.dmgRollList){
            diceList.add(dmgRoll.getDmgDiceList());
        }
        return diceList;
    }

    public int getDmgSum(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum=0;
        for(DmgRoll dmgRoll:this.dmgRollList){
            sum+=dmgRoll.getSumDmg(element);
        }
        return sum;
    }

    public int getMaxDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum=0;
        for(DmgRoll dmgRoll:this.dmgRollList){
            sum+=dmgRoll.getMaxDmg(element);
        }
        return sum;
    }

    public int getMinDmg(String... elementArg) {
        String element = elementArg.length > 0 ? elementArg[0] : "";
        Integer sum=0;
        for(DmgRoll dmgRoll:this.dmgRollList){
            sum+=dmgRoll.getMinDmg(element);
        }

        return sum;
    }

    public boolean isMissed() {
        return atkRoll.isMissed();
    }


    public int getNthAtkRoll() {
        return nthAtkRoll;
    }

}
