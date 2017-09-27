package server.protobuf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import server.entities.Author;
import server.entities.Book;
import server.protobuf.BookAuthorProtos.AuthorProto;
import server.protobuf.BookAuthorProtos.BookProto;
public class CodingAuthor {
    public static AuthorProto Encode(Author author){
        AuthorProto.Builder proto = AuthorProto.newBuilder();
        if (author.getId()>0 & author.getId()<100000)
            proto.setId(author.getId());
        proto.setFirstName(author.getFirstName());
        proto.setLastName(author.getLastName());
        proto.setDate(author.getDate().toString());
        if (author.getBooks() != null)
            for (Book book : author.getBooks())
                proto.addBooks(CodingBook.Encode(book));
        return proto.build();

    }
    public static Author Decode(AuthorProto proto){
        Author author= new Author();
        if (proto.getId()>0 & proto.getId()<100000)
            author.setId(proto.getId());
        author.setLastName(proto.getLastName());
        author.setFirstName(proto.getFirstName());

        String str= proto.getDate();
        DateTimeFormatter formatter_1=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate_1= LocalDate.parse(str,formatter_1);
        author.setDate(localDate_1);

        if (proto.getBooksList() != null)
            for (BookProto bookProto : proto.getBooksList())
                author.addBook(CodingBook.Decode(bookProto));

        return author;
    }
}
