package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findOneByName(String name);
}
