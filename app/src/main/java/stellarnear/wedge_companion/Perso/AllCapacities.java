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

import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */

public class AllCapacities {
    private Context mC;
    private List<Capacity> allCapacities = new ArrayList<>();
    private Map<String, Capacity> mapIdcapacity = new HashMap<>();
    private String pjID = "";
    private Tools tools = Tools.getTools();

    public AllCapacities(Context mC, String pjID) {
        this.mC = mC;
        this.pjID = pjID;
        buildCapacitiesList();
    }

    private void buildCapacitiesList() {
        allCapacities = new ArrayList<>();
        mapIdcapacity = new HashMap<>();
        try {
            String extendID = pjID.equalsIgnoreCase("") ? "" : "_" + pjID;
            InputStream is = mC.getAssets().open("capacities" + extendID + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("capacity");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Capacity capacity = new Capacity(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            readValue("dailyuse", element2),
                            readValue("value", element2),
                            mC,
                            pjID);
                    allCapacities.add(capacity);
                    mapIdcapacity.put(capacity.getId(), capacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Capacity> getAllCapacitiesList() {
        return allCapacities;
    }

    public Capacity getcapacity(String capacitytId) {
        Capacity selectedCapacity;
        try {
            selectedCapacity = mapIdcapacity.get(capacitytId);
        } catch (Exception e) {
            selectedCapacity = null;
        }
        return selectedCapacity;
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

    public Capacity getCapacity(String id) {
        Capacity selectedCapa;
        try {
            selectedCapa = mapIdcapacity.get(id);
        } catch (Exception e) {
            selectedCapa = null;
        }
        return selectedCapa;
    }

    public void reset() {
        buildCapacitiesList();
    }

    public boolean capacityIsActive(String id) {
        boolean val = false;
        try {
            val = getCapacity(id).isActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
}
