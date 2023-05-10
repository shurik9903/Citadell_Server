package org.example.model.doc.docReader;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.data.mydata.DDocXLS;
import org.example.model.properties.ServerProperties;
import org.example.model.workingFiles.IWorkingFiles;
import org.example.model.workingFiles.WorkingFiles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class DocXLS implements IDocReader {

    private DDocXLS docXLS;

    private final IWorkingFiles workingFiles = new WorkingFiles();

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
    public void saveFile(String userName) throws Exception {
        if (docXLS == null)
            throw new Exception("Ошибка при сохранении файла");

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

        InputStream origFile = new ByteArrayInputStream(ArrayUtils.toPrimitive(bytes.toArray(new Byte[0])));
        Workbook inWorkbook = new XSSFWorkbook(origFile);
        Workbook outWorkbook = new XSSFWorkbook();
        Sheet inSheet = inWorkbook.getSheetAt(0);
        Sheet outSheet = outWorkbook.createSheet();

        if(docXLS.getAutoSize()) {

            int rowCount = inSheet.getLastRowNum();
            int ColCount = inSheet.getRow(inSheet.getFirstRowNum()).getPhysicalNumberOfCells();

            if (rowCount <= 0)
                throw new Exception("Ошибка при сохранении файла. Файл пуст.");

            int currentRowIndex = 0;

            if(!docXLS.getTitle()) {

                int currentCellIndex = 0;

                for (int step = 0; step < ColCount; ++step) {

                    if (currentCellIndex == 0)

                        outSheet.createRow(0).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    else

                        outSheet.getRow(0).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    ++currentCellIndex;
                }
                currentRowIndex = 1;
            }

            for (Row cells : inSheet) {

                int currentCellIndex = 0;

                Iterator<Cell> cellIterator = cells.cellIterator();

                while (cellIterator.hasNext()) {

                    String cellData = cellIterator.next().toString();

                    if (currentCellIndex == 0)

                        outSheet.createRow(currentRowIndex).createCell(currentCellIndex).setCellValue(cellData);

                    else

                        outSheet.getRow(currentRowIndex).createCell(currentCellIndex).setCellValue(cellData);

                    ++currentCellIndex;
                }
                ++currentRowIndex;
            }
        } else {

            int rowStart = Integer.parseInt(docXLS.getSizeTable().getRowStart());
            int colStart = Integer.parseInt(docXLS.getSizeTable().getColStart());
            int rowSize = Integer.parseInt(docXLS.getSizeTable().getRowSize());
            int colSize = Integer.parseInt(docXLS.getSizeTable().getColSize());

            if (rowStart < 1 || colStart < 1 || rowSize < 1 || colSize < 1)
                throw new Exception("Начало строки/столбца и длина строки/столбца не может быть меньше 1");


            int currentRowIndex = 0;

            if (!docXLS.getTitle()) {

                int currentCellIndex = 0;

                for (int colNum = colStart - 1; colNum < colStart + colSize - 1; ++colNum) {

                    if (currentCellIndex == 0)

                        outSheet.createRow(currentRowIndex).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    else

                        outSheet.getRow(currentRowIndex).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));


                ++currentCellIndex;
            }
            currentRowIndex = 1;
        }


            for (int rowNum = rowStart - 1; rowNum < rowStart + rowSize - 1; ++rowNum) {
                Row row = inSheet.getRow(rowNum);

                if (row == null)
                    continue;


                int currentCellIndex = 0;


                for (int colNum = colStart - 1; colNum < colStart + colSize - 1; ++colNum) {
                    Cell cell = row.getCell(colNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    if (cell != null) {
                        if (currentCellIndex == 0)

                            outSheet.createRow(currentRowIndex).createCell(currentCellIndex).setCellValue(cell.toString());

                        else

                            outSheet.getRow(currentRowIndex).createCell(currentCellIndex).setCellValue(cell.toString());

                    }
                    ++currentCellIndex;
                }
                ++currentRowIndex;
            }
        }

        File customDir = new File(ServerProperties.getProperty("filepath") + File.separator + userName);
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        String doc_path = customDir.getCanonicalPath() + File.separator + docXLS.getFullName();

        if (! workingFiles.writeFile(outWorkbook, doc_path)) {
            throw new Exception("Ошибка при сохранении файла");
        }

        origFile.close();
    }





}
