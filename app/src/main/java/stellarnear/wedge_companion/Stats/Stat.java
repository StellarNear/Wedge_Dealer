package stellarnear.wedge_companion.Stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import stellarnear.wedge_companion.Rolls.RangedRoll;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class Stat {
    private Map<String,Integer> elemSumDmg=new HashMap<>();
    private List<Integer> nthAtksHit =new ArrayList<>();
    private List<Integer> nthAtksMiss =new ArrayList<>();
    private List<Integer> nthAtksCrit =new ArrayList<>();
    private List<Integer> nthAtksCritNat =new ArrayList<>();
    private Date date=null;
    private UUID uuid;

    public Stat(){  }

    public void feedStat(RollList rolls){
        List<String> elems = Arrays.asList("", "fire", "shock", "frost");
        for (String elem : elems){
            elemSumDmg.put(elem,rolls.getDmgSumFromType(elem));
        }
        for (Roll roll:rolls.getList()){
            if(roll.isCritConfirmed()){
                nthAtksCrit.add(roll.getNthAtkRoll());
                if(roll.getAtkRoll().getAtkDice().getRandValue()==20){
                    nthAtksCritNat.add(roll.getNthAtkRoll());
                }
            }
            if( roll.isHitConfirmed()){
                nthAtksHit.add(roll.getNthAtkRoll());
            } else if (roll.isMissed()){
                nthAtksMiss.add(roll.getNthAtkRoll());
            }
        }
        this.date=new Date();
        this.uuid=UUID.randomUUID();
    }

    public Integer getNCrit() {
        return nthAtksCrit.size();
    }

    public Integer getNCritNat() {
        return nthAtksCritNat.size();
    }

    public Map<String, Integer> getElemSumDmg() {
        return elemSumDmg;
    }

    public int getSumDmg() {
        int sum=0;
        List<String> elems = Arrays.asList("", "fire", "shock", "frost");
        for (String elem : elems){
            sum+=elemSumDmg.get(elem);
        }
        return sum;
    }

    public Date getDate() {
        return date;
    }

    public List<Integer> getListNthAtksHit() {
        return nthAtksHit;
    }

    public List<Integer> getListNthAtksMiss() {
        return nthAtksMiss;
    }

    public Integer getNAtksTot() {
        return nthAtksMiss.size()+nthAtksHit.size();
    }

    public int getNAtksHit() {
        return nthAtksHit.size();
    }

    public List<Integer> getListNthAtksCrit() {
        return nthAtksCrit;
    }

    public List<Integer> getListNthAtksCritNat() {
        return nthAtksCritNat;
    }

    @Override
    public boolean equals(Object obj) {
        boolean returnValue=false;
        if (obj != null) {
            if(Stat.class.isAssignableFrom(obj.getClass())){
                Stat obStat=(Stat)obj;
                if((obStat.uuid!=null && this.uuid!=null) && (this.uuid.equals(obStat.uuid))){
                    returnValue=true;
                }
            }
        }
        return returnValue;
    }

    @Override
    public int hashCode() {
        return this.uuid != null ? this.uuid.hashCode() : 0;
    }
}
