package stellarnear.wedge_dealer.Stats;

import android.content.Context;

import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TinyDB;

public class Stats {
    private StatsList statsList = new StatsList();
    private TinyDB tinyDB;

    public Stats(Context mC){
        tinyDB = new TinyDB(mC);
        refreshStats();
    }

    private void saveLocalStats() { //sauvegarde dans local DB
        tinyDB.putStats("localSaveStats", statsList);
    }

    public void refreshStats(){ //Initialisation ou lecture depuis DB
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
