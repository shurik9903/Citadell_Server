package org.example.model.workingFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorkingFiles implements IWorkingFiles {

    @Override
    public boolean writeFile(byte[] content, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            if (! file.createNewFile()) return false;
        }
        //! Файл перезаписывается если он уже существует
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.flush();
        fos.close();

        return true;
    }
}
