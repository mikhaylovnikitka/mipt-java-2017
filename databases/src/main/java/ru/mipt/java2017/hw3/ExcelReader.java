package ru.mipt.java2017.hw3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExcelReader {
    private static final String COLUMN_TITLE = "Title";
    private static final String COLUMN_AUTHORS = "Authors";
    private static final String COLUMN_ISBN = "ISBN";
    private static Logger logger = LoggerFactory.getLogger("ExcelReader");
    private Workbook workbook;

    private ExcelReader(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(fileName));
        workbook = new XSSFWorkbook(fileInputStream);
    }

    static ExcelReader createProvider(String fileName) throws IOException {
        return new ExcelReader(fileName);
    }

    private Long myLongParser(String stringISBN) {
        StringBuilder myISBN = new StringBuilder();
        for (char symbol : stringISBN.toCharArray()) {
            if (symbol >= '0' && symbol <= '9') {
                myISBN.append(symbol);
            }
        }
        return Long.parseLong(myISBN.substring(2));
    }

    Map<Long, BookDescription> getAllBooks() {
        Map<Long, BookDescription> result = new HashMap<>();
        Sheet sheet = workbook.getSheetAt(0);
        int indexOfTitle = -1;
        int indexOfAuthors = -1;
        int indexOfIsbn = -1;
        Row names = sheet.getRow(0);
        for (int iter = 0; iter < names.getLastCellNum(); ++iter) {
            String name = names.getCell(iter).getStringCellValue();
            switch (name) {
                case COLUMN_TITLE:
                    indexOfTitle = iter;
                    break;
                case COLUMN_AUTHORS:
                    indexOfAuthors = iter;
                    break;
                case COLUMN_ISBN:
                    indexOfIsbn = iter;
                    break;
            }
        }
        if (indexOfTitle == -1 || indexOfIsbn == -1 || indexOfAuthors == -1) {
            logger.warn("Can't find column");
            return result;
        }
        for (int iter = 1; iter <= sheet.getLastRowNum(); ++iter) {
            Row row = sheet.getRow(iter);
            String curTitle = row.getCell(indexOfTitle).getStringCellValue();
            String curAuthors = row.getCell(indexOfAuthors).getStringCellValue();
            String curISBN = row.getCell(indexOfIsbn).getStringCellValue();
            Long isbn = myLongParser(curISBN);
            BookDescription book_information = new BookDescription(curTitle, curAuthors);
            result.put(isbn, book_information);
        }
        return result;
    }

}
