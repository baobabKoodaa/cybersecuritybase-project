package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.ReadAccess;

public interface ReadAccessRepository extends JpaRepository<ReadAccess, Long> {
}
