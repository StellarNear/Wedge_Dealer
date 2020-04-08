package stellarnear.wedge_companion;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Stats.Stat;

public class HallOfFame {
    private List<FameEntry> hallOfFameList = new ArrayList<>();
    private TinyDB tinyDB;
    private String pjID = "";

    public HallOfFame(Context mC, String pjID) {
        tinyDB = new TinyDB(mC);
        this.pjID = pjID;
        try {
            refreshAllOfFame();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Load_HALL", "Error loading hall of fame " + pjID + e.getMessage());
            reset();
        }
    }

    // hall of frame

    private void refreshAllOfFame() {
        List<FameEntry> listDB = tinyDB.getHallOfFame("localSaveHallOfFame" + pjID);
        if (listDB.size() == 0) {
            initAllOfFame();
            saveLocalHallOfFame();
        } else {
            hallOfFameList = listDB;
        }
    }

    private void initAllOfFame() {
        this.hallOfFameList = new ArrayList<>();
    }

    private void saveLocalHallOfFame() { //sauvegarde dans local DB
        tinyDB.putHallOfFame("localSaveHallOfFame" + pjID, hallOfFameList);
    }

    public List<FameEntry> getHallOfFameList() {
        return hallOfFameList;
    }

    public void addToHallOfFame(FameEntry entry) {
        hallOfFameList.add(entry);
        saveLocalHallOfFame();
    }

    public boolean containsStat(Stat lastStat) {
        boolean val = false;
        if (hallOfFameList.size() > 0 && hallOfFameList.get(hallOfFameList.size() - 1).getStat().equals(lastStat)) {
            val = true;
        }
        return val;
    }

    public void refreshSave() {
        saveLocalHallOfFame();
    }

    public void reset() {
        this.hallOfFameList = new ArrayList<>();
        saveLocalHallOfFame();
    }

    public void loadFromSave() {
        refreshAllOfFame();
    }
}
