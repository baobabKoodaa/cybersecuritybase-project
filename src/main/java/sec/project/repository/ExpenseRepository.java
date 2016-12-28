package sec.project.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sec.project.domain.Expense;
import sec.project.domain.Book;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /** Returns latest 10 current expenses from given Book. */
    List<Expense> findFirst10ByBookAndCurrentOrderByTimeAddedDesc(Book book, Boolean current);

    /** Returns all expenses from a book, including non current
     * expenses (deleted or old versions of modified expenses). */
    List<Expense> findByBook(Book book);
}
