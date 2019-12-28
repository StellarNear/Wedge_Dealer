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
import java.util.Arrays;
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
    private Inventory inventory;
    private Context mC;
    private Tools tools=new Tools();
    private String pjID="";

    public AllAbilities(Context mC,Inventory inventory,String pjID) {
        this.mC = mC;
        this.inventory=inventory;
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
                    Ability abi;
                    if(!readValue("id", element2).equalsIgnoreCase("ability_reduc_elem")) {
                        abi = new Ability(
                                readValue("name", element2),
                                readValue("shortname", element2),
                                readValue("type", element2),
                                readValue("descr", element2),
                                tools.toBool(readValue("testable", element2)),
                                tools.toBool(readValue("focusable", element2)),
                                readValue("id", element2),
                                mC);
                    } else {
                        abi = new ElementalReducAbility(
                                readValue("name", element2),
                                readValue("shortname", element2),
                                readValue("type", element2),
                                readValue("descr", element2),
                                tools.toBool(readValue("testable", element2)),
                                tools.toBool(readValue("focusable", element2)),
                                readValue("id", element2),
                                mC);
                    }
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
            val=tools.toInt(settings.getString( key.toLowerCase()+extendID, String.valueOf(mC.getResources().getInteger(resId))));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public void refreshAllAbilities() {
        for (Ability abi : listAbilities) {
            int val = 0;
            List<String> allBasicAbi = Arrays.asList("ability_force","ability_dexterite","ability_constitution","ability_sagesse","ability_intelligence","ability_charisme");
            if(allBasicAbi.contains(abi.getId())) {
                val = readAbility(abi.getId()+"_base"); //on prend que la valeur de base + augement perma le reste est faut au niveau du perso avec le stuff
                val += readAbility(abi.getId()+"_augment");
            } else if (abi.getId().equalsIgnoreCase("ability_lvl") && !pjID.equalsIgnoreCase("")){ //pour les autre que wedge !
                switch (pjID){
                    case "halda":
                        val=PersoManager.getMainPJLevel()-2;
                        break;
                    case "sylphe":
                        val=2 + (int) Math.round((PersoManager.getMainPJLevel()-1)*0.75 - 0.25);
                        break;
                    case "rana":
                        val=2 + (int) Math.round((PersoManager.getMainPJLevel()-2-1)*0.75 - 0.25);
                        break;
                }
            }else {
                val = readAbility(abi.getId());
            }
            if(abi.getId().equalsIgnoreCase("ability_rm")){
                int valRMInvent = inventory.getAllEquipments().getAbiBonus(abi.getId());
                if(valRMInvent>val){
                    val=valRMInvent;
                }
            } else {
                val += inventory.getAllEquipments().getAbiBonus(abi.getId());
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
