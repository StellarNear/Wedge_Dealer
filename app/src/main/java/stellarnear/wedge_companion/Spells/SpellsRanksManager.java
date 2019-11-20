package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.Tools;

public class SpellsRanksManager {
    private Tools tools= new Tools();
    private SharedPreferences settings;
    private int highestSpellRank=0;
    private ArrayList<Resource> spellTiers;
    private OnHighTierChange mListner;
    private Context mC;
    private String pjID="";

    public SpellsRanksManager(Context mC,String pjID){
        this.mC=mC;
        this.pjID=pjID;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        refreshRanks();
    }

    public void refreshRanks() {
        int resId = mC.getResources().getIdentifier("highest_tier_spell_def"+pjID, "integer", mC.getPackageName());
        int newHighTier=tools.toInt(settings.getString("highest_tier_spell"+ pjID,String.valueOf(mC.getResources().getInteger(resId))));
        if(highestSpellRank!=newHighTier){
            highestSpellRank = newHighTier;
            refreshAllTiers();
            if(mListner!=null){mListner.onEvent();}
        }
    }

    private void refreshAllTiers() {
        spellTiers=new ArrayList<>();
        for(int rank=1;rank<=highestSpellRank;rank++){
            Resource rankRes = new Resource("Sort disponible rang "+rank,"Sort "+rank,true,true,"spell_rank_"+rank,mC,pjID);
            int val = readResourceMax( rankRes.getId());
            rankRes.setMax(val);
            rankRes.setCurrent(readResourceCurrent(rankRes.getId()));
            spellTiers.add(rankRes);
        }
    }

    private int readResourceCurrent(String id) {
        int val=0;
        try {
            val=settings.getInt(id + "_current",  0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    private int readResourceMax(String key) {
        int val=0;
        int defId=0;
        try {
            defId = mC.getResources().getIdentifier(key.toLowerCase() + "_def", "integer", mC.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {  //deux trycatch different car on peut ne pas avoir de valeur def defini mais une valeur manuelle dans les settings
            if(defId!=0){
                val=tools.toInt(settings.getString(key.toLowerCase(), String.valueOf(mC.getResources().getInteger(defId))));
            } else {
                val=tools.toInt(settings.getString(key.toLowerCase(), "0"));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public ArrayList<Resource> getSpellTiers() {
        return spellTiers;
    }

    public void refreshMax() {
        for(Resource res : spellTiers){
            int val = readResourceMax( res.getId());
            res.setMax(val);
        }
    }

    public int getHighestTier() {
        return highestSpellRank;
    }

    public String getPercentAvail() {
        int allRankCurrent=0;
        int allRankMax=0;
        for (Resource res : spellTiers){
            allRankCurrent+=res.getCurrent();
            allRankMax+=res.getMax();
        }
        return Math.round(100f*allRankCurrent/allRankMax)+"%";
    }

    public interface OnHighTierChange {
        void onEvent();
    }

    public void setRefreshEventListener(OnHighTierChange eventListener) {
        mListner = eventListener;
    }
}
