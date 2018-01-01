package ru.mipt.java2017.hw3;

import java.io.IOException;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mipt.java2017.hw3.models.Author;
import ru.mipt.java2017.hw3.models.Book;
import ru.mipt.java2017.hw3.models.Book_Author;

public class DatabaseUpdater {
    private final static Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);
    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;

    private static void setTitle(Integer id, String name) {
        entityManager.find(Book.class, id).setTitle(name);
    }

    private static void databaseAccess(String jdbc_url) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.url", jdbc_url);
        entityManagerFactory = Persistence.createEntityManagerFactory("my_books", properties);
        entityManager = entityManagerFactory.createEntityManager();
        Runtime.getRuntime().addShutdownHook(new Thread(entityManagerFactory::close));
    }

    private static void shutdown() {
        entityManagerFactory.close();
    }

    private static <T> List<T> getElements(Class<T> objectT) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(objectT);
        query.from(objectT);
        return entityManager.createQuery(query).getResultList();
    }

    private static void updateBooks(Map<Long, BookDescription> isbn_description) {
        ArrayList<Long> presented_isbn = new ArrayList<>();
        List<Book> current_books = getElements(Book.class);

        for (Book book : current_books) {
            logger.error(book.getTitle());
            String database_name = book.getTitle();
            if (isbn_description.get(book.getIsbn()) == null) {
                logger.warn("There are some books not presented in excel");
                continue;
            }
            String excel_name = isbn_description.get(book.getIsbn()).title;
            if (!excel_name.equals(database_name)) {
                setTitle(book.getId(), excel_name);
            }
            presented_isbn.add(book.getIsbn());
        }
        if (presented_isbn.size() != isbn_description.size()) {
            logger.error("There are not presented books");
        }
        entityManager.getTransaction().begin();
        for (Long excel_isbn : isbn_description.keySet()) {
            if (!presented_isbn.contains(excel_isbn)) {
                Book book_to_add = new Book();
                book_to_add.setTitle(isbn_description.get(excel_isbn).title);
                book_to_add.setIsbn(excel_isbn);
                entityManager.persist(book_to_add);
            }
        }
        entityManager.getTransaction().commit();
    }

    private static void updateTableAuthors(Map<Long, BookDescription> isbn_description) {
        Set<String> uniqueAuthors = new HashSet<>();
        for (BookDescription descriptionBook : isbn_description.values()) {
            for (String author : descriptionBook.authors) {
                if (!uniqueAuthors.contains(author)) {
                    uniqueAuthors.add(author);
                }
            }
        }
        entityManager.getTransaction().begin();
        for (String author : uniqueAuthors) {
            Author newRow = new Author();
            newRow.setName(author);
            entityManager.persist(newRow);
        }
        entityManager.getTransaction().commit();
    }

    private static void updateTableBooksAuthors(Map<Long, BookDescription> isbn_description) {
        Map<String, Integer> book_title = getBookTitle();
        Map<String, Integer> author_name = getAuthorName();
        for (BookDescription descriptionBook : isbn_description.values()) {
            Integer bookTitleId = book_title.get(descriptionBook.title);
            logger.debug(descriptionBook.title);
            int count = 1;
            entityManager.getTransaction().begin();
            for (String author : descriptionBook.authors) {
                Integer author_id = author_name.get(author);
                Book_Author book_author = new Book_Author(bookTitleId, author_id, count++);
                logger.info("Add to Books_Authors: {}, {}, {}", book_author.getBookId(),
                        book_author.getAuthorId(), book_author.getNum());
                entityManager.persist(book_author);
            }
            entityManager.getTransaction().commit();
        }
    }

    private static Map<String, Integer> getBookTitle() {
        List<Book> books = getElements(Book.class);
        Map<String, Integer> book_title_to_id = new HashMap<>();
        for (Book book : books) {
            book_title_to_id.put(book.getTitle(), book.getId());
        }
        return book_title_to_id;
    }

    private static Map<String, Integer> getAuthorName() {
        List<Author> authors = getElements(Author.class);
        Map<String, Integer> author_name_to_id = new HashMap<>();
        for (Author author : authors) {
            author_name_to_id.put(author.getName(), author.getId());
        }
        return author_name_to_id;
    }

    public static void main(String[] args) throws IOException {
        String connection_string = args[0];
        String description_file = args[1];
        String output_file = args[2];

        databaseAccess(connection_string);
        ExcelReader excel_reader = ExcelReader.createProvider(description_file);
        ExcelWriter excel_writer = ExcelWriter.createProvider(output_file);

        Map<Long, BookDescription> isbn_description = excel_reader.getAllBooks();

        if (!isbn_description.isEmpty()) {
            updateBooks(isbn_description);
            updateTableAuthors(isbn_description);
            updateTableBooksAuthors(isbn_description);

            List<Book> books = getElements(Book.class);
            List<Author> authors = getElements(Author.class);
            List<Book_Author> books_authors = getElements(Book_Author.class);

            excel_writer.writeDatabase(books, authors, books_authors);
        }

        shutdown();
    }
}
