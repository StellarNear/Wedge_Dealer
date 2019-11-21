package stellarnear.wedge_companion.Perso;


import android.content.Context;

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

/**
 * Created by jchatron on 26/12/2017.
 */
public class AllMythicFeats {
    private Context mC;
    private List<MythicFeat> allMythicFeatsList = new ArrayList<>();
    private Map<String,MythicFeat> mapIdMythicFeat =new HashMap<>();
    private String pjID="";

    public AllMythicFeats(Context mC,String pjID)
    {
        this.mC = mC;
        this.pjID=pjID;
        buildFeatsList();
    }

    private void buildFeatsList() {
        allMythicFeatsList = new ArrayList<>();
        mapIdMythicFeat =new HashMap<>();
        try {
            InputStream is = mC.getAssets().open("mythicfeats"+pjID+".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("feat");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    MythicFeat feat=new MythicFeat(
                            readValue("name", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            mC,
                            pjID);
                    allMythicFeatsList.add(feat);
                    mapIdMythicFeat.put(feat.getId(),feat);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MythicFeat> getMythicFeatsList(){
        return allMythicFeatsList;
    }

    public MythicFeat getMythicFeat(String featId) {
        MythicFeat selectedFeat;
        try {
            selectedFeat= mapIdMythicFeat.get(featId);
        } catch (Exception e){  selectedFeat=null;  }
        return selectedFeat;
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e){
            return "";
        }
    }

    public void reset() {
        buildFeatsList();
    }

    public boolean mythicFeatsIsActive(String id) {
        boolean val=false;
        try {
            val= getMythicFeat(id).isActive();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return val;
    }
}
