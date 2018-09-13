package stellarnear.wedge_dealer.Rolls;

import java.util.ArrayList;
import java.util.List;

public class DiceList {

    private List<Dice> diceList;
    public DiceList() {
        this.diceList=new ArrayList<>();
    }

    public void add(Dice dice){
        diceList.add(dice);
    }

    public void add(DiceList diceListToAdd){
        this.diceList.addAll(diceListToAdd.getList());
    }

    public Integer getDiceNumber(){
        return diceList.size();
    }

    public Integer getDiceNumberFromNface(int nFace){
        int size=0;
        for (Dice dice : diceList) {
            if (dice.getnFace() == nFace) {
                size += 1;
            }
        }
        return size;
    }

    public DiceList filterWithNface(int nFace){
        DiceList list = new DiceList();
        for (Dice dice : diceList) {
            if (dice.getnFace() == nFace) {
                list.add(dice);
            }
        }
        return list;
    }

    public DiceList filterWithElement(String... elementArg){
        String element = elementArg.length > 0 ? elementArg[0] : "";
        DiceList list = new DiceList();
        for (Dice dice : diceList) {
            if (dice.getElement().equalsIgnoreCase(element)) {
                list.add(dice);
            }
        }
        return list;
    }

    public DiceList filterCritable() {
        DiceList list = new DiceList();
        for (Dice dice : diceList) {
            if (dice.canCrit()) {
                list.add(dice);
            }
        }
        return list;
    }

    public List<Dice> getList() {
        return diceList;
    }

    public Integer getMin(){
        return diceList.size(); //puisque on fait 1 au min
    }

    public Integer getMax(){
        int value=0;
        for (Dice dice : diceList){
            value+=dice.getnFace();
        }
        return value;
    }

    public Integer getSum(){
        int value=0;
        for (Dice dice : diceList){
            value+=dice.getRandValue();
        }
        return value;
    }

    public Integer size(){
        return diceList.size();
    }
}
