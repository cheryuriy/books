package server.entities;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "book_id")
  private Long id;
  private String book;
    private LocalDate date;
    private String publisher;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @ManyToMany
    @JoinTable(name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "author_id"))
    private Set<Author> authors= new HashSet<Author>();

    public Set<Author> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
    public void addAuthors(Set<Author> authors){
        this.authors.addAll(authors);
    }
    public void addAuthor(Author author){this.authors.add(author);}

    public Book() {};
    public Book(String book, String publisher, LocalDate  date) {
        this.book = book;
        this.publisher = publisher;
        this.date = date;
    }
    public Book(String book, String publisher, LocalDate  date, Set<Author> authors) {
        this.authors = authors;
        this.book = book;
        this.publisher = publisher;
        this.date = date;
    }

    public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBook() {
    return book;
  }

  public void setBook(String book) {
    this.book = book;
  }

  public LocalDate  getDate() {
    return date;
  }

  public void setDate(LocalDate  date) {

      this.date = date;
    }
}
