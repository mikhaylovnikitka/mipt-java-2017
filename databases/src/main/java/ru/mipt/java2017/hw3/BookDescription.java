package ru.mipt.java2017.hw3;

class BookDescription {

    String title;
    String[] authors;

    BookDescription(String title, String authors) {
        this.title = title;
        String[] splitted = authors.split(",");
        for (int iter = 0; iter < splitted.length; ++iter) {
            splitted[iter] = splitted[iter].trim();
        }
        this.authors = splitted;
    }
}
