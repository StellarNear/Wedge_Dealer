package stellarnear.wedge_companion.Attack;


import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Tools;

public class Attack {

    private String name;
    private int diceType=0;
    private int nDice=0;
    private int flatDmg=0;
    private String damageTxt;
    private FormCapacity capacity;
    private Tools tools=new Tools();

    public Attack( String name, String damageTxt){
        this.name=name;
        this.damageTxt=damageTxt;
        if(damageTxt.contains("d")){
            this.nDice=tools.toInt(damageTxt.split("d")[0]);
            this.diceType=tools.toInt(damageTxt.split("d")[1]);
        }
        if(damageTxt.contains("+")){
            this.flatDmg=tools.toInt(damageTxt.split("+")[1]);
        }
    }

    public int getDiceType() {
        return diceType;
    }

    public int getFlatDmg() {
        return flatDmg;
    }

    public int getnDice() {
        return nDice;
    }

    public String getName() {
        return name;
    }

    public String getDmgTxt() {
        return damageTxt;
    }

    public void setCapacity(FormCapacity capacity) { //TODO
        this.capacity = capacity;
    }

    public FormCapacity getCapacity() {
        return capacity;
    }

    public boolean hasPower() {
        return capacity!=null;
    }
}

