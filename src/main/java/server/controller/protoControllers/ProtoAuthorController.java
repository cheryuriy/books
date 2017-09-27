package server.controller.protoControllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.entities.Author;
import server.entities.AuthorBooks;
import server.entities.Book;
import server.functions.DefaultRestResponse;
import server.functions.FindOne;
import server.protobuf.BookAuthorProtos.AuthorsProto;
import server.protobuf.BookAuthorProtos.AuthorProto;
import server.protobuf.CodingAuthor;
import server.repositories.AuthorRepository;
import server.repositories.BookRepository;

import java.util.HashSet;
import java.util.Set;


@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/protobuf/authors")
public class ProtoAuthorController {
    @Autowired
    private AuthorRepository arepo;
    @Autowired
    private BookRepository brepo;

    @RequestMapping(method = RequestMethod.GET)
    public Object PROgetAuthors(@RequestParam(value = "number", defaultValue = "0",required = false) int number, @RequestParam(value = "size", defaultValue = "5",required = false) int size, @RequestParam(value = "property", defaultValue = "", required = false)  String sort, @RequestParam(value = "direction", defaultValue = "",required = false) String direct)  {
        if(size<1 | size>200) {
            size = 10;
            System.out.println("No size of page provided");
        }
        if(number<0 | number>100) {
            number = 0;
            System.out.println("No number of page provided");
        }
        Sort.Direction dir= Sort.Direction.ASC;
        if (direct.equals("down") | direct.equals("DESC") | direct.equals("descending"))
            dir= Sort.Direction.DESC;
        if(!(sort.equals("firstName") | sort.equals("date") | sort.equals("id")))
            sort ="lastName";
        Page<Author> allAuthors = arepo.findAll(new PageRequest(number, size,new Sort(dir, sort)));
        if( allAuthors.getTotalElements()>0 ){
            AuthorsProto.Builder proto= AuthorsProto.newBuilder();
            for (Author author : allAuthors){
                for (Book book : author.getBooks())
                    book.setAuthors(null);
                proto.addAuthors(CodingAuthor.Encode(author));
            }
            return proto.build();
        }
        else
            return new DefaultRestResponse(true, "No authors found.");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object PROgetById(@PathVariable Long id) {
        Author author= arepo.findOne(id);
        if (author != null) {
            Set<Book> books= author.getBooks();
            if (books.size() >0)
            {
                AuthorBooks authorBooks= new AuthorBooks();
                for (Book book : books)
                    book.setAuthors(null);
                author.setBooks(books);
                return CodingAuthor.Encode(author);
            }
            return CodingAuthor.Encode(author);
        }
        return new DefaultRestResponse(true, "No author found by id = "+id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE )
    public Object deleteById(@PathVariable Long id) {
        Author author= arepo.findOne(id);
        if( author != null) {
            arepo.delete(author);
            return new DefaultRestResponse(false, "deleted author #"+id);
        }
        else
             return new DefaultRestResponse(true, "No author found by id= "+id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Object PROaddAuthor(@RequestBody AuthorProto proto) {
        if (proto != null) {
            Author author= CodingAuthor.Decode(proto);
            Set<Book> bookSet= FindOne.NoRepeatBooks(author.getBooks());
            author.setBooks(new HashSet<Book>());
            Long id = FindOne.findAuthor(author,arepo.findAll());//
            if( id>=0L) {
                if (bookSet.isEmpty())
                    return new DefaultRestResponse(true, "Such author exists!!! id=" + id);
                author = arepo.findOne(id);
            }
            if (!bookSet.isEmpty()){
                Set<Book> newbooks= new HashSet<Book>();
                Set<Book> Allbooks = brepo.findAll();
                for (Book book : bookSet)
                {
                    id= FindOne.findBook(book, Allbooks );
                    if( id== -1L)
                        newbooks.add(book);
                    else
                        if (FindOne.NotThereBook(book, author.getBooks()))
                            newbooks.add(brepo.findOne(id));
                }
                author.addBooks(newbooks);
            }
            arepo.save(author);
            //return CodingAuthor.Encode(arepo.save(author));
            return new DefaultRestResponse(false, author.getId().toString());
        }
        return new DefaultRestResponse(true, "Expected JSON, but didn't find one.");
    }

@RequestMapping(value = "/{Id}", method = RequestMethod.POST)
public Object PROchangeById(@PathVariable Long Id, @RequestBody AuthorProto proto) {
    if( proto == null){
        return new DefaultRestResponse(true, "No author in Json was entered");
    }
    Author change= CodingAuthor.Decode(proto);
    Author author= arepo.findOne(Id);
    if( author != null & change != null) {
        Long id= FindOne.findAuthor(change, arepo.findAll());
        if (id !=-1L & Id != id)
            return new DefaultRestResponse(true, "This author already exists!!! #" + id);
        author.setDate(change.getDate());
        author.setFirstName(change.getFirstName());
        author.setLastName(change.getLastName());
        Set<Book> bookSet = FindOne.NoRepeatBooks(change.getBooks());
        if (bookSet != null){
            Set<Book> newbooks= new HashSet<Book>();
            Set<Book> Abooks = author.getBooks();
            for (Book book : bookSet)
            {
                id= FindOne.findBook(book, Abooks );
                if( id== -1L)
                    newbooks.add(book);
            }
            author.addBooks(newbooks);
        }
        change= arepo.save(author );//
        if (change != null)
            return new DefaultRestResponse(false, "Changed author #" + Id);
        else
            return new DefaultRestResponse(true, "Couldn't change author #" + Id);
    }
    else
        return new DefaultRestResponse(true, "No author found by id= "+Id);
}

}
