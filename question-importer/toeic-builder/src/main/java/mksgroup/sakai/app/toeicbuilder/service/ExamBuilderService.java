/**
 * Copyright 2018, MKS GROUP.
 */
package mksgroup.sakai.app.toeicbuilder.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mksgroup.java.common.CHARA;
import mksgroup.java.common.CommonUtil;
import mksgroup.java.common.Constant;
import mksgroup.java.common.FileUtil;
import mksgroup.java.poi.PoiUtil;
import mksgroup.sakai.app.toeicbuilder.RootApplication;
import mksgroup.sakai.app.toeicbuilder.ex.FormatException;

/**
 * @author ThachLN
 */
public class ExamBuilderService {
    private static final String METADATA_JSON = "metadata.json";

    private static final String DIRECTIONS_HTML = "directions.html";

    /** For logging. */
    private static Logger LOG = LoggerFactory.getLogger(RootApplication.class);
    
    /** Template from the CLASSPATH . */
    // private final String TOEIC_TEMPLATE = "/templates/Template_TOEIC.xlsx";

    private String inFolder;
    // private String rootUrl;

    /** Folder contains file Excel and 7 folders "part1" ~ "part2". It contains / at the end. */
    private String outFolder = null;
    // private String outputFilePath;

    
    /** Map data to replace rootURL or rootUrl . */
    Map<String, Object> mapUrlValue = new HashMap<>();
    
    /** mapLocalValue is used to check local media. */
    Map<String, Object> mapLocalValue = new HashMap<>();
    /**
     * @param folder input folder contains folder "media" with includes 7 parts of the TOEIC Exam.
     * @param rootUrl the URL Path of Resources in Sakai which contains the media folder.
     * @param outFolder empty folder will contains the result of building. It contain the correct file .html, .json and
     *            media.
     */
    public ExamBuilderService(String folder, String rootUrl, String outFolder) {
        // Reformat the path separator
        folder = folder.replace("\\", "/");
        outFolder = outFolder.replace("\\", "/");

        // Make sure inFolder is end with '/'
        this.inFolder = folder.endsWith("/") ? folder : (folder + "/");
        this.outFolder = outFolder.endsWith("/") ? outFolder : (outFolder + "/");
        // Remove / at the end from the rootUrl
        
        String pRootUrl = rootUrl.endsWith("/") ? rootUrl.substring(0, rootUrl.length() - 1) : rootUrl;
        
        // Initial some data
        
        mapUrlValue.put("rootURL", pRootUrl);
        mapUrlValue.put("rootUrl", pRootUrl);
        
        mapLocalValue.put("rootURL", inFolder);
        mapLocalValue.put("rootUrl", inFolder);
    }

    /**
     * Main processing.
     * @return
     * @throws IOException 
     * @throws FormatException 
     */
    public String parse() throws IOException, FormatException {
        final int NPART = 7;

        // Workbook wb = null;
        // Load Excel template
        // wb = PoiUtil.loadWorkbookByResource(TOEIC_TEMPLATE);

        // Step 1
        // Process file begin.html
        processBegin();
        
        // Step 2: Processing directions.html, metadata.json in part1 ~ part7
        String partInFolder;
        String partOutFolder;
        String directionsInPath;
        String metaDataInPath;
        String directionsInContent;
        String metaDataInContent;
        
        String directionsOutContent;
        String metaDataOutContent;
        
        String directionsOutPath;
        String metaDataOutPath;

        for (int i = 1; i <= NPART; i++) {
            partInFolder = inFolder + "media/part" + i + CHARA.STR_RIGHTSLASH;
            
            // Skip folder if not existed
            if (!(new File(partInFolder).exists())) {
                continue;
            }

            partOutFolder = outFolder + "media/part" + i + CHARA.STR_RIGHTSLASH;
            
            directionsInPath = partInFolder + DIRECTIONS_HTML;
            metaDataInPath =  partInFolder + METADATA_JSON;
            
            // Read the content of files
            directionsInContent = FileUtil.getContent(new File(directionsInPath), Constant.DEF_ENCODE);
            metaDataInContent = FileUtil.getContent(new File(metaDataInPath), Constant.DEF_ENCODE);
            
            // Replace the variable rootURL or rootUrl
            directionsOutContent = CommonUtil.formatPattern(directionsInContent, mapUrlValue);
            metaDataOutContent = CommonUtil.formatPattern(metaDataInContent, mapUrlValue);
            
            // Copy all media files in each part
            copyMedia(partInFolder, partOutFolder);

            directionsOutPath = partOutFolder + DIRECTIONS_HTML;
            metaDataOutPath =  partOutFolder + METADATA_JSON;
            
            FileUtil.saveFile(directionsOutPath, directionsOutContent);
            FileUtil.saveFile(metaDataOutPath, metaDataOutContent);
            
        }
        // Copy folder 'common'
        copyMedia(inFolder + "media/common", outFolder + "media/common");
        
        // Step 3: Processing the Excel file
        String outputExcelFilePath = processExcelFile(inFolder);
        
        return outputExcelFilePath;

        // Final step: write the Excel file
        // PoiUtil.writeExcelFile(wb, outputFilePath);
    }

    /**
     * [Give the description for method].
     * @param folder this folder contains only one file Excel.
     * @return Filename
     * @throws IOException 
     * @throws FormatException 
     */
    private String processExcelFile(String folder) throws IOException, FormatException {
        LOG.info(String.format("Processing Excel file in folder '%s'", folder));
        List<Path> listExcelFiles = listExcelFiles(Paths.get(folder));
        
        int size = (listExcelFiles != null) ? listExcelFiles.size() : 0;
        
        if (size == 0) {
            LOG.error("Excel file not found!");

            return null;
        } else if (size > 1) {
            LOG.error("There are many Excel files in the folder! Please check hidden files.");

            return CHARA.BLANK;
        } else {
            Path filePath = listExcelFiles.get(0);
            
            Workbook wb = PoiUtil.loadWorkbook(new FileInputStream(filePath.toFile()));
            String fileName = filePath.getFileName().toString();

            wb = processContentFileExcel(wb);
            
            String outputFileExcelPath = outFolder + fileName;
            PoiUtil.writeExcelFile(wb, outputFileExcelPath);
            
            return outputFileExcelPath;
        }
    }

    List<Path> listExcelFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{xls,xlsx}")) {
            Path fileName;
            for (Path entry: stream) {
                fileName = entry.getFileName();
                if (entry.toFile().isHidden()) {
                    LOG.info(String.format("Skip hidden file '%s'", entry));
                } else {
                    result.add(entry);
                }
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }

    /**
     * Fill literal "${rootURL}".
     * @param wb contains sheet Part1 ~ Part7
     * @return
     * @throws IOException
     * @throws FormatException 
     */
    private Workbook processContentFileExcel(Workbook wb) throws IOException, FormatException {
        
        String sheetName;
        Sheet sheet;
        // valid sheet indexes: <=7 or < 8
        for (int i = 1; i < 8; i++) {
            sheetName = "Part" + i;
            
            // Skip hidden sheets
            if (wb.isSheetHidden(wb.getSheetIndex(sheetName))) {
                continue;
            }
            
            LOG.info(String.format("Processing sheet '%s'", sheetName));
            sheet = wb.getSheet(sheetName);
            
            
            
            // Check column 1 or 2 is "Question"
            String questionCell = (String) PoiUtil.getValue(sheet, "A1");
            int headerQuestionCol = "Question".equals(questionCell) ? 0 : 1;
            
            processSheet(sheet, headerQuestionCol);
        }
        
        return wb;
    }

    /**
     * Scan all text in specified column in the sheet to replace the variable.
     * @param sheet
     * @param colIdx
     * @throws IOException 
     * @throws FormatException 
     */
    private void processSheet(Sheet sheet, int colIdx) throws IOException, FormatException {
        int lastRowNum = sheet.getLastRowNum();
        
        String questionText;
        String questionTextLocalPath;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonLocalObj;
        String imageLocalPath;
        String audioLocalPath;
        Row row;
        Object objective;
        String objectiveText;
        String sheetName;
        int objectiveColIdx;
        // Variables supports to validate objective value.
        int prevB = 1; // In the first row, the objective is ?.1
        int prevC = 0; // In the first row, the objective is ?.1. It means c = 0
        // objective value a.b.c
        int a;
        int b;
        int c;
        for (int rowIdx = 1; rowIdx <= lastRowNum; rowIdx++) {
            row = sheet.getRow(rowIdx);
            // Check column "Objective" in Part3, Part4, Part6, Part7 to make sure not null
            sheetName = sheet.getSheetName();
            if ("Part3, Part4, Part6, Part7".contains(sheetName)) {
                // objective = row.getCell(colIdx + 18).getRichStringCellValue().getString();
                objectiveColIdx = colIdx + 18;
                objective = PoiUtil.getValue(row, objectiveColIdx); // There are 18 columns from column "Question" to "Objective".
                
                
                // Value of Objective in Excel can be numeric or string.
                // objective = row.getCell(objectiveColIdx).getStringCellValue();

                // Check the patter d.d or d.d.d
                if ((objective == null) || (!objective.toString().matches("\\d\\.\\d+(\\.\\d){0,1}?"))) {

//                    LOG.error(String.format("Objective at sheet '%s', row '%d', col '%d' is not valid format: '%s'.", sheet.getSheetName(), rowIdx + 1, colIdx + 1, objective));
                    throw new FormatException(String.format("Objective at sheet '%s', row '%d', col '%c' is not valid format: '%s'.", sheet.getSheetName(), rowIdx + 1, 'A' + objectiveColIdx, objective));
                } else {
                    objectiveText = objective.toString();
                    // Check value of Objective
                    // Sheet "Part3" has Objective: 3.1 -> 3.1.1 -> 3.1.2; 3.2-> 3.2.1 -> ....
                    // Sheet "Part4" has Objective= 
                    // Rules:
                    // Check value of Objective with partIndex (get the last digit in sheet name), objective value a.b.c, rowIdx
                    // a = partIndex => sheetName.endsWith(a);
                    // b >= b of previous b => max(previous b) <= current b;
                    // c = previous c + 1; for objective pattern a.b, c means 0.

                    // Debug
                    if (rowIdx == 37) {
                        // 
                        LOG.info("objectiveText=" + objectiveText);
                    }
                    // Get a, b, c
                    String[] objectiveItems = objectiveText.split("\\.");
                    if (objectiveItems.length < 2) {
                        // Error
                        throw new FormatException(String.format("Objective at sheet '%s', row '%d', col '%c' is not valid value in [a.?.?]: '%s'.", sheet.getSheetName(), rowIdx + 1, 'A' + objectiveColIdx, objectiveText));
                    }
                    a = Integer.parseInt(objectiveItems[0]);
                    b = Integer.parseInt(objectiveItems[1]);
                    c = (objectiveItems.length == 3) ? Integer.parseInt(objectiveItems[2]) : 0;
                
                    // Check a
                    if (!sheetName.endsWith(String.valueOf(a))) {
                        LOG.error(String.format("Objective(a.b.c)=(%d, %d, %d).", a, b, c));
                        throw new FormatException(String.format("Objective at sheet '%s', row '%d', col '%c' is not valid value in [a.?.?]: '%s'.", sheet.getSheetName(), rowIdx + 1, 'A' + objectiveColIdx, objective));
                    }

                    // Check b
                    if (b == prevB || b == prevB + 1) {
                        // Valid
                    } else {
                        LOG.error(String.format("Objective(a.b.c)=(%d, %d, %d).", a, b, c));
                        throw new FormatException(String.format("Objective at sheet '%s', row '%d', col '%c' is not valid value in [?.b.?]: '%s'.", sheet.getSheetName(), rowIdx + 1, 'A' + objectiveColIdx, objective));
                    }
                    
                    // Check c
                    if (c == 0 || c == prevC + 1) {
                        // Valid
                    } else {
                        LOG.error(String.format("Objective(a.b.c)=(%d, %d, %d).", a, b, c));
                        throw new FormatException(String.format("Objective at sheet '%s', row '%d', col '%c' is not valid value in [?.?.c]: '%s'.", sheet.getSheetName(), rowIdx + 1, 'A' + objectiveColIdx, objective));
                    }
                    
                    // Determine prevB, prevC
                    prevB = b;
                    prevC = c;
                }
            }

            questionText = (String) PoiUtil.getValue(sheet, rowIdx, colIdx);
            
            // Check valid of json: 
            // {"image": "${rootURL}/media/part1/1.png",
            // "audio": "${rootURL}/media/part1/1.mp3"}
            
            // Check if the question is json text
            if (questionText == null) {
                break;
            }

            if (questionText.trim().startsWith("{")) {
                // Check local media
                questionTextLocalPath = CommonUtil.formatPattern(questionText, mapLocalValue);

                jsonLocalObj = mapper.readTree(questionTextLocalPath);
                imageLocalPath = (jsonLocalObj.get("image") != null) ? jsonLocalObj.get("image").textValue() : null;
                audioLocalPath = (jsonLocalObj.get("audio") != null) ? jsonLocalObj.get("audio").textValue() : null;
                
                if ((CommonUtil.isNNandNB(imageLocalPath)) && (!new File(imageLocalPath).exists())) {
                    throw new IOException(String.format("Media in sheet '%s', row '%d', col '%d' '%s' is not existed.", sheet.getSheetName(), rowIdx + 1, colIdx + 1, imageLocalPath));
                }
                
                if ((CommonUtil.isNNandNB(audioLocalPath)) && (!new File(audioLocalPath).exists())) {
                    throw new IOException(String.format("Media in sheet '%s', row '%d', col '%d' '%s' is not existed.", sheet.getSheetName(), rowIdx + 1, colIdx + 1, audioLocalPath));
                }
            }

            questionText = CommonUtil.formatPattern(questionText, mapUrlValue);
            PoiUtil.setContent(sheet, rowIdx, colIdx, questionText);
        }
        
    }

    private void copyMedia(String partInFolder, String partOutFolder) throws IOException {
        // Check out folder
        File fOutFolder = new File(partOutFolder);
        if (!fOutFolder.exists() || (!fOutFolder.isDirectory())) {
            fOutFolder.mkdirs();
        }

        Path inPath = Paths.get(partInFolder);
        Path outPath = Paths.get(partOutFolder);
        FileSystemUtils.copyRecursively(inPath, outPath);
    }

    /**
     * [Give the description for method].
     * @throws IOException 
     */
    private void processBegin() throws IOException {
        String begineFilePath = inFolder + "media/begin.html";

        File file = new File(begineFilePath);
        String beginFileContent = FileUtil.getContent(file, Constant.DEF_ENCODE);

        beginFileContent = CommonUtil.formatPattern(beginFileContent, mapUrlValue);

        // Write the result of begin.html
        String mediaOutFolder = outFolder + "media";
        Boolean createdFolder = FileUtil.mkdir(mediaOutFolder);
        
        if (createdFolder == null || createdFolder) {
            FileUtil.saveFile(mediaOutFolder + "/begin.html", beginFileContent);
        } else {
            LOG.warn(String.format("Could not created folder '%s'", mediaOutFolder));
            throw new IOException(String.format("Could not created folder '%s'", mediaOutFolder));
        }

    }

}
