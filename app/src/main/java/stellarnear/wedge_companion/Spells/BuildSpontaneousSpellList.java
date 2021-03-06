package stellarnear.wedge_companion.Spells;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.Tools;


public class BuildSpontaneousSpellList extends AppCompatActivity {

    private static BuildSpontaneousSpellList instanceHada = null;

    public SpellList allSpells = null;
    private Tools tools = Tools.getTools();


    private BuildSpontaneousSpellList(Context mC) {  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        this.allSpells = new SpellList();
        addSpells(mC, "");
        addSpells(mC, "Mythic");
    }

    public static BuildSpontaneousSpellList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instanceHada == null) {
            instanceHada = new BuildSpontaneousSpellList(mC);
        }
        return instanceHada;
    }

    public static void resetSpellList() {
        instanceHada = null;
    }

    private void addSpells(Context mC, String mode) {
        try {
            InputStream is = mC.getAssets().open("spells" + mode + "_halda.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("spell");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    allSpells.add(new Spell(getValue("id", element2),
                            tools.toBool(getValue("mythic", element2)),
                            tools.toBool(getValue("from_mystery", element2)),
                            tools.toBool(getValue("from_mystery_bonus", element2)),
                            getValue("normalSpellId", element2),
                            getValue("name", element2),
                            getValue("descr", element2),
                            getValue("shortdescr", element2),
                            getValue("type", element2),
                            tools.toInt(getValue("n_sub_spell", element2)),
                            getValue("dice_type", element2),
                            tools.toDouble(getValue("n_dice_per_lvl", element2)),
                            tools.toInt(getValue("cap_dice", element2)),
                            getValue("dmg_type", element2),
                            getValue("flat_dmg", element2),
                            tools.toInt(getValue("flat_cap", element2)),
                            getValue("range", element2),
                            getValue("contact", element2),
                            getValue("area", element2),
                            getValue("cast_time", element2),
                            getValue("duration", element2),
                            getValue("compo", element2),
                            getValue("compo_m", element2),  //on s'en fou pour Halda car ignore les compo mais pour l'objet Spell
                            getValue("rm", element2),
                            getValue("save_type", element2),
                            tools.toInt(getValue("rank", element2)),
                            BuildSpontaneousMetaList.getInstance(mC).getMetaList(),
                            mC));
                }
            }
            Collections.sort(allSpells.asList(), nameComparator); //order by name alphabeticly
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Comparator<Spell> nameComparator = new Comparator<Spell>() {
        public int compare(Spell s1, Spell s2) {
            String spellName1 = s1.getName().toUpperCase();
            String spellName2 = s2.getName().toUpperCase();
            return spellName1.compareTo(spellName2);
        }
    };

    public SpellList getSpellList() {
        return allSpells;
    } //pas besoin de clonner la liste car on clone apres le spell


    private String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

}
