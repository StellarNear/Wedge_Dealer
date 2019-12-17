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
 * Created by jchatron on 17/12/2019.
 */

public class AllCanalisationCapacities {
    private Context mC;
    private List<CanalisationCapacity> allCanalCapacities = new ArrayList<>();
    private Map<String, CanalisationCapacity> mapIdCanalcapacity =new HashMap<>();
    private Tools tools=new Tools();

    public AllCanalisationCapacities(Context mC)
    {
        this.mC = mC;
        buildKiCapacitiesList();
    }

    private void buildKiCapacitiesList() {
        try {
            InputStream is = mC.getAssets().open("canal_capacities.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("canalcapacity");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    CanalisationCapacity canalisationCapacity =new CanalisationCapacity(
                            readValue("name", element2),
                            tools.toInt(readValue("cost", element2)),
                            readValue("feat", element2),
                            readValue("descr", element2),
                            readValue("shortdescr", element2),
                            readValue("id", element2),
                            mC);
                    allCanalCapacities.add(canalisationCapacity);
                    mapIdCanalcapacity.put(canalisationCapacity.getId(), canalisationCapacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CanalisationCapacity> getAllCanalCapacitiesList(){
        return allCanalCapacities;
    }

    public CanalisationCapacity getCanalcapacity(String capacitytId) {
        CanalisationCapacity selectedCanalisationCapacity;
        try {
            selectedCanalisationCapacity = mapIdCanalcapacity.get(capacitytId);
        } catch (Exception e){  selectedCanalisationCapacity =null;  }
        return selectedCanalisationCapacity;
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
}
