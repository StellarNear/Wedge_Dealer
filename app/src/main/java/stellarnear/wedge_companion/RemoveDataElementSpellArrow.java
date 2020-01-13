package stellarnear.wedge_companion;

import stellarnear.wedge_companion.Perso.PersoManager;


public class RemoveDataElementSpellArrow {
    private String targetSheet= "spell_arrow";
    private String date="-";
    private String uuid ="-";
    private String typeEvent="remove_arrow_spell";
    private String caster=PersoManager.getCurrentNamePJ();
    private String result="-";

    public RemoveDataElementSpellArrow(GetData.PairSpellUuid pairSpellUuid) {
        this.uuid=pairSpellUuid.getUuid();
    }

    public String getCaster() {
        return caster;
    }

    public String getUuid() {
        return uuid;
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

}
