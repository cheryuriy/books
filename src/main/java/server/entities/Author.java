package server.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "author_id")
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate date;

    @ManyToMany(cascade = CascadeType.ALL)//(mappedBy = "authors")
    @JsonBackReference
    @JoinTable(name = "books_authors",
            joinColumns = @JoinColumn(name = "author_id", referencedColumnName = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"))
    private Set<Book> books= new HashSet<Book>();

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public void addBooks(Set<Book> books){
        this.books.addAll(books);
    }

    public void addBook(Book book){this.books.add(book);}

    public Author(String firstName, String lastName, LocalDate date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public Author( String firstName, String lastName, LocalDate date, Set<Book> books) {
        this.books = books;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public Author() { }

    public Long getId() {return id;
    }
    public void setId(Long id) {this.id = id;
    }

    public String getFirstName() {return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDate() {return date;
    }
    public void setDate(LocalDate date) {this.date = date;
    }
}
