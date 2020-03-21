package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class PetRollFactory {

    private Activity mA;
    private Context mC;
    private String mode;
    private RollList rollList;
    private SharedPreferences settings;
    private Tools tools=Tools.getTools();
    private Perso pj = PersoManager.getCurrentPJ();

    public PetRollFactory(Activity mA, Context mC, String mode){
        this.mA=mA;
        this.mC=mC;
        this.mode=mode;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildRollList();
    }

    private void buildRollList(){
        this.rollList = new RollList();

        int baseAtk=new CalculationAtk(mC).getBaseAtk();
        if (this.mode.equalsIgnoreCase("fullround")) {
            if(pj.getID().equalsIgnoreCase("sylphe")) {
                Roll roll1 = new PetRoll(mA, mC, baseAtk);
                roll1.setMode("claw");
                this.rollList.add(roll1);
                Roll roll2 = new PetRoll(mA, mC, baseAtk);
                roll2.setMode("claw");
                this.rollList.add(roll2);
                Roll roll3 = new PetRoll(mA, mC, baseAtk);
                roll3.setMode("bite");
                this.rollList.add(roll3);
                Roll roll4 = new PetRoll(mA, mC, baseAtk);
                roll4.setMode("claw");
                this.rollList.add(roll4);
                Roll roll5 = new PetRoll(mA, mC, baseAtk);
                roll5.setMode("claw");
                this.rollList.add(roll5);
            } else {  //rana
                Roll roll1 = new PetRoll(mA, mC, baseAtk);
                roll1.setMode("claw");
                this.rollList.add(roll1);
                Roll roll2 = new PetRoll(mA, mC, baseAtk);
                roll2.setMode("claw");
                this.rollList.add(roll2);
                Roll roll3 = new PetRoll(mA, mC, baseAtk);
                roll3.setMode("bite");
                this.rollList.add(roll3);
            }

            int nCount=1;
            for (Roll roll : this.rollList.getList()){
                roll.setNthAtkRoll(nCount);
                nCount++;
            }
        } else if(this.mode.equalsIgnoreCase("leap") && pj.getAllCapacities().capacityIsActive("capacity_leap")) {
            Roll roll1 = new PetRoll(mA, mC, baseAtk);
            roll1.setMode("leapclaw");
            this.rollList.add(roll1);
            Roll roll2 = new PetRoll(mA, mC, baseAtk);
            roll2.setMode("leapclaw");
            this.rollList.add(roll2);
            Roll roll3 = new PetRoll(mA, mC, baseAtk);
            roll3.setMode("leapbite");
            this.rollList.add(roll3);
            Roll roll4 = new PetRoll(mA, mC, baseAtk);
            roll4.setMode("leapclaw");
            this.rollList.add(roll4);
            Roll roll5 = new PetRoll(mA, mC, baseAtk);
            roll5.setMode("leapclaw");
            this.rollList.add(roll5);
        } else {
            Roll roll = new PetRoll(mA, mC, baseAtk);
            roll.setMode(mode);
            this.rollList.add(roll);
        }
    }

    public RollList getRollList() {
        return this.rollList;
    }
}
