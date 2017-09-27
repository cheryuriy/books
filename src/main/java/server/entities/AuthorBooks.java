package server.entities;


import java.util.Set;

public class AuthorBooks {
    private Author author;
    private Set<Book> books;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
