package stellarnear.wedge_companion.Stats.SpellStats;

import stellarnear.wedge_companion.Spells.CalculationSpell;
import stellarnear.wedge_companion.Spells.Spell;


public class DamagesShortListElement {
    private String element;
    private int dmgSum;
    private int nMeta;
    private int rank;
    private boolean mythic;

    public DamagesShortListElement(Spell spell){
        this.element=spell.getDmg_type();
        this.dmgSum=spell.getDmgResult();
        this.rank=new CalculationSpell().currentRank(spell);
        this.nMeta=this.rank-spell.getRank(); //todo pas vrai avec la reduc sur sort mystere
        this.mythic=spell.isMyth();
    }

    public String getElement() {
        return element;
    }

    public int getDmgSum() {
        return dmgSum;
    }

    public int getRank() {
        return rank;
    }

    public int getnMeta() {
        return nMeta;
    }

    public boolean isMythic() {
        return mythic;
    }

    public void addBindedSpell(Spell spell) {
        this.dmgSum+=spell.getDmgResult();
    }
}
