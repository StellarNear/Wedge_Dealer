package stellarnear.wedge_dealer.Stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TinyDB;
import stellarnear.wedge_dealer.Tools;

public class Stats {
    private StatsList statsList = new StatsList();
    private SharedPreferences settings;
    private Context mC;
    private Tools tools=new Tools();
    private TinyDB tinyDB;

    public Stats(Context mC){
        this.mC = mC;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        refreshStats();
    }

    private void saveLocalStats() { //sauvegarde dans local DB
        tinyDB.putStats("localSaveStats", statsList);
    }

    public void refreshStats(){ //Initialisation ou lecture depuis DB
        tinyDB = new TinyDB(mC);
        StatsList listDB = tinyDB.getStats("localSaveStats");
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

    public void resetStats() {
        this.statsList =new StatsList();
        saveLocalStats();
    }
}
