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
    private String pjID="";
    private Tools tools=Tools.getTools();

    public Skill(String name, String abilityDependence, String descr, String id, Context mC,String pjID){
        this.name=name;
        this.abilityDependence = abilityDependence;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
        this.pjID=pjID;
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
        int valDef=0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        try {
            int valDefId = mC.getResources().getIdentifier(this.id+"_rankDEF"+extendID, "integer", mC.getPackageName());
            valDef = mC.getResources().getInteger(valDefId);
        } catch ( Exception e) {}
        this.rank = tools.toInt(settings.getString(this.id+"_rank"+extendID, String.valueOf(valDef)));
    }

    public int getRank(){
        return this.rank;
    }

    private void refreshBonus() {
        int bonusDef=0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        try {
            int bonusDefId = mC.getResources().getIdentifier(this.id+"_bonusDEF"+extendID, "integer", mC.getPackageName());
            bonusDef = mC.getResources().getInteger(bonusDefId);
        } catch ( Exception e) {}
        this.bonus= tools.toInt(settings.getString(this.id+"_bonus"+extendID, String.valueOf(bonusDef)));
    }

    public int getBonus(){
        return this.bonus;
    }
}