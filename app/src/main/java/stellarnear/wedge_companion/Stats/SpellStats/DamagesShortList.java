package stellarnear.wedge_companion.Stats.SpellStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;

public class DamagesShortList {
    private List<DamagesShortListElement> listElements; //list des subDamageIndivSpell

    public DamagesShortList(SpellList spells){
        buildList(spells);
    }

    public DamagesShortList(){
        listElements=new ArrayList<>();
    }

    public void add(DamagesShortListElement element) {
        listElements.add(element);
    }

    public void add(DamagesShortList list) {
            listElements.addAll(list.asList());
    }

    private void buildList(SpellList spells) {
        listElements=new ArrayList<>();
        Map<Spell,DamagesShortListElement> mapBindedParentDamageShortListElement= new HashMap<>();
        for (Spell spell:spells.asList()){
            if(spell.isBinded()){
                if(mapBindedParentDamageShortListElement.get(spell.getBindedParent())==null){
                    DamagesShortListElement element=new DamagesShortListElement(spell);
                    mapBindedParentDamageShortListElement.put(spell.getBindedParent(),element);
                    listElements.add(element);
                } else {
                    mapBindedParentDamageShortListElement.get(spell.getBindedParent()).addBindedSpell(spell);
                }
            } else {
                listElements.add(new DamagesShortListElement(spell));
            }
        }
    }

    public int getDmgMoy(){
        float result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getDmgSum();
        }
        return listElements.size()>0?Math.round(result/listElements.size()):0;
    }

    public int getDmgSum() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getDmgSum();
        }
        return result;
    }



    public int getRankMoy() {
        float result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getRank();
        }
        return listElements.size()>0?Math.round(result/listElements.size()):0;
    }

    public int getMetaRankMoy() {
        float result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getnMeta();
        }
        return listElements.size()>0?Math.round(result/listElements.size()):0;
    }

    public int getNMythicMoy() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(element.isMythic()) {
                result ++;
            }
        }
        return result;
    }

    public int getNDamageSpell() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(element.getDmgSum()>0) {
                result++;
            }
        }
        return result;
    }

    public DamagesShortList filterByElem(String elem){
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getElement().equalsIgnoreCase(elem)){
                result.add(element);
            }
        }
        return result;
    }

    public DamagesShortList filterByRank(int rank){
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getRank()==rank){
                result.add(element);
            }
        }
        return result;
    }


    public DamagesShortList filterByNMeta(int iMeta) {
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getnMeta()==iMeta){
                result.add(element);
            }
        }
        return result;
    }


    public List<DamagesShortListElement> asList() {
        return listElements;
    }

    public int size() {
        return listElements.size();
    }

    /* DAMAGE part */

    public int getSumDmgTot() {
        int tot=0;
        for (DamagesShortListElement element : listElements){
            tot+=element.getDmgSum();
        }
        return tot;
    }

    public int getMinDmg() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(result==0 && element.getDmgSum()!=0){result=element.getDmgSum();}
            if(result>element.getDmgSum()){result=element.getDmgSum();}
        }
        return result;
    }

    public int getMaxDmg() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(result<element.getDmgSum()){result=element.getDmgSum();}
        }
        return result;
    }

    public DamagesShortListElement getLastDamageElement() {
        return listElements!=null && listElements.size()>0 ? listElements.get(listElements.size()-1) : null;
    }

    public List<DamagesShortListElement> getDamageElementList() {
        return listElements;
    }
}
