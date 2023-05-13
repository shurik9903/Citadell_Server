package org.example.model.doc.docReader;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.data.mydata.*;
import org.example.model.properties.ServerProperties;
import org.example.model.report.Report;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;

import java.io.*;
import java.util.*;

public class DocXLS implements IDocReader {

    private DDocXLS docXLS;

    private final IFileUtils fileUtils = new FileUtils();

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
    public void saveFile(String savePath) throws Exception {
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

        int colLast = outSheet.getRow(0).getLastCellNum();
        int rowLast = outSheet.getLastRowNum();

        outSheet.getRow(0).createCell(colLast).setCellValue("Анализированное сообщение");
        outSheet.getRow(0).createCell(colLast + 1).setCellValue("Вероятность");
        outSheet.getRow(0).createCell(colLast + 2).setCellValue("Обновить");
        outSheet.getRow(0).createCell(colLast + 3).setCellValue("Отчет");

        for (int rowNum = 1; rowNum <= rowLast; ++rowNum){
            outSheet.getRow(rowNum).createCell(colLast + 2).setCellValue("false");
            outSheet.getRow(rowNum).createCell(colLast + 3).setCellValue("false");
        }

        File customDir = new File(savePath);
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        String doc_path = customDir.getCanonicalPath() + File.separator + docXLS.getFullName();

        if (! fileUtils.writeFile(outWorkbook, doc_path)) {
            throw new Exception("Ошибка при сохранении файла");
        }

        if (! fileUtils.writeFile("[]", doc_path + ".json")) {
            throw new Exception("Ошибка при сохранении файла");
        }

        origFile.close();
    }

    @Override
    public void updateDoc(String docPath, String docData, String userID) throws Exception {
//        try {

            FileInputStream file = new FileInputStream(docPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Jsonb jsonb = JsonbBuilder.create();
            DDocData data = jsonb.fromJson(docData, DDocData.class);

            ArrayList<DReport> reports = fileUtils.getReportFile(docPath);


                Row row = sheet.getRow(Integer.parseInt(data.getIndex()));

                int colLast = row.getLastCellNum()-1;

                if (data.getType().equals("report")) {
                    row.getCell(colLast).setCellValue(data.getSelect());

                    DReport dReport = new DReport();
                    dReport.setRowNum(data.getIndex());
                    dReport.setUserID(userID);
                    dReport.setMessage(data.getMessage());

                    reports.add(dReport);

                    if (! fileUtils.writeFile(jsonb.toJson(reports), docPath+".json")) {
                        throw new Exception("Ошибка при сохранении файла");
                    }
                }
                if (data.getType().equals("update"))
                    row.getCell((colLast - 1)).setCellValue(data.getSelect());



            if (! fileUtils.writeFile(workbook, docPath)) {
                throw new Exception("Ошибка при сохранении файла");
            }

            workbook.close();
            file.close();
//        } catch (Exception e){
//            throw new Exception("Ошибка при изменении файла");
//        }
    }



    @Override
    public String parser(String loadPath, int start, int number) throws Exception {

        ++start;

        if (start < 2)
            start = 2;

        if (number < 1)
            number = 25;

        try {
            FileInputStream file = new FileInputStream(loadPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, ArrayList<String>> rows = new HashMap<>();
            ArrayList<String> title = new ArrayList<>();

            int i = 0;
            for (Row row : sheet) {
                ++i;


                if (i > start + number - 1) break;
                if (i == 1) {
                    for (int cellNum=0; cellNum<row.getLastCellNum(); ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null)
                            title.add("");
                        else
                            title.add(cell.getStringCellValue());
                    }
                } else {
                    if (i < start)
                        continue;

                    rows.put(i, new ArrayList<>());
                    for (int cellNum=0; cellNum<row.getLastCellNum(); ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null)
                            rows.get(i).add("");
                        else
                            rows.get(i).add(cell.getStringCellValue());
                    }
                }
            }

            file.close();

            return JsonbBuilder.create().toJson(new DExcel(sheet.getPhysicalNumberOfRows(), rows, title)) ;

        }catch (Exception e){
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
    }



}
