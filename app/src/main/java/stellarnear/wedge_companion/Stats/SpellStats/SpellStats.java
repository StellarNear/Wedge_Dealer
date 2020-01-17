package stellarnear.wedge_companion.Stats.SpellStats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.SpellList;
import stellarnear.wedge_companion.TinyDB;


public class SpellStats {
    private SpellStatsList spellStatsList = new SpellStatsList();
    private TinyDB tinyDB;
    private String pjID;
    private Context mC;

    public SpellStats(Context mC,String pjID){
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
        tinyDB.putSpellStats("localSaveSpellStats"+pjID, spellStatsList);
    }

    public void refreshStats(){ //Initialisation ou lecture depuis DB
        SpellStatsList listDB = tinyDB.getSpellStats("localSaveSpellStats"+pjID);
        if (listDB.size() == 0) {
            initStats();
            saveLocalStats();
        } else {
            spellStatsList = listDB;
        }
    }

    private void initStats(){
        this.spellStatsList =new SpellStatsList();
    }

    public void storeSpellStatsFromRolls(SpellList selectedSpells) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (!settings.getBoolean("switch_demo_mode",mC.getResources().getBoolean(R.bool.switch_demo_mode_def))){
            SpellStat spellStat = new SpellStat();
            spellStat.feedStat(selectedSpells);
            spellStatsList.add(spellStat);
            saveLocalStats();
        }
    }

    public SpellStatsList getSpellStatsList() {
        return spellStatsList;
    }

    public void reset() {
        this.spellStatsList =new SpellStatsList();
        saveLocalStats();
    }

}
