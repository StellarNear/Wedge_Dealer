package stellarnear.wedge_dealer.Stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TinyDB;
import stellarnear.wedge_dealer.Tools;

public class StatsList {
    private List<Stat> listStats = new ArrayList<>();

    public StatsList(){
        this.listStats=new ArrayList<Stat>();
    }

    public StatsList(List<Stat> listStats){
        this.listStats=listStats;
    }

    public void add(Stat value) {
        listStats.add(value);
    }

    public int size() {
        return listStats.size();
    }

    public List<Stat> asList() {
        return listStats;
    }

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

    public int getSumDmgTot() {
        int res=0;
        for (Stat stat:listStats){
            res+=stat.getSumDmg();
        }
        return res;
    }

    public int getSumDmgTotElem(String elem) {
        int res=0;
        for (Stat stat:listStats){
            res+=stat.getElemSumDmg().get(elem);
        }
        return res;
    }

}