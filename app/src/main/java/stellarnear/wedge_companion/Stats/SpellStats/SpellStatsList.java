package stellarnear.wedge_companion.Stats.SpellStats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpellStatsList {
    private List<SpellStat> listSpellStats = new ArrayList<>();

    public SpellStatsList(){
        this.listSpellStats =new ArrayList<SpellStat>();
    }

    public SpellStatsList(List<SpellStat> listSpellStats){
        this.listSpellStats = listSpellStats;
    }

    public void add(SpellStat value) {
        listSpellStats.add(value);
    }

    public int size() {
        return listSpellStats.size();
    }

    public List<SpellStat> asList() {
        return listSpellStats;
    }

    public int getNSpellHit() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListHit().size();
        }
        return tot;
    }

    public int getNSpellMiss() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListMiss().size();
        }
        return tot;
    }

    public int getNSpellHitForRank(int rankSelectedForPieChart) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListHit()){
                if(rank==rankSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNSpellMissForRank(int rankSelectedForPieChart) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListMiss()){
                if(rank==rankSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }


    public int getNCrit() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListCrit().size();
        }
        return tot;
    }

    public SpellStat getLastStat() {
        SpellStat res=null;
        if (listSpellStats.size()>0){
            res= listSpellStats.get(listSpellStats.size()-1);
        }
        return res;
    }

    public boolean contains(SpellStat lastSpellStat) {
        return listSpellStats.contains(lastSpellStat);
    }


    public int getNGlaeBoost() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListGlaeBoost().size();
        }
        return tot;
    }

    public int getNContactMiss() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListContactMiss().size();
        }
        return tot;
    }

    public int getNGlaeFail() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListGlaeFail().size();
        }
        return tot;
    }

    public int getNResist() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListResist().size();
        }
        return tot;
    }

    public int getNGlaeBoostForRank(int selectedRank) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListGlaeBoost()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNContactMissForRank(int selectedRank) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListContactMiss()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNGlaeFailForRank(int selectedRank) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListGlaeFail()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNResistForRank(int selectedRank) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListResist()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNCritForRank(int selectedRank) {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            for (int rank : spellStat.getListCrit()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNSpell() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getListRank().size();
        }
        return tot;
    }

    public int getNDamageSpell() {
        int tot=0;
        for (SpellStat spellStat : listSpellStats){
            tot+= spellStat.getNDamageSpell();
        }
        return tot;
    }

    public SpellStatsList filterByDate(String dateFormated){
        SpellStatsList res = new SpellStatsList();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
        for (SpellStat spellStat : listSpellStats){
            if( formater.format(spellStat.getDate()).equalsIgnoreCase(dateFormated)){
                res.add(spellStat);
            }
        }
        return res;
    }

    public DamagesShortList getDamageShortList() {
        DamagesShortList damagesShortListAll= new DamagesShortList();
        for(SpellStat spellStat : listSpellStats){
            damagesShortListAll.add(spellStat.getDamageShortList());
        }
        return damagesShortListAll;
    }

    public DamagesShortList getDamageShortListForElem(String elem) {
        DamagesShortList damagesShortListAll= new DamagesShortList();
        for(SpellStat spellStat : listSpellStats){
            damagesShortListAll.add(spellStat.getDamageShortList().filterByElem(elem));
        }
        return damagesShortListAll;
    }
}
