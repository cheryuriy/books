package server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import server.entities.Book;
import java.util.Set;


public interface BookRepository extends PagingAndSortingRepository<Book, Long>
{
   Page<Book> findAll(Pageable pageable);
    Set<Book> findAll();
    Book findOne(Long primaryKey);
    void delete(Book author);
}
