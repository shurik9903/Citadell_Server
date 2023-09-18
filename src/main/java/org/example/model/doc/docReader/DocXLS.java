package org.example.model.doc.docReader;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.data.mydata.*;
import org.example.model.utils.FileUtils;
import org.example.model.utils.IFileUtils;

import java.io.*;
import java.util.*;

public class DocXLS implements IDocReader {

    private DDocXLS docXLS;

    private final IFileUtils fileUtils = new FileUtils();

    private class FindCell {
        Cell cell;
        Integer typeIndex;

        FindCell(Cell cell, Integer typeIndex) {
            this.cell = cell;
            this.typeIndex = typeIndex;
        }
    }

    private FindCell getTypeCell(String type, int index, Sheet sheet) {
        Row types = sheet.getRow(0);
        Row row = sheet.getRow(index);

        return getTypeCell(type, types, row);
    }

    private FindCell getTypeCell(String type, Row types, Row row) {
        int index;
        for (Cell cell : types) {
            if (cell != null && cell.getStringCellValue().equals(type)) {
                index = cell.getColumnIndex();
                return new FindCell(row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), index);
            }
        }

        return new FindCell(null, null);
    }

    private Integer getTypeIndex(String type, Row types) {
        for (Cell cell : types) {
            if (cell != null && cell.getStringCellValue().equals(type)) {
                return cell.getColumnIndex();
            }
        }

        return null;
    }

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
                                .replace("[", "")
                                .replace("]", "")
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


        if (docXLS.getAutoSize()) {


            int rowCount = inSheet.getLastRowNum();
            int ColCount = inSheet.getRow(inSheet.getFirstRowNum()).getPhysicalNumberOfCells();

            int currentCellIndex = 0;

            for (int step = 0; step < ColCount; ++step) {

                if (currentCellIndex == 0)

                    outSheet.createRow(0).createCell(currentCellIndex).setCellValue("default");

                else

                    outSheet.getRow(0).createCell(currentCellIndex).setCellValue("default");

                ++currentCellIndex;
            }


            if (rowCount <= 0)
                throw new Exception("Ошибка при сохранении файла. Файл пуст.");

            int currentRowIndex = 1;

            if (!docXLS.getTitle()) {

                currentCellIndex = 0;

                for (int step = 0; step < ColCount; ++step) {

                    if (currentCellIndex == 0)

                        outSheet.createRow(1).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    else

                        outSheet.getRow(1).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    ++currentCellIndex;
                }
                currentRowIndex = 2;
            }

            for (Row cells : inSheet) {

                currentCellIndex = 0;

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


            int currentCellIndex = 0;

            for (int colNum = colStart - 1; colNum < colStart + colSize - 1; ++colNum) {

                if (currentCellIndex == 0)
                    outSheet.createRow(0).createCell(currentCellIndex).setCellValue("default");
                else
                    outSheet.getRow(0).createCell(currentCellIndex).setCellValue("default");

                ++currentCellIndex;
            }

            int currentRowIndex = 1;

            if (!docXLS.getTitle()) {

                currentCellIndex = 0;

                for (int colNum = colStart - 1; colNum < colStart + colSize - 1; ++colNum) {

                    if (currentCellIndex == 0)
                        outSheet.createRow(currentRowIndex).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));
                    else
                        outSheet.getRow(currentRowIndex).createCell(currentCellIndex).setCellValue("Столбец " + (currentCellIndex + 1));

                    ++currentCellIndex;
                }
                currentRowIndex = 2;
            }


            for (int rowNum = rowStart - 1; rowNum < rowStart + rowSize - 1; ++rowNum) {
                Row row = inSheet.getRow(rowNum);

                if (row == null)
                    continue;


                currentCellIndex = 0;


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

        outSheet.getRow(0).createCell(colLast).setCellValue("analysis");
        outSheet.getRow(0).createCell(colLast + 1).setCellValue("probability");
        outSheet.getRow(0).createCell(colLast + 2).setCellValue("update");
        outSheet.getRow(0).createCell(colLast + 3).setCellValue("report");
        outSheet.getRow(0).createCell(colLast + 4).setCellValue("type");

        outSheet.getRow(1).createCell(colLast).setCellValue("Анализированное сообщение");
        outSheet.getRow(1).createCell(colLast + 1).setCellValue("Вероятность");
        outSheet.getRow(1).createCell(colLast + 2).setCellValue("Обновить");
        outSheet.getRow(1).createCell(colLast + 3).setCellValue("Отчет");
        outSheet.getRow(1).createCell(colLast + 4).setCellValue("Тип");

        for (int rowNum = 2; rowNum <= rowLast; ++rowNum) {
            outSheet.getRow(rowNum).createCell(colLast + 2).setCellValue("false");
            outSheet.getRow(rowNum).createCell(colLast + 3).setCellValue("false");
            outSheet.getRow(rowNum).createCell(colLast + 4).setCellValue("0");
        }

        File customDir = new File(savePath);
        if (!customDir.exists()) {
            customDir.mkdir();
        }

        String doc_path = customDir.getCanonicalPath() + File.separator + docXLS.getFullName();

        if (!fileUtils.writeFile(outWorkbook, doc_path)) {
            throw new Exception("Ошибка при сохранении файла");
        }

        if (!fileUtils.writeFile("[]", doc_path + ".json")) {
            throw new Exception("Ошибка при сохранении файла");
        }

        origFile.close();
    }

    @Override
    public void setDataAnalysis(Object data, String docPath) throws Exception {
        try {

            FileInputStream file = new FileInputStream(docPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            DAnalysisResult analysisResults = (DAnalysisResult) data;


            for (DAnalysisResult.AnalysisRows result : analysisResults.getComments()) {
                Row row = sheet.getRow(result.getNumber() + 2);


//                Cell cell = row.getCell(row.getLastCellNum() - 4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                FindCell findCell = getTypeCell("probability", sheet.getRow(0), row);
                if (findCell.cell != null) {
                    findCell.cell.setCellValue(String.valueOf(result.getPercent()));
                } else if (findCell.typeIndex != null)
                    row.createCell(findCell.typeIndex).setCellValue(String.valueOf(result.getPercent()));

                findCell = getTypeCell("type", sheet.getRow(0), row);
                if (findCell.cell != null) {
                    findCell.cell.setCellValue(String.valueOf(result.getClass_comment()));
                } else if (findCell.typeIndex != null)
                    row.createCell(findCell.typeIndex).setCellValue(String.valueOf(result.getClass_comment()));

            }

            if (!fileUtils.writeFile(workbook, docPath)) {
                throw new Exception("Ошибка при сохранении файла");
            }

            workbook.close();
            file.close();

        } catch (Exception e) {
            throw new Exception("Ошибка при изменении файла");
        }
    }

    @Override
    public void updateDoc(String docPath, String docData, String userID) throws Exception {
        try {

            FileInputStream file = new FileInputStream(docPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Jsonb jsonb = JsonbBuilder.create();

            DDocData dDocData = jsonb.fromJson(docData, DDocData.class);


            if (dDocData.getAllSelect() != null) {

                int i = 0;
                for (Row row : sheet) {

                    ++i;

                    if (i <= 2)
                        continue;

                    FindCell findCell =
                            getTypeCell("update", sheet.getRow(0), row);

                    if (findCell.cell != null)
                        findCell.cell.setCellValue(dDocData.getAllSelect());
                }

                if (!fileUtils.writeFile(workbook, docPath)) {
                    throw new Exception("Ошибка при сохранении файла");
                }

                workbook.close();
                file.close();

                return;
            }

            ArrayList<DReport> reports = fileUtils.getReportFile(docPath);

            for (DDocData.Data data : dDocData.getData()) {

                Row row = sheet.getRow(Integer.parseInt(data.getIndex()));

                if (data.getType().equals("report")) {

                    FindCell findCell = getTypeCell("report", sheet.getRow(0), row);

                    findCell.cell.setCellValue(data.getSelect());

                    Optional<DReport> report = reports.stream().filter(dReport -> dReport.getRowNum().equals(data.getIndex())).findFirst();

                    if (report.isPresent()) {
                        report.get().setMessage(data.getMessage());
                    } else {
                        DReport dReport = new DReport();
                        dReport.setRowNum(data.getIndex());
                        dReport.setUserID(userID);
                        dReport.setMessage(data.getMessage());
                        reports.add(dReport);
                    }

                    if (!fileUtils.writeFile(jsonb.toJson(reports), docPath + ".json")) {
                        throw new Exception("Ошибка при сохранении файла");
                    }
                }

                if (data.getType().equals("update")) {
                    FindCell findCell = getTypeCell("update", sheet.getRow(0), row);
                    findCell.cell.setCellValue(data.getSelect());
                }

                if (data.getType().equals("type")) {
                    FindCell findCell = getTypeCell("type", sheet.getRow(0), row);
                    findCell.cell.setCellValue(data.getClassComment());
                }

                if (data.getType().equals("analysis")) {
                    FindCell findCell = getTypeCell("analysis", sheet.getRow(0), row);

                    if (findCell.cell != null)
                        findCell.cell.setCellValue(data.getAnalysisText());
                    else if (findCell.typeIndex != null)
                        row.createCell(findCell.typeIndex).setCellValue(data.getAnalysisText());
                }

            }

            if (!fileUtils.writeFile(workbook, docPath)) {
                throw new Exception("Ошибка при сохранении файла");
            }

            workbook.close();
            file.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка при изменении файла ");
        }
    }

    @Override
    public String parser(String loadPath) throws Exception {

        try {
            FileInputStream file = new FileInputStream(loadPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, ArrayList<String>> rows = new HashMap<>();
            ArrayList<String> title = new ArrayList<>();
            ArrayList<String> titleType = new ArrayList<>();
            Map<Integer, String> type = new HashMap<>();

            int i = 0;
            for (Row row : sheet) {
                ++i;

                if (i == 1) {
                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {

                            if (cell.getStringCellValue().equals("type")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("report")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("update")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("probability")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("analysis")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            titleType.add(cell.getStringCellValue());
                            continue;

                        }

                        titleType.add("default");
                    }
                    continue;
                }

                if (i == 2) {
                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null) {
                            title.add("");
                        } else {
                            title.add(cell.getStringCellValue());
                        }
                    }
                } else {
                    rows.put(i, new ArrayList<>());
                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null)
                            rows.get(i).add("");
                        else
                            rows.get(i).add(cell.getStringCellValue());
                    }

                    Cell cell = row.getCell(row.getLastCellNum() - 1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null)
                        type.put(i, "0");
                    else
                        type.put(i, cell.getStringCellValue());
                }
            }

            file.close();

            return JsonbBuilder.create().toJson(new DExcel(sheet.getPhysicalNumberOfRows(), rows, title, titleType, type));

        } catch (Exception e) {
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Map<String, Object>> parserSelectColumn(String loadPath, int column, int select) throws Exception {

        try {
            FileInputStream file = new FileInputStream(loadPath);
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);


            ArrayList<Map<String, Object>> rows = new ArrayList<>();

            if (column > sheet.getRow(1).getLastCellNum() - 5) {
                throw new Exception("Ошибка при разборе Excel: указанный столбец отсутствует");
            }

            int i = 0;
            for (Row row : sheet) {
                ++i;

                if (i <= 2)
                    continue;

                FindCell findCell = getTypeCell("update", sheet.getRow(0), row);
                String selectValue = findCell.cell.getStringCellValue();
                findCell = getTypeCell("type", sheet.getRow(0), row);
                String typeValue = findCell.cell.getStringCellValue();

                if ((select == 1) ||
                        (select == 2 && selectValue.equals("true"))
                        || (select == 3 && selectValue.equals("false"))) {
                    Cell cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null)
                        rows.add(new HashMap<>() {{
                            put("number", row.getRowNum());
                            put("comment", cell.getStringCellValue());
                            put("class_comment", Integer.parseInt(typeValue));
                        }});
                }

            }

            file.close();

            return rows;

        } catch (Exception e) {
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
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
            ArrayList<String> titleType = new ArrayList<>();
            Map<Integer, String> type = new HashMap<>();

            int i = 0;
            for (Row row : sheet) {
                ++i;

                if (i - 1 > start + number - 1) break;

                if (i == 1) {
                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell != null) {

                            if (cell.getStringCellValue().equals("type")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("report")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("update")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("probability")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            if (cell.getStringCellValue().equals("analysis")) {
                                titleType.add(cell.getStringCellValue());
                                continue;
                            }

                            titleType.add(cell.getStringCellValue());
                            continue;

                        }

                        titleType.add("default");
                    }
                    continue;
                }

                if (i == 2) {

                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null) {
                            title.add("");
                        } else {
                            title.add(cell.getStringCellValue());
                        }

                    }
                } else {
                    if (i < start)
                        continue;

                    rows.put(i, new ArrayList<>());


                    for (int cellNum = 0; cellNum <= row.getLastCellNum() - 1; ++cellNum) {
                        Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null)
                            rows.get(i).add("");
                        else
                            rows.get(i).add(cell.getStringCellValue());
                    }


                    Cell cell = row.getCell(row.getLastCellNum() - 1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null)
                        type.put(i, "0");
                    else
                        type.put(i, cell.getStringCellValue());
                }
            }

            file.close();

            return JsonbBuilder.create().toJson(new DExcel(sheet.getPhysicalNumberOfRows(), rows, title, titleType, type));

        } catch (Exception e) {
            System.out.println("Ошибка при разборе Excel: " + e.getMessage());
            throw new Exception("Error: " + e.getMessage());
        }
    }


}
