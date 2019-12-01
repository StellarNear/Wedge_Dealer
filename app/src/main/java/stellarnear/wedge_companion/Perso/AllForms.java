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

import stellarnear.wedge_companion.FormSpell.FormPower;
import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */
public class AllForms {
    private Context mC;
    private Form activeForm=null;

    private List<Form> allFormsList = new ArrayList<>();
    private Map<String,Form> mapIdForm =new HashMap<>();

    private List<FormCapacity> allFormsCapacitiesList= new ArrayList<>();
    private Map<String,FormCapacity> mapIdFormCapacities =new HashMap<>();

    private List<FormPower> allPowersList = new ArrayList<>();
    private Map<String, FormPower> mapIdPower =new HashMap<>();

    private Tools tools=new Tools();

    public AllForms(Context mC)
    {
        this.mC = mC;
        buildFormCapacityList();
        buildPowerList();
        buildFormList();
    }

    private void buildFormCapacityList() {
        allFormsCapacitiesList = new ArrayList<>();
        mapIdFormCapacities =new HashMap<>();
        try {
            InputStream is = mC.getAssets().open("druid_forms_capacities.xml");
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
                    FormCapacity formCapacityapacity=new FormCapacity(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            readValue("id", element2),
                            tools.toInt(readValue("dailyuse", element2)),
                            tools.toInt(readValue("value", element2)),
                            mC);
                    allFormsCapacitiesList.add(formCapacityapacity);
                    mapIdFormCapacities.put(formCapacityapacity.getId(),formCapacityapacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildPowerList() {
        allPowersList =new ArrayList<>();
        mapIdPower =new HashMap<>();
        try {
            InputStream is = mC.getAssets().open("power_form.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element=doc.getDocumentElement();
            element.normalize();
            NodeList nList = doc.getElementsByTagName("power");
            for (int i=0; i<nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    FormPower formPower = new FormPower(readValue("id",element2),
                            readValue("name",element2),
                            readValue("descr",element2),
                            readValue("effect",element2),
                            readValue("dmg_type",element2),
                            readValue("dice_type_type",element2),
                            tools.toDouble(readValue("n_dice_per_lvl",element2)),
                            tools.toInt(readValue("cap_dice",element2)),
                            tools.toInt(readValue("flat_dmg",element2)),
                            readValue("range",element2),
                            readValue("area",element2),
                            readValue("cast_time",element2),
                            readValue("save_type",element2),
                            mC);
                    allPowersList.add(formPower);
                    mapIdPower.put(formPower.getID(), formPower);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    private void buildFormList() {
        allFormsList = new ArrayList<>();
        mapIdForm = new HashMap<>();
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
                    addPassivesCapa(form,element2);
                    addActivesCapa(form,element2);
                    allFormsList.add(form);
                    mapIdForm.put(form.getId(),form);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addPassivesCapa(Form form, Element element2) {
        NodeList nodeList = element2.getElementsByTagName("passive_capacity").item(0).getChildNodes();
        Node node = nodeList.item(0);
        if(node.getNodeType() == Node.ELEMENT_NODE  ){
            form.getType();
            //todo boucler sur les val entre separateur "," pour add list passive capacity en allant find par le tag dans la list capa "form_capacity_+val"
        }
    }

    private void addActivesCapa(Form form, Element element2) {
        //todo boucler sur les val entre les balises primaire active_capacity pour add list passive capacity en allant find par le tag dans la list capa "form_capacity_+val"
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
        buildFormCapacityList();
        buildPowerList();
        buildFormList();
    }

    public boolean formIsActive(String formId) {
        boolean active = false;
        try {
            if(activeForm==mapIdForm.get(formId)){active=true;}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }

    public boolean hasActiveForm(){
        return activeForm!=null;
    }

    public int getAbilityModif(String abiId){
        int val=0;
        try {
            switch (activeForm.getType()){
                case "animal":
                    switch (activeForm.getSize()){
                        case "Min":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=6;
                                    break;
                                case "ability_force":
                                    val=-4;
                                    break;
                                case "ability_ca":
                                    val=1;
                                    break;
                            }
                            break;
                        case "TP":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=4;
                                    break;
                                case "ability_force":
                                    val=-2;
                                    break;
                                case "ability_ca":
                                    val=1;
                                    break;
                            }
                            break;
                        case "P":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=2;
                                    break;
                                case "ability_ca":
                                    val=1;
                                    break;
                            }
                            break;
                        case "M":
                            switch (abiId){
                                case "ability_force":
                                    val=2;
                                    break;
                                case "ability_ca":
                                    val=2;
                                    break;
                            }
                            break;
                        case "G":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=-2;
                                    break;
                                case "ability_force":
                                    val=4;
                                    break;
                                case "ability_ca":
                                    val=4;
                                    break;
                            }
                            break;
                        case "TG":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=-4;
                                    break;
                                case "ability_force":
                                    val=6;
                                    break;
                                case "ability_ca":
                                    val=6;
                                    break;
                            }
                            break;
                    }
                    break;
                case "magical":
                    switch (activeForm.getSize()){
                        case "TP":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=8;
                                    break;
                                case "ability_force":
                                    val=-2;
                                    break;
                                case "ability_ca":
                                    val=3;
                                    break;
                            }
                            break;
                        case "P":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=4;
                                    break;
                                case "ability_ca":
                                    val=2;
                                    break;
                            }
                            break;
                        case "M":
                            switch (abiId){
                                case "ability_force":
                                    val=4;
                                    break;
                                case "ability_ca":
                                    val=4;
                                    break;
                            }
                            break;
                        case "G":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=-2;
                                    break;
                                case "ability_constitution":
                                    val=2;
                                    break;
                                case "ability_force":
                                    val=6;
                                    break;
                                case "ability_ca":
                                    val=6;
                                    break;
                            }
                            break;
                    }
                    break;
                case "vegetal":
                    switch (activeForm.getSize()){
                        case "P":
                            switch (abiId){
                                case "ability_constitution":
                                    val=2;
                                    break;
                                case "ability_ca":
                                    val=2;
                                    break;
                            }
                            break;
                        case "M":
                            switch (abiId){
                                case "ability_force":
                                    val=2;
                                    break;
                                case "ability_ca":
                                    val=2;
                                    break;
                                case "ability_constitution":
                                    val=2;
                                    break;
                            }
                            break;
                        case "G":
                            switch (abiId){
                                case "ability_constitution":
                                    val=2;
                                    break;
                                case "ability_force":
                                    val=4;
                                    break;
                                case "ability_ca":
                                    val=4;
                                    break;
                            }
                            break;
                        case "TG":
                            switch (abiId){
                                case "ability_dexterite":
                                    val=-2;
                                    break;
                                case "ability_constitution":
                                    val=4;
                                    break;
                                case "ability_force":
                                    val=8;
                                    break;
                                case "ability_ca":
                                    val=6;
                                    break;
                            }
                            break;
                    }
                    break;
                case "elemental_air":
                    switch (abiId){
                        case "ability_dexterite":
                            val=6;
                            break;
                        case "ability_force":
                            val=4;
                            break;
                        case "ability_ca":
                            val=4;
                            break;
                    }
                    break;
                case "elemental_water":
                    switch (abiId){
                        case "ability_dexterite":
                            val=-2;
                            break;
                        case "ability_constitution":
                            val=8;
                            break;
                        case "ability_force":
                            val=4;
                            break;
                        case "ability_ca":
                            val=6;
                            break;
                    }
                    break;
                case "elemental_fire":
                    switch (abiId){
                        case "ability_dexterite":
                            val=6;
                            break;
                        case "ability_constitution":
                            val=4;
                            break;
                        case "ability_ca":
                            val=4;
                            break;
                    }
                    break;
                case "elemental_earth":
                    switch (abiId){
                        case "ability_dexterite":
                            val=-2;
                            break;
                        case "ability_constitution":
                            val=4;
                            break;
                        case "ability_force":
                            val=8;
                            break;
                        case "ability_ca":
                            val=6;
                            break;
                    }
                    break;
            }
        } catch (Exception e){}
        return val;
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
