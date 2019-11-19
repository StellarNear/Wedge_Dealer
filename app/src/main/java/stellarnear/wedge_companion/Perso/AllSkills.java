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
 * Created by jchatron on 10/01/2018.
 */
public class AllSkills {
    private Context mC;
    private List<Skill> allSkillsList = new ArrayList<>();
    private Map<String,Skill> mapIdSkill=new HashMap<>();
    public AllSkills(Context mC)
    {
        this.mC = mC;
        buildSkillsList();
    }

    private void buildSkillsList() {
        allSkillsList = new ArrayList<>();
        mapIdSkill=new HashMap<>();
        try {
            InputStream is = mC.getAssets().open("skills.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("skill");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Skill skill=new Skill(
                            readValue("name", element2),
                            readValue("abilityDependence", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            mC);
                    allSkillsList.add(skill);
                    mapIdSkill.put(skill.getId(),skill);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Skill> getSkillsList(){
        return allSkillsList;
    }

    public Skill getSkill(String skillId) {
        Skill selectedSkill;
        try {
            selectedSkill=mapIdSkill.get(skillId);
        } catch (Exception e){  selectedSkill=null;  }
        return selectedSkill;
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

    public void refreshAllVals() {
        for (Skill skill : allSkillsList){
            skill.refreshVals();
        }
    }

    public void reset() {
        buildSkillsList();
    }
}
