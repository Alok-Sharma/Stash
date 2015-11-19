package aloksharma.ufl.edu.stash;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Alok on 11/18/2015.
 */
public class BankMappingHelper {
    Context context;
    HashMap<String, String> namesToCode;
    HashMap<String, String> codeToNames;

    public BankMappingHelper(Context context) {
        this.context = context;
        namesToCode = new HashMap<>();
        codeToNames = new HashMap<>();
        parseStringArray();
    }

    public void parseStringArray() {
        String[] stringArray = context.getResources().getStringArray(R.array.bankNamesMapping);
        for (String entry : stringArray) {
            String[] splitResult = entry.split(";", 2);
            namesToCode.put(splitResult[0], splitResult[1]);
            codeToNames.put(splitResult[1], splitResult[0]);
        }
    }

    public HashMap<String, String> getNamesToCodeMap() {
        return namesToCode;
    }

    public HashMap<String, String> getCodeToNamesMap() {
        return codeToNames;
    }

    public String getBankCode(String bankName) {
        return namesToCode.get(bankName);
    }

    public String getBankName(String bankCode) {
        return codeToNames.get(bankCode);
    }
}
