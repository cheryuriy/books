package server.functions;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entities.Author;
import server.entities.Book;
import server.repositories.AuthorRepository;
import server.repositories.BookRepository;
import java.util.HashSet;
import java.util.Set;

@Component
public class FindRepeatsDelete {
    @Autowired
    private AuthorRepository arepo;
    @Autowired
    private BookRepository brepo;


    public String FindRepeats() {
        return DeleteRepeats(false);
    }

    public String DeleteRepeats(boolean delete){
        String str = findAuthorRepeats( delete);
        str += findBookRepeats(delete);
        return str;
    }

    public String findBookRepeats(boolean delete){
        Set<Book> BooksAll = brepo.findAll();
        Long[] Id= new Long[BooksAll.size()];
        int n = 0;
        Set<Book> BookSet2 = new HashSet<>();
        BookSet2.addAll(BooksAll);
        int i;
        boolean del;
        for (Book bk : BooksAll)
        {
            BookSet2.remove(bk);
            del=false;
            for (i=0; i<n; i++)
                if (Id[i] == bk.getId()) {
                    del=true;
                    break;
                }
            if (del)
                continue;
            for (Book book: BookSet2)
            {
                del=false;
                for (i=0; i<n; i++)
                    if (Id[i] == book.getId()) {
                        del=true;
                        break;
                    }
                if (del)
                    continue;
                if (bk.getBook().equals(book.getBook()) &
                        bk.getPublisher().equals(book.getPublisher()) &
                        bk.getDate().compareTo(book.getDate())==0)
                    if (bk.getAuthors().size()>=book.getAuthors().size())
                        Id[n++] = book.getId();
                    else{
                        Id[n++] = bk.getId();
                        break;
                    }
            }
        }
        String str;
        if (delete)
            str="\nDeleted repeats of Books:\n";
        else
            str="\nFind repeats of Books:\n";
        if (n==0)
            str+="None";
        for ( i=0; i<n; i++) {
            Book book = brepo.findOne(Id[i]);
            str+= "id=" +Id[i]+", Book='"+book.getBook()+"'\n";
            if(delete)
                brepo.delete(book);
        }
        return str;
    }
    //________+++++++++++________++++++++++++++++
    public String findAuthorRepeats(boolean delete){
        Set<Author> authorsAll = arepo.findAll();
        Long[] Id= new Long[authorsAll.size()];
        int n = 0;
        Set<Author> authorSet2 = new HashSet<>();
        authorSet2.addAll(authorsAll);
        int i;
        boolean del;
        for (Author aut : authorsAll)
        {
            authorSet2.remove(aut);
            del=false;
            for (i=0; i<n; i++)
                if (Id[i] == aut.getId()) {
                    del=true;
                    break;
                }
            if (del)
                continue;
            for (Author author: authorSet2)
            {
                del=false;
                for (i=0; i<n; i++)
                    if (Id[i] == author.getId()) {
                        del=true;
                        break;
                    }
                if (del)
                    continue;
                if (aut.getLastName().equals(author.getLastName()) &
                        aut.getFirstName().equals(author.getFirstName()) &
                        aut.getDate().compareTo(author.getDate())==0)
                    if (aut.getBooks().size()>=author.getBooks().size())
                        Id[n++] = author.getId();
                    else{
                        Id[n++] = aut.getId();
                        break;
                    }
            }
        }
        String str;
        if (delete)
            str="\nDeleted repeats of Authors:\n";
        else
            str="\nFind repeats of Authors:\n";
        if (n==0)
            str+="None\n";
        for ( i=0; i<n; i++) {
            Author author= arepo.findOne(Id[i]);
            str+= "id=" +Id[i]+", Author='" +author.getFirstName()+"__"+author.getLastName()+"\n";
            if(delete)
                arepo.delete(author);
        }
        return str;
    }
}
