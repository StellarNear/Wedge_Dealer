package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;


public class BuildSpellList extends AppCompatActivity {

    private static BuildSpellList instanceWedge=null;
    private static BuildSpellList instanceHada=null;

    public SpellList allSpells = null;
    private Tools tools=new Tools();

    public static BuildSpellList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        BuildSpellList instance=null;
        if(PersoManager.getCurrentNamePJ().equalsIgnoreCase("wedge")){
            if (instanceWedge==null){
                instanceWedge = new BuildSpellList(mC);
            }
            instance=instanceWedge;
        } else {
            if (instanceHada==null){
                instanceHada = new BuildSpellList(mC);
            }
            instance=instanceHada;
        }
        return instance;
    }

    public static void resetSpellList() {
        instanceWedge = null;
        instanceHada = null;
    }

    private BuildSpellList(Context mC){  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        this.allSpells=new SpellList();
        addSpells(mC,"");
        addSpells(mC,"Mythic");
    }

    private void addSpells(Context mC, String mode) {
        try {
            InputStream is = mC.getAssets().open("spells"+mode+PersoManager.getPJSuffix()+".xml");

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
                            getValue("mythic",element2),
                            getValue("normalSpellId",element2),
                            getValue("name",element2),
                            getValue("descr",element2),
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
                            getValue("rm",element2),
                            getValue("save_type",element2),
                            tools.toInt(getValue("rank",element2)),
                            mC));
                }
            }

        } catch (Exception e) {e.printStackTrace();}

    }

    public SpellList getSpellList(){
        return allSpells;
    } //pas besoin de clonner la liste car on clone apres le spell

    private String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e){
            return "";
        }
    }

}
