package stellarnear.wedge_companion.Perso;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PersoManager {

    private static Map<String,Perso> currentMapPJ = new HashMap<>();
    private static String currentPJ = "";

    public static Perso getCurrentPJ() {
        Perso current=null;
        try {
            current = currentMapPJ.get(currentPJ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }

    public static void initPJs(Context applicationContext) {
        currentMapPJ.put("Wedge",new Perso(applicationContext,""));
        currentMapPJ.put("Halda",new Perso(applicationContext,"halda"));
        currentMapPJ.put("Sylphe",new Perso(applicationContext,"sylphe"));
        currentMapPJ.put("Ràna",new Perso(applicationContext,"rana"));
        currentPJ="Wedge";
    }

    public static int getMainPJLevel() {
        int levelWedge = currentMapPJ.get("Wedge").getAbilityScore("ability_lvl") ;
        return levelWedge;
    }

    public static String getCurrentNamePJ() {
        return currentPJ;
    }

    public static void swap() {
        if(currentPJ.equalsIgnoreCase("Wedge")){
            currentPJ="Halda";
        } else if(currentPJ.equalsIgnoreCase("Halda")) {
            currentPJ="Wedge";
        } else if(currentPJ.equalsIgnoreCase("Sylphe")) {
            currentPJ="Ràna";
        } else if(currentPJ.equalsIgnoreCase("Ràna")) {
            currentPJ="Sylphe";
        } else {
            currentPJ="Wedge";
        }
    }

    public static void setMainPJ() {
        if(currentPJ.equalsIgnoreCase("Sylphe")){
            currentPJ="Wedge";
        } else if(currentPJ.equalsIgnoreCase("Ràna")) {
            currentPJ="Halda";
        }
    }

    public static void setPetPJ() {
        if(currentPJ.equalsIgnoreCase("Wedge")){
            currentPJ="Sylphe";
        } else if(currentPJ.equalsIgnoreCase("Halda")) {
            currentPJ="Ràna";
        }
    }

    public static void allSleep() {
        currentMapPJ.get("Wedge").sleep();
        currentMapPJ.get( "Halda").sleep();
        currentMapPJ.get("Sylphe").sleep();
        currentMapPJ.get("Ràna").sleep();
    }

    public static void allHalfSleep() {
        currentMapPJ.get("Wedge").halfSleep();
        currentMapPJ.get("Halda").halfSleep();
        currentMapPJ.get("Sylphe").halfSleep();
        currentMapPJ.get("Ràna").halfSleep();
    }

    public static void loadFromSaveAllPJs() {
        currentMapPJ.get("Wedge").loadFromSave();
        currentMapPJ.get("Halda").loadFromSave();
        currentMapPJ.get("Sylphe").loadFromSave();
        currentMapPJ.get("Ràna").loadFromSave();
    }

    public static void hardReset() {
        currentMapPJ.get("Wedge").hardReset();
        currentMapPJ.get("Halda").hardReset();
        currentMapPJ.get("Sylphe").hardReset();
        currentMapPJ.get("Ràna").hardReset();
    }

    public static String getPJSuffix() {
        String currentID=getCurrentPJ().getID();
        return currentID.equalsIgnoreCase("") ? "" : "_"+currentID;
    }

    public static String getPJPrefix() {
        String currentID=getCurrentPJ().getID();
        return currentID.equalsIgnoreCase("") ? "" : currentID+"_";
    }


}
