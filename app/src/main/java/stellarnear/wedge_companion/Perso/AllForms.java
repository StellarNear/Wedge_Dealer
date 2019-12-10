package stellarnear.wedge_companion.Perso;


import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.Attack.Attack;
import stellarnear.wedge_companion.FormSpell.FormPower;
import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */
public class AllForms {
    private Context mC;
    private Form currentForm =null;

    private List<Form> allFormsList = new ArrayList<>();
    private Map<String,Form> mapIdForm =new HashMap<>();

    private List<FormCapacity> allFormsCapacitiesList= new ArrayList<>();
    private Map<String,FormCapacity> mapIdFormCapacities =new HashMap<>();

    private List<FormPower> allPowersList = new ArrayList<>();
    private Map<String, FormPower> mapIdPower =new HashMap<>();

    private AllFormsAbiModifCalculation modifCalculation=new AllFormsAbiModifCalculation();
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
                            readValue("cooldown", element2),
                            readValue("damage", element2),
                            readValue("savetype", element2),
                            readValue("powerid", element2),
                            mC);
                    allFormsCapacitiesList.add(formCapacityapacity);
                    mapIdFormCapacities.put(formCapacityapacity.getId(),formCapacityapacity);
                }
            }
            is.close();
        } catch (Exception e) {
            Log.d("FormBuilding_capa",e.getMessage());
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
        } catch (Exception e) {    Log.d("FormBuilding_power",e.getMessage());e.printStackTrace();}
    }

    private void buildFormList() {
        allFormsList = new ArrayList<>();
        mapIdForm = new HashMap<>();
        try {
            InputStream is = mC.getAssets().open("druid_forms.xml");
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
                            readValue("size", element2),
                            readValue("descr", element2),
                            readValue("vulnerability", element2),
                            readValue("resistance", element2),
                            readValue("id", element2),
                            mC);
                    addPassivesCapa(form,element2);
                    addActivesCapa(form,element2);
                    addAttacks(form,element2);
                    allFormsList.add(form);
                    mapIdForm.put(form.getId(),form);
                }
            }
            is.close();
        } catch (Exception e) {
            Log.d("FormBuilding_form",e.getMessage());
            e.printStackTrace();
        }
    }

    private void addAttacks(Form form, Element element2) {
        if(element2.getElementsByTagName("attack").getLength()>0) {
            List<Attack> allAtks = new ArrayList<>();
            NodeList nodeList = element2.getElementsByTagName("attack").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node attackNode = nodeList.item(i);
                if (attackNode.getNodeType() == Node.ELEMENT_NODE) { // on a une capa active
                    allAtks.add(new Attack(attackNode.getNodeName(),attackNode.getChildNodes().item(0).getNodeValue()));
                }
            }
            form.setListAttacks(allAtks);
        }
    }

    private void addPassivesCapa(Form form, Element element2) {
            if(element2.getElementsByTagName("passive_capacity").getLength()>0) {
                List<FormCapacity> formCapacities = new ArrayList<>();
                NodeList nodeList = element2.getElementsByTagName("passive_capacity").item(0).getChildNodes();
                Node node = nodeList.item(0);
                String allPassives = node.getNodeValue();
                List<String> passivesList = Arrays.asList(allPassives.split(","));
                for (String passiveId : passivesList) {
                    FormCapacity capa = mapIdFormCapacities.get("form_capacity_" + passiveId);
                    if (capa != null) {
                        formCapacities.add(capa);
                    }
                }
                form.setListPassiveCapacities(formCapacities);
            }
    }

    private void addActivesCapa(Form form, Element element2) {
        if(element2.getElementsByTagName("active_capacity").getLength()>0) {
            List<FormCapacity> formCapacities = new ArrayList<>();
            NodeList nodeList = element2.getElementsByTagName("active_capacity").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node capaActive = nodeList.item(i);
                if (capaActive.getNodeType() == Node.ELEMENT_NODE) { // on a une capa active
                    FormCapacity capa = mapIdFormCapacities.get("form_capacity_" + capaActive.getChildNodes().item(0).getNodeValue());
                    if (capa != null) {
                        formCapacities.add(capa);
                    }
                }
            }
            form.setListActivesCapacities(formCapacities);
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
        buildFormCapacityList();
        buildPowerList();
        buildFormList();
    }

    public boolean formIsActive(String formId) {
        boolean active = false;
        try {
            if(currentForm ==mapIdForm.get(formId)){active=true;}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }

    public boolean hasActiveForm(){
        return currentForm !=null;
    }

    public int getFormAbilityModif(String abiId){
        return modifCalculation.getCurrentAbilityModif(currentForm,abiId);
    }

    public int getFormAbilityModif(Form form,String abiId){
        if(form==null){form=currentForm;}
        return modifCalculation.getCurrentAbilityModif(form,abiId);
    }

    public Form getCurrentForm() {
        return currentForm;
    }

    public List<Form> getFormOfType(String type) {
        List<Form> listForm =new ArrayList<>();
        if(!type.equalsIgnoreCase("")) {
            for (Form form : allFormsList) {
                if (form.getType().contains(type)) { //contain pour les elemental fire water etc
                    listForm.add(form);
                }
            }
        }
        return listForm;
    }

    public void changeFormTo(Form form) {
        currentForm=form;
    }

    public String getFormAbiModText(Form... formOptional) {
        Form form = formOptional.length > 0 ? formOptional[0] : null;
        LinkedHashMap<String,String> mapIDsNames= new LinkedHashMap<>();
        mapIDsNames.put("ability_force","For"); mapIDsNames.put("ability_dexterite","Dex"); mapIDsNames.put("ability_constitution","Con"); mapIDsNames.put("ability_ca","CA");

        String result="";
        for(LinkedHashMap.Entry<String,String> entry : mapIDsNames.entrySet()) {
            int modVal=0;
            if(form==null){modVal=getFormAbilityModif(entry.getKey());}else {modVal=getFormAbilityModif(form,entry.getKey());}
            if(modVal!=0){
                if(!result.equalsIgnoreCase("")){ result+=", ";}
                String modTxt=modVal>0? "+"+modVal:String.valueOf(modVal);
                result+=entry.getValue()+modTxt;
            }
        }
        return result.equalsIgnoreCase("") ? "-" : result;
    }

    public void unform() {
        currentForm=null;
    }

    public int getResistBonus(String elemKey) {
        int res=0;
        if(currentForm!=null){
            res=currentForm.getElementResist(elemKey);
        }
        return res;
    }

    public int getMaxResistBonus() {
        int res=0;
        if(currentForm!=null){
            res=currentForm.getMaxElementResist();
        }
        return res;
    }


    /*
    TODO

    vegetal
    TODO régénération 5. Si cette forme ne peut pas bouger, la vitesse de déplacement du personnage est réduite à 1,50 m (1 case) et il perd toute autre possibilité de mouvement. Si la créature est immunisée ou possède une résistance à un élément, le personnage gagne un résistance de 20 contre cet élément.

Magic
pour les souffle DD = 10+ lvl/2 + mod const   9m range

    ELEMENTAIRE
     Pour les capa tout elementaire a ca : Ce sort fonctionne comme corps élémentaire III mais il permet aussi au lanceur de sorts de se transformer en élémentaire d’air,
        d’eau, de feu ou de terre de taille TG. Ses aptitudes dépendent du type d’élémentaire choisi. De plus, le personnage est immunisé contre les saignements, les coups critiques
         et les attaques sournoises tant qu’il est sous forme élémentaire et il gagne une résistance TODO RD 5/—.

     */

}
