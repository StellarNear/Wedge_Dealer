package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

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

import stellarnear.wedge_companion.Spells.SpellsRanksManager;
import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 02/02/2018.
 */

public class AllResources {
    private Context mC;
    private AllAbilities allAbilities;
    private AllMythicCapacities allMythicCapacities;
    private Map<String, Resource> mapIDRes = new HashMap<>();
    private List<Resource> listResources = new ArrayList<>();
    private SpellsRanksManager rankManager=null;
    private SharedPreferences settings;
    private Tools tools = new Tools();
    private String pjID="";

    public AllResources(Context mC,AllAbilities allAbilities,AllMythicCapacities allMythicCapacities,String pjID) {
        this.mC = mC;
        this.allAbilities=allAbilities;
        this.allMythicCapacities=allMythicCapacities;
        this.pjID=pjID;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        try {
            buildResourcesList();
            refreshMaxs();
            loadCurrent();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Load_RES","Error loading resources "+pjID+e.getMessage());
            reset();
        }
    }

    private void buildResourcesList() {
        listResources = new ArrayList<>();
        mapIDRes = new HashMap<>();
        try {
            String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
            InputStream is = mC.getAssets().open("resources"+extendID+".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("resource");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Resource res = new Resource(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            tools.toBool(readValue("testable", element2)),
                            tools.toBool(readValue("hide", element2)),
                            readValue("id", element2),
                            mC,
                            pjID);
                    listResources.add(res);
                    mapIDRes.put(res.getId(), res);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        rankManager=new SpellsRanksManager(mC,pjID);
        rankManager.setRefreshEventListener(new SpellsRanksManager.OnHighTierChange() {
            @Override
            public void onEvent() {
                buildResourcesList();
            }
        });
        for (Resource res : rankManager.getSpellTiers()){
            listResources.add(res);
            mapIDRes.put(res.getId(), res);
        }


        Resource display_spell = new Resource("Rang de sorts","Sorts",false,false,"resource_display_rank",mC,pjID);
        listResources.add(display_spell);
        mapIDRes.put(display_spell.getId(), display_spell);
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }


    public List<Resource> getResourcesList() {
        return listResources;
    }

    public List<Resource> getResourcesListDisplay() {
        List<Resource> list = new ArrayList<>();

        for (Resource res : listResources) {
            if (!res.isHidden()) {
                list.add(res);
            }
        }
        return list;
    }


    public Resource getResource(String resourceId) {
        Resource selectedResource;
        try {
            selectedResource = mapIDRes.get(resourceId);
        } catch (Exception e) {
            selectedResource = null;
        }
        return selectedResource;
    }
    private int readResource(String key) {
        int val=0;
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        int resId = mC.getResources().getIdentifier(key.toLowerCase() + "_def"+extendID, "integer", mC.getPackageName());
        try {
            val=tools.toInt(settings.getString(key.toLowerCase()+extendID, String.valueOf(mC.getResources().getInteger(resId))));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public void refreshMaxs() {
        //partie from setting
        int hpPool = readResource("resource_hp_base");
        int hpMythPerGrad=0;
        if(PersoManager.getCurrentNamePJ().equalsIgnoreCase("Wedge")){
            hpMythPerGrad=5; //champion mythique
        } else if(PersoManager.getCurrentNamePJ().equalsIgnoreCase("Halda")){
            hpMythPerGrad=4; //hierophante mythique
        }
        hpPool += hpMythPerGrad*readResource("mythic_tier");

        hpPool += allAbilities.getAbi("ability_constitution").getMod()*allAbilities.getAbi("ability_lvl").getValue();

        getResource("resource_hp").setMax(hpPool);
        getResource("resource_regen").setMax(readResource("resource_regen"));
        getResource("resource_heroic").setMax(readResource("resource_heroic"));
        getResource("resource_mythic_points").setMax(3+2*readResource("mythic_tier"));
        getResource("resource_legendary_points").setMax(readResource("legendary_points"));
        rankManager.refreshMax();
    }

    private void loadCurrent() {
        for (Resource res : listResources) {
            res.loadCurrentFromSettings();
        }
    }

    public void resetCurrent() {
        for (Resource res : listResources) {
            res.resetCurrent();
        }
    }

    public void halfSleepReset() {
        getResource("resource_mythic_points").resetCurrent();
    }

    public boolean checkSpellAvailable(Integer selected_rank) {
        return selected_rank==0 || (getResource("spell_rank_"+selected_rank)!=null && getResource("spell_rank_"+selected_rank).getCurrent()>0);
    }

    public void castSpell(Integer selected_rank) {
        getResource("spell_rank_"+selected_rank).spend(1);
    }

    public void resetRessources(){
        buildResourcesList();
    }

    public void refresh() {
        rankManager.refreshRanks();
        refreshMaxs();
        loadCurrent();
    }

    public SpellsRanksManager getRankManager() {
        return rankManager;
    }

    public void reset() {
        buildResourcesList();
        refreshMaxs();
        resetCurrent();
    }
}
