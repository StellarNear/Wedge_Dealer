package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.PreparationSpellsAlertDialog;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TinyDB;
import stellarnear.wedge_companion.Tools;


public class BuildPreparedSpellList extends AppCompatActivity {

    private static BuildPreparedSpellList instanceWedge=null;


    private List<String> listIDsSpellsPrepared=new ArrayList<>();
    public SpellList allPreparedSpells = null;
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
                            getValue("rm",element2),
                            getValue("save_type",element2),
                            tools.toInt(getValue("rank",element2)),
                            mC));
                }
            }

        } catch (Exception e) {e.printStackTrace();}

    }

    public SpellList getSpellList(){
        if(allPreparedSpells==null){
            buildPreparedList();
        }
        return allPreparedSpells;
    } //pas besoin de clonner la liste car on clone apres le spell

    private void buildPreparedList() {
        List<String> listIDs= myDB.getPreparedSpellsListIDs("localSavePreparedSpellsIDs");
        allPreparedSpells=new SpellList();
        for(String id : listIDs){
            allPreparedSpells.add(new Spell(allSpells.getNormalSpellFromID(id)));
        }
    }

    public void removeSpellFromPreparedList(Spell spellToRemove){
        allPreparedSpells.remove(spellToRemove);
        for(String spellId:listIDsSpellsPrepared){
            if(spellId.equalsIgnoreCase(spellToRemove.getID())){
                listIDsSpellsPrepared.remove(spellId);
                break; //on break pour pas remove si on a plusieur fois le meme sort préparé
            }
        }
        savePreparedList();
    }


    private void savePreparedList(){
        myDB.putPreparedSpellsListIDs("localSavePreparedSpellsIDs",listIDsSpellsPrepared);
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



    public PreparationSpellsAlertDialog makePopupSelectSpellsToPrepare(final Context mC) {
        PreparationSpellsAlertDialog selectSpellPopup = new PreparationSpellsAlertDialog(mC);
        return selectSpellPopup;
    }

}
