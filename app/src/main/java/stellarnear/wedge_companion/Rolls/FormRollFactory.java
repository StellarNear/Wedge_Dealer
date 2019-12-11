package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.Attack.Attack;
import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.Perso.Form;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class FormRollFactory {

    private Activity mA;
    private Context mC;
    private String mode;
    private RollList rollList;
    private SharedPreferences settings;
    private Tools tools=new Tools();
    private Perso pj = PersoManager.getCurrentPJ();
    private Form currentForm= pj.getAllForms()!=null?pj.getAllForms().getCurrentForm():null;

    public FormRollFactory(Activity mA, Context mC, String mode){
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

            for(Attack atk : currentForm.getAllAtks()){
                Roll roll = new FormRoll(mA, mC, baseAtk,atk);
                this.rollList.add(roll);
            }

            /*
            int nCount=1;
            for (Roll roll : this.rollList.getList()){
                roll.setNthAtkRoll(nCount);
                nCount++;
            }
             */
        } else {
            Roll roll = new FormRoll(mA, mC, baseAtk,currentForm.getAllAtks().get(0));
            this.rollList.add(roll);
        }
    }

    public RollList getRollList() {
        return this.rollList;
    }
}
