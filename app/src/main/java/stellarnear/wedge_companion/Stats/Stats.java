package stellarnear.wedge_companion.Stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.TinyDB;
import stellarnear.wedge_companion.Tools;

public class Stats {
    private StatsList statsList = new StatsList();
    private TinyDB tinyDB;
    private String pjID="";
    private Context mC;
    private Tools tools= new Tools();

    public Stats(Context mC,String pjID){
        this.mC=mC;
        tinyDB = new TinyDB(mC);
        this.pjID=pjID;
        try {
            refreshStats();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Load_STATS","Error loading stats "+pjID+e.getMessage());
            reset();
        }
    }

    private void saveLocalStats() { //sauvegarde dans local DB
        tinyDB.putStats("localSaveStats"+pjID, statsList);
    }   //on save avec le pjID pour avoir une database differente pour halda

    public void refreshStats(){ //Initialisation ou lecture depuis DB
        StatsList listDB = tinyDB.getStats("localSaveStats"+pjID);  //on save avec le pjID pour avoir une database differente pour halda
        if (listDB.size() == 0) {
            initStats();
            saveLocalStats();
        } else {
            statsList = listDB;
        }
    }

    private void initStats(){
        this.statsList =new StatsList();
    }

    public void storeStatsFromRolls(RollList allRolls) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (!settings.getBoolean("switch_demo_mode",mC.getResources().getBoolean(R.bool.switch_demo_mode_def))){
            Stat stat = new Stat();
            stat.feedStat(allRolls);
            statsList.add(stat);
            saveLocalStats();
            tools.customToast(mC,"Jet d'attaque sauvegardé","short");
        } else {
            tools.customToast(mC,"Mode démo activé pas de sauvegarde","short");
        }
    }

    public StatsList getStatsList() {
        return statsList;
    }

    public void reset() {
        this.statsList =new StatsList();
        saveLocalStats();
    }

    public void loadFromSave() {
        refreshStats();
    }

    public void removeLast() {
        statsList.removeLastStat();
        saveLocalStats();
    }
}
