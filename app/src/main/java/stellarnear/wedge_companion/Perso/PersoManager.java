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
        currentMapPJ.put("wedge",new Perso(applicationContext,""));
        currentMapPJ.put("halda",new Perso(applicationContext,"halda"));
        currentPJ="wedge";
    }

    public static String getCurrentNamePJ() {
        return currentPJ;
    }

    public void switchPJ(String name){
        if(name.equalsIgnoreCase("wedge")||name.equalsIgnoreCase("halda")){
            currentPJ=name;
        } else {
            currentPJ="wedge";
        }
    }



}
