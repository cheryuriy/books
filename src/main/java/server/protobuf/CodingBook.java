package server.protobuf;

import server.entities.Author;
import server.entities.Book;
import server.protobuf.BookAuthorProtos.BookProto;
import server.protobuf.BookAuthorProtos.AuthorProto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CodingBook {
    public static Book Decode(BookProto proto){
        Book book= new Book();
        if (proto.getId()>0 & proto.getId()<100000)
            book.setId(proto.getId());
        book.setBook(proto.getBook());
        book.setPublisher(proto.getPublisher());

        String str= proto.getDate();
        DateTimeFormatter formatter_1=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate_1= LocalDate.parse(str,formatter_1);
        book.setDate(localDate_1);

        if (proto.getAuthorsList() != null)
            for (AuthorProto authorProto : proto.getAuthorsList())
                book.addAuthor(CodingAuthor.Decode(authorProto));
        return book;
    }

    public static BookProto Encode(Book book){
        BookProto.Builder proto= BookProto.newBuilder();
        if (book.getId()>0 & book.getId()<100000)
            proto.setId(book.getId());
        proto.setDate(book.getDate().toString());
        proto.setBook(book.getBook());
        proto.setPublisher(book.getPublisher());
        if (book.getAuthors()!=null)
            for (Author author : book.getAuthors())
                proto.addAuthors(CodingAuthor.Encode(author));
        return proto.build();
    }
}
