package ru.mipt.java2017.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mipt.java2017.hw3.models.Author;
import ru.mipt.java2017.hw3.models.Book;
import ru.mipt.java2017.hw3.models.Book_Author;

class ExcelWriter {
    private static Logger logger = LoggerFactory.getLogger("ExcelWriter");

    private static final String[] BOOKS_COLUMNS = {"ID", "ISBN", "title", "cover"};
    private static final String[] AUTHORS_COLUMNS = {"ID", "name"};
    private static final String[] BOOKS_AUTHORS_COLUMNS = {"ID", "books_id", "authors_id", "num"};

    private final FileOutputStream fileOutputStream;

    private Workbook workbook;

    private ExcelWriter(String fileName) throws IOException {
        fileOutputStream = new FileOutputStream(new File(fileName));
        workbook = new XSSFWorkbook();
    }

    static ExcelWriter createProvider(String fileName) throws IOException {
        return new ExcelWriter(fileName);
    }

    private void fillTitle(Row row, String[] columnsName) {
        for (int i = 0; i < columnsName.length; ++i) {
            row.createCell(i);
            row.getCell(i).setCellValue(columnsName[i]);
        }
    }

    private void createRowBook(Row row, Book book) {
        row.createCell(0).setCellValue(book.getId());
        row.createCell(1).setCellValue(book.getIsbn());
        row.createCell(2).setCellValue(book.getTitle());
        row.createCell(3).setCellValue(book.getCover());
    }

    private void createRowAuthor(Row row, Author author) {
        row.createCell(0).setCellValue(author.getId());
        row.createCell(1).setCellValue(author.getName());
    }

    private void createRowBookAuthor(Row row, Book_Author book_author) {
        row.createCell(0).setCellValue(book_author.getId());
        row.createCell(1).setCellValue(book_author.getBookId());
        row.createCell(2).setCellValue(book_author.getAuthorId());
        row.createCell(3).setCellValue(book_author.getNum());
    }

    private void updateBooksTab(Collection<Book> books) {
        Sheet tab = workbook.createSheet("Books");
        Row rowTitle = tab.createRow(0);
        fillTitle(rowTitle, BOOKS_COLUMNS);
        int iter = 1;
        for (Book book : books) {
            Row curRow = tab.createRow(iter++);
            createRowBook(curRow, book);
        }
    }

    private void updateAuthorsTab(Collection<Author> authors) {
        Sheet tab = workbook.createSheet("Authors");
        Row rowTitle = tab.createRow(0);
        fillTitle(rowTitle, AUTHORS_COLUMNS);
        int iter = 1;
        for (Author author : authors) {
            Row curRow = tab.createRow(iter++);
            createRowAuthor(curRow, author);
        }
    }

    private void updateBookAuthorsTab(Collection<Book_Author> books_authors) {
        Sheet tab = workbook.createSheet("Books_Authors");
        Row rowTitle = tab.createRow(0);
        fillTitle(rowTitle, BOOKS_AUTHORS_COLUMNS);
        int iter = 1;
        for (Book_Author book_author : books_authors) {
            Row curRow = tab.createRow(iter++);
            createRowBookAuthor(curRow, book_author);
        }
    }

    void writeDatabase(Collection<Book> books, Collection<Author> authors, Collection<Book_Author> books_authors) {
        updateBooksTab(books);
        updateAuthorsTab(authors);
        updateBookAuthorsTab(books_authors);
        try {
            workbook.write(fileOutputStream);
        } catch (Exception exception) {
            logger.error("Can't write to output file");
        }
    }
}
