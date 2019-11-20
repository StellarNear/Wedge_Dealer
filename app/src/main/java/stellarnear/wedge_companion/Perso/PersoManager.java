package stellarnear.wedge_companion.Perso;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PersoManager {

    private static Map<String,Perso> currentMapPJ = null;
    private static String currentPJ = "";

    public static Perso getCurrentPJ() {
        Perso current=null;
        try {
            current=currentMapPJ.get(currentPJ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }

    public static void initPJs(Context applicationContext) {
        currentMapPJ = new HashMap<>();
        currentMapPJ.put("Wedge",new Perso(applicationContext,""));
        currentMapPJ.put("Halda",new Perso(applicationContext,"halda"));
        currentPJ="Wedge";

    }

    public static String getCurrentNamePJ() {
        return currentPJ;
    }

    public static void swap() {
        if(currentPJ.equalsIgnoreCase("wedge")){
            currentPJ="Halda";
        } else if(currentPJ.equalsIgnoreCase("halda")) {
            currentPJ="Wedge";
        }
    }

    public void getPJ(String name){
        if(name.equalsIgnoreCase("Wedge")||name.equalsIgnoreCase("Halda")){
            currentPJ=name;
        } else {
            currentPJ="Wedge";
        }
    }
}
