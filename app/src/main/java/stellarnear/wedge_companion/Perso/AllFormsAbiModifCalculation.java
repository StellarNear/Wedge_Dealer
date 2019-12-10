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

import stellarnear.wedge_companion.FormSpell.FormPower;
import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 09/12/2019.
 */
public class AllFormsAbiModifCalculation {
    private Tools tools=new Tools();

    public AllFormsAbiModifCalculation()
    {
    }

    public int getCurrentAbilityModif(Form currentForm,String abiId){
        int val=0;
        try {
            switch (currentForm.getType()){
                case "animal":
                    switch (currentForm.getSize()){
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
                case "magic":
                    switch (currentForm.getSize()){
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
                    switch (currentForm.getSize()){
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
}
