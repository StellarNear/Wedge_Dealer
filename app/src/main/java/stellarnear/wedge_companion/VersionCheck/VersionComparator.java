package stellarnear.wedge_companion.VersionCheck;

import stellarnear.wedge_companion.BuildConfig;
import stellarnear.wedge_companion.Tools;

public class VersionComparator {
    private static Tools tools = Tools.getTools();

    public VersionComparator() {
    }


    public static boolean isNewer(VersionData versionToTest, VersionData olderVersion) {
        boolean isNewer = false;
        String olderVersionName = olderVersion == null ? BuildConfig.VERSION_NAME.replace("v", "") : olderVersion.getVersion_name().replace("v", "");
        String[] olderParts = olderVersionName.split("\\.");

        Integer olderMaj=0;
        Integer olderMed=0;
        Integer olderMin=0;
        switch (olderParts.length) {
            case 3:
                olderMin =  tools.toInt(olderParts[2]);
                olderMed =  tools.toInt(olderParts[1]);
                olderMaj =  tools.toInt(olderParts[0]);
                break;
            case 2:
                olderMed =  tools.toInt(olderParts[1]);
                olderMaj =  tools.toInt(olderParts[0]);
                break;
            case 1:
                olderMaj = tools.toInt(olderParts[0]);
                break;
            default:
                break;
        }


        String versionToTestName = versionToTest.getVersion_name().replace("v", "");
        String[] toTestParts = versionToTestName.split("\\.");

        Integer testMaj=0;
        Integer testMed=0;
        Integer testMin=0;
        switch (toTestParts.length) {
            case 3:
                testMin =  tools.toInt(toTestParts[2]);
                testMed =  tools.toInt(toTestParts[1]);
                testMaj =  tools.toInt(toTestParts[0]);
                break;
            case 2:
                testMed =  tools.toInt(toTestParts[1]);
                testMaj =  tools.toInt(toTestParts[0]);
                break;
            case 1:
                testMaj =  tools.toInt(toTestParts[0]);
                break;
            default:
                break;
        }

        if(testMaj>olderMaj){
            isNewer=true;
        } else if(testMaj!=0 && testMaj==olderMaj){
            if(testMed>olderMed){
                isNewer=true;
            } else if(testMed!=0 && testMed==olderMed) {
                if(testMin>olderMin){
                    isNewer=true;
                }
            }
        }
        return isNewer;
    }
}


