package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Book;
import sec.project.domain.User;
import sec.project.domain.WriteAccess;

public interface WriteAccessRepository extends JpaRepository<WriteAccess, Long> {
    WriteAccess findOneByBookAndUser(Book book, User user);
}
