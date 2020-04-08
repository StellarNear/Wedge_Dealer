package stellarnear.wedge_companion.Perso;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by jchatron on 06/04/2020.
 */

public class AllAdditionalsInfos {
    private Context mC;
    private List<Info> listInfos = new ArrayList<>();
    private String pjID = "";

    public AllAdditionalsInfos(Context mC, String pjID) {
        this.mC = mC;
        this.pjID = pjID;
        buildInfosList();
    }

    private void buildInfosList() {
        listInfos = new ArrayList<>();
        try {
            String extendID = pjID.equalsIgnoreCase("") ? "" : "_" + pjID;
            InputStream is = mC.getAssets().open("infos" + extendID + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("info");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Info info = new Info(
                            readValue("name", element2),
                            readValue("type", element2),
                            readValue("value", element2));
                    listInfos.add(info);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<Info> getListInfos() {
        return listInfos;
    }

    public class Info {
        private String name;
        private String type;
        private String value;

        public Info(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }
    }

}
