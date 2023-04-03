package org.example.model.workingFiles;

import java.io.IOException;

public interface IWorkingFiles {
    boolean writeFile(byte[] content, String filename) throws IOException;
}
