package stellarnear.wedge_companion;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Spells.Spell;


public class PostDataElementSpellArrow {
    private String targetSheet = "spell_arrow";
    private String date = "-";
    private String uuid = "-";
    private String typeEvent = "store_arrow_spell";
    private String caster = PersoManager.getCurrentNamePJ();
    private String result = "-";

    private Spell arrowSpell = null; //pour les fleches avec sort


    /* lancement d'un sort */
    public PostDataElementSpellArrow(Spell spell) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date = formater.format(new Date());
        this.uuid = UUID.randomUUID().toString();
        this.result = spellToJSonString(spell);
    }

    private String spellToJSonString(Spell spell) {
        Gson gson = new Gson();
        return gson.toJson(spell);
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
