package server.functions;


import server.entities.Author;
import server.entities.Book;

import java.util.HashSet;
import java.util.Set;

public class FindOne {

    public static Set<Book> NoRepeatBooks(Set<Book> bookSet)
    {
        Set<Book> newSet= new HashSet<>();
        Set<Book> bookSet2 = new HashSet<>();
        bookSet2.addAll(bookSet);
        for (Book book : bookSet) {
            bookSet2.remove(book);
            if (FindOne.NotThereBook(book, bookSet2))
                newSet.add(book);
        }
         return newSet;
    }
    public static Set<Author> NoRepeatAuthors(Set<Author> authorSet)
    {
        Set<Author> newauthors = new HashSet<>();
        Set<Author> authorSet2 = new HashSet<>();
        authorSet2.addAll(authorSet);
        for (Author author : authorSet)
        {
            authorSet2.remove(author);
            if (FindOne.NotThereAuthor(author,authorSet2))
                newauthors.add(author);
        }
        return newauthors;
    }
    public static boolean NotThereAuthor(Author author, Set<Author> authorSet)
    {
        for (Author aut : authorSet)
            if ( aut.getLastName().equals(author.getLastName()) &
                    aut.getFirstName().equals(author.getFirstName())&
                    aut.getDate().compareTo(author.getDate())==0)
                return false;
        return true;
    }
    public static boolean NotThereBook(Book book, Set<Book> bookSet)
    {
        for (Book bk : bookSet)
            if(bk.getBook().equals(book.getBook()) &
                    bk.getPublisher().equals(book.getPublisher()) &
                    bk.getDate().compareTo(book.getDate())==0)
                return false;
        return true;
    }
    public static Long findAuthor(Author author,  Set <Author> authorSet)
    {
        for (Author aut : authorSet)
        if (aut.getLastName().equals(author.getLastName()) &
                aut.getFirstName().equals(author.getFirstName()) &
                aut.getDate().compareTo(author.getDate())==0)
            return aut.getId();
        return -1L;
    }
    public static Long findBook(Book book,Set <Book> bookSet)
    {
        for (Book bk : bookSet)
            if(bk.getBook().equals(book.getBook()) &
                    bk.getPublisher().equals(book.getPublisher()) &
                    bk.getDate().compareTo(book.getDate())==0)
                return bk.getId();
        return -1L;
    }
}
