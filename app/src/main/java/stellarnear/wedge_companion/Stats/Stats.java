package stellarnear.wedge_companion.Stats;

import android.content.Context;
import android.util.Log;

import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.TinyDB;

public class Stats {
    private StatsList statsList = new StatsList();
    private TinyDB tinyDB;
    private String pjID="";

    public Stats(Context mC,String pjID){
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

    public void storeStatsFromRolls(RollList selectedRolls) {
        Stat stat = new Stat();
        stat.feedStat(selectedRolls);
        statsList.add(stat);
        saveLocalStats();
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
}
