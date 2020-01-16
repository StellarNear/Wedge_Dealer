package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.PreparationSpellsAlertDialog;
import stellarnear.wedge_companion.TinyDB;
import stellarnear.wedge_companion.Tools;


public class BuildPreparedSpellList extends AppCompatActivity {

    private static BuildPreparedSpellList instanceWedge=null;

    public SpellList preparedSpells = null;
    public SpellList allSpells = null;

    private Tools tools=new Tools();
    private TinyDB myDB;


    public static BuildPreparedSpellList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instanceWedge==null) {
            instanceWedge = new BuildPreparedSpellList(mC);
        }
        return instanceWedge;
    }

    public static void resetSpellList() {
        instanceWedge = null;
    }

    private BuildPreparedSpellList(Context mC){  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        this.allSpells=new SpellList();
        addSpells(mC,"");
        //addSpells(mC,"Mythic");
        myDB=new TinyDB(mC);
    }

    private void addSpells(Context mC, String mode) {
        try {
            InputStream is = mC.getAssets().open("spells"+mode+".xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("spell");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    allSpells.add(new Spell(getValue("id",element2),
                            tools.toBool(getValue("mythic",element2)),
                            tools.toBool(getValue("from_mystery",element2)),
                            getValue("normalSpellId",element2),
                            getValue("name",element2),
                            getValue("descr",element2),
                            getValue("shortdescr",element2),
                            getValue("type",element2),
                            tools.toInt(getValue("n_sub_spell",element2)),
                            getValue("dice_type",element2),
                            tools.toDouble(getValue("n_dice_per_lvl",element2)),
                            tools.toInt(getValue("cap_dice",element2)),
                            getValue("dmg_type",element2),
                            getValue("flat_dmg",element2),
                            tools.toInt(getValue("flat_cap",element2)),
                            getValue("range",element2),
                            getValue("contact",element2),
                            getValue("area",element2),
                            getValue("cast_time",element2),
                            getValue("duration",element2),
                            getValue("compo",element2),
                            getValue("compo_m",element2),
                            getValue("rm",element2),
                            getValue("save_type",element2),
                            tools.toInt(getValue("rank",element2)),
                            mC));
                }
            }

        } catch (Exception e) {e.printStackTrace();}

    }

    public SpellList getPreparedSpellList(){
        if(preparedSpells ==null){
            loadFromSavePreparedList();
        }
        return preparedSpells;
    } //pas besoin de clonner la liste car on clone apres le spell

    private void loadFromSavePreparedList() {
        preparedSpells= myDB.getSpellList("localSavePreparedSpells");
    }

    public void removePreparedSpell(Spell spell){
        if(spell.isBinded()){
            for(Spell spellSearchSub : preparedSpells.asList()){
                if(spellSearchSub.getID().equalsIgnoreCase(spell.getID().replace("_sub",""))){
                    preparedSpells.remove(spellSearchSub);
                    break;
                }
            }
        } else {
            preparedSpells.remove(spell);
        }
        savePreparedList();
    }

    private void savePreparedList(){
        myDB.putSpellList("localSavePreparedSpells",preparedSpells);
    }


    private String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e){
            return "";
        }
    }

    public PreparationSpellsAlertDialog makePopupSelectSpellsToPrepare(final Context mC,String mode) {
        this.preparedSpells=null;
        PreparationSpellsAlertDialog selectSpellPopup = new PreparationSpellsAlertDialog(mC, this,mode);
        return selectSpellPopup;
    }

    public SpellList getAllSpells() {
        return allSpells;
    }

    public SpellList getOldFullSleepPreparedSpellList() {
        return myDB.getSpellList("localSaveFullSleepPreparedSpells");
    }

    public void saveListFromPreparationAlert(SpellList preparedSpells,String mode) {
        Collections.sort(preparedSpells.asList(),nameComparator); //order by name
        this.preparedSpells= preparedSpells;
        if(mode.equalsIgnoreCase("sleep")){myDB.putSpellList("localSaveFullSleepPreparedSpells",preparedSpells);}
        savePreparedList();
    }

    public static Comparator<Spell> nameComparator = new Comparator<Spell>() {
        public int compare(Spell s1, Spell s2) {
            String spellName1 = s1.getName().toUpperCase();
            String spellName2 = s2.getName().toUpperCase();
            return spellName1.compareTo(spellName2);
        }};
}
