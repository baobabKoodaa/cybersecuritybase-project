package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findOneByName(String name);
}
