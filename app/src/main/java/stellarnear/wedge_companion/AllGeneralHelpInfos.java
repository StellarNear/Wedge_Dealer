package stellarnear.wedge_companion;

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
 * Created by jchatron on 16/02/2018.
 */

public class AllGeneralHelpInfos {
    List<GeneralHelpInfo> listGeneralHelpInfos;

    public AllGeneralHelpInfos(Context mC) {
        listGeneralHelpInfos = new ArrayList<>();
        try {
            InputStream is = mC.getAssets().open("help.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("help");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    GeneralHelpInfo help = new GeneralHelpInfo(
                            readValue("name", element2),
                            readValue("descr", element2),
                            readValue("id", element2));
                    listGeneralHelpInfos.add(help);
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

    public List<GeneralHelpInfo> getListGeneralHelpInfos() {
        return listGeneralHelpInfos;
    }
}
