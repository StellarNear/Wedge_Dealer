package stellarnear.wedge_dealer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_dealer.Stats.Stat;

public class HallOfFame {
    private List<FameEntry> hallOfFameList = new ArrayList<>();
    private TinyDB tinyDB;

    public HallOfFame(Context mC){
        tinyDB = new TinyDB(mC);
        refreshAllOfFame();
    }

    // hall of frame

    private void refreshAllOfFame() {
        List<FameEntry> listDB = tinyDB.getHallOfFame("localSaveHallOfFame");
        if (listDB.size() == 0) {
            initAllOfFame();
            saveLocalHallOfFame();
        } else {
            hallOfFameList = listDB;
        }
    }
    private void initAllOfFame(){
        this.hallOfFameList =new ArrayList<>();
    }

    private void saveLocalHallOfFame() { //sauvegarde dans local DB
        tinyDB.putHallOfFame("localSaveHallOfFame", hallOfFameList);
    }

    public List<FameEntry> getHallOfFameList() {
        return hallOfFameList;
    }

    public void addToHallOfFame(FameEntry entry) {
        hallOfFameList.add(entry);
        saveLocalHallOfFame();
    }

    public boolean containsStat(Stat lastStat) {
        boolean val=false;
            if (hallOfFameList.size()>0 && hallOfFameList.get(hallOfFameList.size()-1).getStat().equals(lastStat)){
                val=true;
            }
        return val;
    }

    public void refreshSave() {
        saveLocalHallOfFame();
    }
}
