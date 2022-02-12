package utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

public class StringOperations {

    public static Boolean isImportRequired(String importStr) {
        String[] temp = importStr.split("\\.");

        if(temp.length > 2) {
            if((temp[0] + "." + temp[1]).equals("java.lang")) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }

    public static String getNameFromImport(String importPath) {
        String[] temp = importPath.split("\\.");
        return temp[temp.length -1];
    }

    public static String toCamelCase(String strToCaseSwap) {
        return CaseUtils.toCamelCase(strToCaseSwap, false, ' ', '_');
    }

    public static String toCapitalCamelCase(String strToCaseSwap) {
        String camelCase = StringOperations.toCamelCase(strToCaseSwap);
        return StringUtils.capitalize(camelCase);
    }

}
