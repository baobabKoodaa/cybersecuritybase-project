package sec.project.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import sec.project.domain.*;
import sec.project.repository.*;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class DAO {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    ReadAccessRepository readAccessRepository;

    @Autowired
    WriteAccessRepository writeAccessRepository;

    public User findUserByLoginname(String loginname) {
        return userRepository.findOneByLoginname(loginname);
    }

    public Book findBookById(Long bookId) {
        return bookRepository.findOne(bookId);
    }

    public Expense findExpenseById(Long expenseId) {
        return expenseRepository.findOne(expenseId);
    }

    public boolean hasWriteAccess(User user, Book book) {
        WriteAccess writeAccess = writeAccessRepository.findOneByBookAndUser(book, user);
        return (writeAccess != null);
    }

    public User createUser(String userName, String clearTextPassword) {
        User user = new User(userName, encode(clearTextPassword));
        userRepository.save(user);
        return user;
    }

    public Category createCategory(String categoryName) {
        Category category = new Category(categoryName);
        categoryRepository.save(category);
        return category;
    }

    /** Tries to, in order,
     * 1. Return most recently used book
     * 2. Return any book
     * 3. Create new book and return it. */
    @Transactional
    public Book detLatestBookForUser(User user) {
        Book latest = user.getLatestRead();
        if (latest == null) {
            /** Assign any accessible book as latest. */
            for (ReadAccess r : user.getReadAccessSet()) {
                latest = r.getBook();
                setBookAsLatestForUser(latest, user);
                break;
            }
        }
        if (latest == null) {
            /** If user has deleted all their books. */
            latest = createBook("New book", user);
            setBookAsLatestForUser(latest, user);
        }
        return latest;
    }

    public Category detCategoryByName(String categoryName) {
        Category category = categoryRepository.findOneByName(categoryName);
        if (category == null) {
            category = new Category(categoryName);
            categoryRepository.save(category);
        }
        return category;
    }

    public Book createBook(String bookName, User user) {
        Book book = new Book(bookName, user);
        bookRepository.save(book);
        readAccessRepository.save(new ReadAccess(book, user));
        writeAccessRepository.save(new WriteAccess(book, user));
        return book;
    }
    private void setBookAsLatestForUser(Book book, User user) {
        user.setLatestRead(book);
        userRepository.save(user);
    }

    private String encode(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Expense> findSomeRecentExpenses(Book book) {
        return expenseRepository.findFirst10ByBookAndCurrentOrderByTimeAddedDesc(book, true);
    }

    public Expense createExpense(int year, int month, Book book, String category, long amountCents, User user) {
        Category lowestSubCategory = detCategoryByName(category);
        Expense expense = new Expense(year, month, book, lowestSubCategory, amountCents, user);
        expenseRepository.save(expense);
        return expense;
    }

    /** Called when an expense is modified. */
    @Transactional
    public void updateVersionHistory(Expense current, String previousVersion) {
        Long prevId = Long.parseLong(previousVersion);
        Expense previous = expenseRepository.findOne(prevId);
        previous.setCurrent(false);
        previous.setNextVersionId(current.getId());
        expenseRepository.save(previous);
        current.setPreviousVersionId(prevId);
        expenseRepository.save(current);
    }

    /** Deleted expenses are kept in the database to provide a full modification history. */
    public void deleteExpense(Expense expense) {
        expense.setCurrent(false);
        expenseRepository.save(expense);
    }
}
