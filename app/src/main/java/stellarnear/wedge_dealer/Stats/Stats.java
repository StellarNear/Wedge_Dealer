package stellarnear.wedge_dealer.Stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TinyDB;
import stellarnear.wedge_dealer.Tools;

public class Stats {
    private List<Stat> listStats = new ArrayList<>();
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
        tinyDB.putStats("localSaveStats", listStats);
    }

    public void refreshStats(){ //Initialisation ou lecture depuis DB
        tinyDB = new TinyDB(mC);
        List<Stat> listDB = tinyDB.getStats("localSaveStats");
        if (listDB.size() == 0) {
            initStats();
            saveLocalStats();
        } else {
            listStats = listDB;
        }
    }

    private void initStats(){
        this.listStats=new ArrayList<Stat>();
    }

    public void storeStatsFromRolls(RollList selectedRolls) {
        Stat stat = new Stat();
        stat.feedStat(selectedRolls);
        listStats.add(stat);
        saveLocalStats();
    }

    public List<Stat> getListStats() {
        return listStats;
    }

    public void resetStats() {
        this.listStats=new ArrayList<>();
        saveLocalStats();
    }
}
