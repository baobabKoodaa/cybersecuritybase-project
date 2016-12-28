package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.WriteAccess;

public interface WriteAccessRepository extends JpaRepository<WriteAccess, Long> {
}
