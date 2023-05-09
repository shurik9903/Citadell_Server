package org.example.model.doc.docReader;

import org.apache.poi.ss.formula.functions.BooleanFunction;

import java.util.regex.Pattern;

public class DocReaderFactory {

    private static boolean rexMatch(String type, String text){
        Pattern pattern = Pattern.compile("^"+type+".?$");
        return pattern.matcher(text).matches();
    }

    public static IDocReader getDocReader(String type) throws Exception {

        if (rexMatch(".xls", type)){
            return new DocXLS();
        }

        throw new Exception("Неизвестный тип файла");

    }



}
