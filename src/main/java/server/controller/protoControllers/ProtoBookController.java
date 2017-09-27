package server.controller.protoControllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.entities.Author;
import server.entities.Book;
import server.functions.DefaultRestResponse;
import server.functions.FindOne;
import server.protobuf.CodingBook;
import server.repositories.AuthorRepository;
import server.repositories.BookRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import server.protobuf.BookAuthorProtos.BookProto;
import server.protobuf.BookAuthorProtos.BooksProto;

@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/protobuf/books")
public class ProtoBookController {
     private final AtomicLong counter = new AtomicLong();
    @Autowired
  private BookRepository brepo;
    @Autowired
    private AuthorRepository arepo;

  @RequestMapping(value = "", method = GET)
  public Object PROgetBooks(@RequestParam(value = "number", defaultValue = "0",required = false) int number,@RequestParam(value = "size", defaultValue = "5",required = false) int size, @RequestParam(value = "property", defaultValue = "", required = false)  String sort, @RequestParam(value = "direction", defaultValue = "",required = false) String direct) {
      if(size<1 | size>200) {
          size = 5;
          System.out.println("No size of page provided");
      }
      if(number<0 | number>100) {
          number = 0;
          System.out.println("No number of page provided");
      }
      Direction dir= Direction.ASC;
      if (direct.equals("down") | direct.equals("DESC") | direct.equals("descending"))
          dir= Direction.DESC;
      if(!(sort.equals("publisher") | sort.equals("date") | sort.equals("id")))
          sort ="book";
      Page<Book> allBooks = brepo.findAll(new PageRequest(number, size,new Sort(dir, sort)));
      if (allBooks.getTotalElements()== 0 )
          return new DefaultRestResponse(true, "No books found.");
      BooksProto.Builder proto = BooksProto.newBuilder();
      for (Book book : allBooks) {
          for (Author author : book.getAuthors())
              author.setBooks(null);
          proto.addBooks(CodingBook.Encode(book));
      }
      return proto.build();
  }

    @RequestMapping(value = "", method = PUT)
    public Object PROaddBook(@RequestBody BookProto proto) {
      if (proto != null) {
          Book book = CodingBook.Decode(proto);
          Set<Author> authorSet= FindOne.NoRepeatAuthors(book.getAuthors());
          book.setAuthors(new HashSet<>());
          Long id= FindOne.findBook(book, brepo.findAll());
          if( id>=0L) {
              if (authorSet.isEmpty())
                return new DefaultRestResponse(true, "Such book exists!!! id=" + id);
              book = brepo.findOne(id);
          }
        if (!authorSet.isEmpty()) {
            Set<Author> newauthors = new HashSet<Author>();
            Set<Author> Allauthors = arepo.findAll();
            for (Author author : authorSet){
                id= FindOne.findAuthor(author, Allauthors);
                if (id == -1L)
                    newauthors.add(arepo.save(author));
                else
                    if (FindOne.NotThereAuthor(author,book.getAuthors()))
                        newauthors.add(arepo.findOne(id));
            }
            book.addAuthors(newauthors);
          }
          book = brepo.save(book);

          return new DefaultRestResponse(false, book.getId().toString());

      }
      return new DefaultRestResponse(true, "Expected JSON, but didn't find one.");
    }

    @RequestMapping(value = "/{Id}", method = RequestMethod.POST)
    public Object PROchangeById(@PathVariable Long Id, @RequestBody BookProto proto) {
        if( proto == null){
            return new DefaultRestResponse(true, "No book in Json was entered");
        }
        Book change = CodingBook.Decode(proto);
        Book book= brepo.findOne(Id);
        if( book != null  & change != null)
        {
            Long id= FindOne.findBook(change, brepo.findAll());
            if (id !=-1L & Id != id)
                return new DefaultRestResponse(true, "This book already exists!!! #" + id);
            book.setDate(change.getDate());
            book.setBook(change.getBook());
            book.setPublisher(change.getPublisher());
            Set<Author> authorSet = FindOne.NoRepeatAuthors(change.getAuthors());
            Set<Author> newauthors = new HashSet<Author>();
            Set<Author> Allauthors = arepo.findAll();
            if (authorSet != null)
            {
                for (Author author : authorSet){
                    id= FindOne.findAuthor(author, Allauthors);
                    if (id == -1L)
                        newauthors.add(arepo.save(author));
                    else
                        newauthors.add(arepo.findOne(id));
                }
                book.setAuthors(newauthors);
            }
            change= brepo.save(book);//
            if (change != null)
                return new DefaultRestResponse(false, "Changed book #" + Id);
            else
                return new DefaultRestResponse(true, "Couldn't change book #" + Id);
        }
        else
            return new DefaultRestResponse(true, "No book found by id= "+Id);
    }

@RequestMapping(value = "/{id}", method = RequestMethod.GET)
public Object PROgetById(@PathVariable Long id) {
    Book book= brepo.findOne(id);
    if (book == null)
        return new DefaultRestResponse(true, "No book found by id = "+id);
    for (Author author : book.getAuthors())
        author.setBooks(null);
    return CodingBook.Encode(book);
  }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE )
    public Object deleteById(@PathVariable Long id) {
        Book book= brepo.findOne(id);
        if( book != null) {
            brepo.delete(book);
            return new DefaultRestResponse(false, "deleted book #"+id);
        }
        else
            return new DefaultRestResponse(true, "No book found by id= "+id);
    }
}
