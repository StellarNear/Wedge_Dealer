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
public class AllForms {
    private Context mC;
    private List<Form> allFormsList = new ArrayList<>();
    private Map<String,Form> mapIdForm =new HashMap<>();

    public AllForms(Context mC)
    {
        this.mC = mC;
        buildFormList();
    }

    private void buildFormList() {
        allFormsList = new ArrayList<>();
        try {

            InputStream is = mC.getAssets().open("forms.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("form");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Form form=new Form(
                            readValue("name", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            mC);
                    allFormsList.add(form);
                    mapIdForm.put(form.getId(),form);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Form> getFormList(){
        return allFormsList;
    }

    public Form getForm(String formId) {
        Form selectedForm;
        try {
            selectedForm= mapIdForm.get(formId);
        } catch (Exception e){  selectedForm=null;  }
        return selectedForm;
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
        buildFormList();
    }

    public boolean formIsActive(String formId) {
        boolean active = false;
        try {
            active = mapIdForm.get(formId).isActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }


    /*
    TODO

    vegetal
    régénération 5. Si cette forme ne peut pas bouger, la vitesse de déplacement du personnage est réduite à 1,50 m (1 case) et il perd toute autre possibilité de mouvement. Si la créature est immunisée ou possède une résistance à un élément, le personnage gagne un résistance de 20 contre cet élément.
    Si la créature est vulnérable à un élément, le personnage le devient aussi.

    Animal
    Si la créature est immunisée ou résistante contre un type d’énergie, le personnage gagne 20 points de résistance contre ce type d’énergie. En revanche, si elle est vulnérable,
     le personnage l’est aussi.

Magic
pour les souffle DD = 10+ lvl/2 + mod const   9m range

    ELEMENTAIRE
     Pour les capa tout elementaire a ca : Ce sort fonctionne comme corps élémentaire III mais il permet aussi au lanceur de sorts de se transformer en élémentaire d’air,
        d’eau, de feu ou de terre de taille TG. Ses aptitudes dépendent du type d’élémentaire choisi. De plus, le personnage est immunisé contre les saignements, les coups critiques
         et les attaques sournoises tant qu’il est sous forme élémentaire et il gagne une résistance RD 5/—.

     */

}
