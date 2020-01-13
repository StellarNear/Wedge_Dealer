package stellarnear.wedge_companion;

import stellarnear.wedge_companion.Perso.PersoManager;

public class RemoveDataElementAllSpellArrow {
    private String targetSheet= "spell_arrow";
    private String date="-";

    private String typeEvent="remove_all_arrow_spell";
    private String caster= PersoManager.getCurrentNamePJ();
    private String result="-";

    public RemoveDataElementAllSpellArrow() {
    }

    public String getCaster() {
        return caster;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getTargetSheet() {
        return targetSheet;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public RemoveDataElementAllSpellArrow forceCaster(String forced){
        caster=forced;
        return this;
    }

}

