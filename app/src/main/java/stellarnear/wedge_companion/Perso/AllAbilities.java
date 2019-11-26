package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.Tools;


/**
 * Created by jchatron on 05/01/2018.
 */

public class AllAbilities {

    private Map<String, Ability> mapIDAbi = new HashMap<>();
    private List<Ability> listAbilities= new ArrayList<>();
    private Context mC;
    private Tools tools=new Tools();
    private String pjID="";

    public AllAbilities(Context mC,String pjID) {
        this.mC = mC;
        this.pjID=pjID;
        buildAbilitiesList();
        refreshAllAbilities();
    }

    private void buildAbilitiesList() {
        mapIDAbi = new HashMap<>();
        listAbilities= new ArrayList<>();
        try {
            InputStream is = mC.getAssets().open("abilities.xml");  //ici on change pas de xml halda a les meme abi mais des valeurs differentes
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("ability");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Ability abi = new Ability(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            tools.toBool(readValue("testable", element2)),
                            tools.toBool(readValue("focusable", element2)),
                            readValue("id", element2),
                            mC);
                    listAbilities.add(abi);
                    mapIDAbi.put(abi.getId(), abi);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    private int readAbility(String key) {
        int val=0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        int resId = mC.getResources().getIdentifier( key.toLowerCase() + "_def"+extendID, "integer", mC.getPackageName());
        try {
            val=tools.toInt(settings.getString( key.toLowerCase()+pjID, String.valueOf(mC.getResources().getInteger(resId))));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public void refreshAllAbilities() {
        for (Ability abi : listAbilities) {
            int val = 0;
            if (abi.getId().equalsIgnoreCase("ability_ca")) {
                val = 10+readAbility("ability_ca_stuff")+readAbility("ability_ca_other");
            } else if (abi.getId().equalsIgnoreCase("ability_bmo")) {
                val = getAbi("ability_force").getMod();
            } else if (abi.getId().equalsIgnoreCase("ability_dmd")) {
                val = getAbi("ability_force").getMod() + 10 + getAbi("ability_dexterite").getMod() ;
            } else if (abi.getId().equalsIgnoreCase("ability_init")) {
                val = getAbi("ability_dexterite").getMod();
            } else if (abi.getId().equalsIgnoreCase("ability_equipment")) {
                //on laisse à 0 le nombre de piece de stuff est calculer au niveau du perso
            } else {
                val = readAbility(abi.getId());
            }
            abi.setValue(val);
        }
    }

    public List<Ability> getAbilitiesList(String... type){
        String typeSelected = type.length > 0 ? type[0] : "";  //parametre optionnel type
        List<Ability> list= new ArrayList<>();
        if (typeSelected.equalsIgnoreCase("base")
                ||typeSelected.equalsIgnoreCase("general")
                ||typeSelected.equalsIgnoreCase("def")
                ||typeSelected.equalsIgnoreCase("advanced")){
            for(Ability abi : listAbilities){
                if(abi.getType().equalsIgnoreCase(typeSelected)){
                    list.add(abi);
                }
            }
        } else {
            list=listAbilities;
        }
        return list;
    }

    public Ability getAbi(String abiId){
        Ability selecteAbi;
        try {
            selecteAbi=mapIDAbi.get(abiId.toLowerCase());
        } catch (Exception e){  selecteAbi=null;  }
        return selecteAbi;
    }

    public void reset() {
        buildAbilitiesList();
        refreshAllAbilities();
    }
}
