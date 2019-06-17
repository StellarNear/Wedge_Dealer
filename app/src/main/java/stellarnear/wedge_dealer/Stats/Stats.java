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

    //Atk part

    public Integer getNAtksTot() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getNAtksTot();
        }
        return tot;
    }

    public int getNAtksHit() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListNthAtksHit().size();
        }
        return tot;
    }

    public int getNAtksMiss() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListNthAtksMiss().size();
        }
        return tot;
    }

    public int getNAtksHitNthAtk(int nthAtkSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int nthAtk : stat.getListNthAtksHit()){
                if(nthAtk==nthAtkSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNAtksMissNthAtk(int nthAtkSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int nthAtk : stat.getListNthAtksMiss()){
                if(nthAtk==nthAtkSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNCrit() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getNCrit();
        }
        return tot;
    }

    public int getNCritNat() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getNCritNat();
        }
        return tot;
    }

    public int getNCritNth(int nthAtkSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int nthAtk : stat.getListNthAtksCrit()){
                if(nthAtk==nthAtkSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNCritNatNth(int nthAtkSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int nthAtk : stat.getListNthAtksCritNat()){
                if(nthAtk==nthAtkSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    // Dmg part

    public int getNDmgTot() {
        return listStats.size();
    }

    public int getMinDmgTot() {
        int res=0;
        for (Stat stat:listStats){
            int sumDmg=stat.getSumDmg();
            if(res==0 && sumDmg!=0 ){
                res=sumDmg;
            }
            if (sumDmg!=0 && sumDmg<res){
                res=sumDmg;
            }
        }
        return res;
    }

    public int getMaxDmgTot() {
        int res=0;
        for (Stat stat:listStats){
            int sumDmg=stat.getSumDmg();
            if(res==0 && sumDmg!=0 ){
                res=sumDmg;
            }
            if (sumDmg!=0 && sumDmg>res){
                res=sumDmg;
            }
        }
        return res;
    }

    public int getMinDmgElem(String elem) {
        int res=0;
        for (Stat stat:listStats){
            if(res==0 && stat.getElemSumDmg().get(elem)!=null ){
                res=stat.getElemSumDmg().get(elem);
            }
            if (stat.getElemSumDmg().get(elem)!=null && stat.getElemSumDmg().get(elem)<res){
                res=stat.getElemSumDmg().get(elem);
            }
        }
        return res;
    }

    public int getMaxDmgElem(String elem) {
        int res=0;
        for (Stat stat:listStats){
            if(res==0 && stat.getElemSumDmg().get(elem)!=null ){
                res=stat.getElemSumDmg().get(elem);
            }
            if (stat.getElemSumDmg().get(elem)!=null && stat.getElemSumDmg().get(elem)>res){
                res=stat.getElemSumDmg().get(elem);
            }
        }
        return res;
    }
}
