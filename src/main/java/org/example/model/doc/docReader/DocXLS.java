package org.example.model.doc.docReader;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.example.data.mydata.DDocXLS;
import org.example.model.properties.ServerProperties;
import org.example.model.workingFiles.IWorkingFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DocXLS implements IDocReader {

    private DDocXLS docXLS;

    @Inject
    private IWorkingFiles workingFiles;

    @Override
    public String getFullName() {
        return docXLS.getFullName();
    }

    @Override
    public void setDoc(String data) {
        Jsonb jsonb = JsonbBuilder.create();
        docXLS = jsonb.fromJson(data, DDocXLS.class);
    }

    @Override
    public void saveFile() throws Exception {

        ArrayList<String> strBytes = new ArrayList<>(
                Arrays.asList(
                        docXLS.getBytes()
                                .replace("[","")
                                .replace("]","")
                                .replace(" ", "")
                                .split(","))
        );

        ArrayList<Byte> bytes = new ArrayList<>();

        for (String e : strBytes) {
            bytes.add((byte) Integer.parseInt(e));
        }

        File customDir = new File(ServerProperties.getProperty("filepath"));
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        String doc_path = customDir.getCanonicalPath() + File.separator + docXLS.getFullName();
        if (! workingFiles.writeFile(ArrayUtils.toPrimitive(bytes.toArray(new Byte[0])), doc_path)) {
            throw new Exception("Ошибка при сохранении файла");
        }
    }





}
