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
    private AllFeats allFeats;
    private AllCapacities allCapacities;
    private Map<String, Resource> mapIDRes = new HashMap<>();
    private List<Resource> listResources = new ArrayList<>();
    private SpellsRanksManager rankManager=null;
    private SharedPreferences settings;
    private Tools tools = Tools.getTools();
    private String pjID="";

    public AllResources(Context mC,AllFeats allFeats,AllAbilities allAbilities,AllCapacities allCapacities,String pjID) {
        this.mC = mC;
        this.allFeats=allFeats;
        this.allAbilities=allAbilities;
        this.allCapacities=allCapacities;
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
            // Partie from assets
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

        // Partie sort
        Resource displaySpellResource = getResource("resource_display_rank");
        if(displaySpellResource!=null){ //ca veut dire que dans asset resource on a souhaité cette resource
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
        }

        // Partie from capacities
        addCapacitiesResources();

        if(pjID.equalsIgnoreCase("halda")){
            Resource resourceCanalTriger = new Resource("Canalisation Programmable","Trigger Canal",false,true,"resource_heal_trigger",mC,pjID);
            resourceCanalTriger.setMax(1);
            listResources.add(resourceCanalTriger);
            mapIDRes.put(resourceCanalTriger.getId(), resourceCanalTriger);
        }
    }

    private void addCapacitiesResources() {
        for(Capacity cap : allCapacities.getAllCapacitiesList()){
            if ((cap.getDailyUse()>0|| cap.isInfinite()) && cap.isActive() && getResource(cap.getId().replace("capacity_", "resource_"))==null){
                //on test si elle est pas deja presente pour la pertie rebuild  de refreshCapaListResources
                boolean testable = true; boolean hide=false;
                if(cap.getId().equalsIgnoreCase("capacity_lynx_eye")){testable=false;hide=true;}
                else if(cap.getId().equalsIgnoreCase("capacity_animal_form")){testable=false;hide=true;}
                Resource capaRes = new Resource(cap.getName(),cap.getShortname(),testable,hide,cap.getId().replace("capacity_","resource_"),mC,pjID);
                capaRes.setFromCapacity(cap);
                listResources.add(capaRes);
                mapIDRes.put(capaRes.getId(), capaRes);
            }
        }
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
        if(pjID.equalsIgnoreCase("")){ //c'est wedge
            hpMythPerGrad=5; //champion mythique
        } else if(pjID.equalsIgnoreCase("halda")){
            hpMythPerGrad=4; //hierophante mythique
        }
        if(allFeats.featIsActive("feat_robust")){ hpPool += 3+allAbilities.getAbi("ability_lvl").getValue();}
        hpPool += hpMythPerGrad*readResource("mythic_tier");
        hpPool += allAbilities.getAbi("ability_constitution").getMod()*allAbilities.getAbi("ability_lvl").getValue();
        getResource("resource_hp").setMax(hpPool);

        try {
            getResource("resource_regen").setMax(readResource("resource_regen"));
        } catch (Exception e) { }
        try {
            getResource("resource_mythic_points").setMax(3+2*readResource("mythic_tier"));
        } catch (Exception e) { }
        try {
            getResource("resource_legendary_points").setMax(readResource("resource_legendary_points"));
        } catch (Exception e) { }

        if(allFeats.featIsActive("feat_heroic_recovery") && getResource("resource_heroic_recovery")!=null){
            try {
                getResource("resource_heroic_recovery").setMax(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(getResource("resource_heroic_recovery")!=null){
            try {
                getResource("resource_heroic_recovery").setMax(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(rankManager!=null){rankManager.refreshMax();}

        for(Resource resource : listResources){
            if(resource.isFromCapacity()){
                resource.refreshFromCapacity();
            }
        }
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
        for (Resource res : listResources) {
            if(!res.isSpellResource()) {
                res.resetCurrent();
            }
        }
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
        if(rankManager!=null){rankManager.refreshRanks();}
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

    public void refreshCapaListResources() {
        List<Resource> tempListIterate = new ArrayList<>(listResources);

        for(Resource res : tempListIterate){
            if(res.isFromCapacity() && !allCapacities.capacityIsActive(res.getId().replace("resource_", "capacity_"))){  //si la capacité n'est plus active on remove la resource
                listResources.remove(res);
                mapIDRes.remove(res.getId(),res);
            }
        }
        addCapacitiesResources();
    }
}
