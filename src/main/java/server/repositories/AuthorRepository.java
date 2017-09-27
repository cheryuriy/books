package server.repositories;

        import java.util.Set;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.repository.PagingAndSortingRepository;
        import org.springframework.stereotype.Repository;
        import server.entities.Author;


@Repository
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long>
{
  Page<Author> findAll(Pageable pageable);
    Author findOne(Long primaryKey);
    void delete(Author author);
    Set<Author> findAll();
}
