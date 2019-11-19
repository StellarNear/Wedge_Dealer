package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 10/01/2018.
 */

public class Skill {
    private String name;
    private String abilityDependence;
    private String descr;
    private String id;
    private int rank;
    private int bonus;
    private Context mC;
    private Tools tools=new Tools();

    public Skill(String name, String abilityDependence, String descr, String id, Context mC){
        this.name=name;
        this.abilityDependence = abilityDependence;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
        refreshVals();
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getAbilityDependence() {
        return this.abilityDependence;
    }

    public void refreshVals() {
        refreshRank();
        refreshBonus();
    }

    private void refreshRank() {
        int valTemp=0;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int valDefId = mC.getResources().getIdentifier(this.id+"_rankDEF", "integer", mC.getPackageName());
            int valDef = mC.getResources().getInteger(valDefId);
            valTemp = tools.toInt(settings.getString(this.id+"_rank", String.valueOf(valDef)));
        } catch ( Exception e) {}
        this.rank = valTemp;
    }

    public int getRank(){
        return this.rank;
    }

    private void refreshBonus() {
        int bonusTemp=0;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int bonusDefId = mC.getResources().getIdentifier(this.id+"_bonusDEF", "integer", mC.getPackageName());
            int bonusDef = mC.getResources().getInteger(bonusDefId);
            bonusTemp = tools.toInt(settings.getString(this.id+"_bonus", String.valueOf(bonusDef)));
        } catch ( Exception e) {}
        this.bonus= bonusTemp;
    }

    public int getBonus(){
        return this.bonus;
    }
}