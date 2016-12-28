package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sec.project.config.CustomUserDetailsService;
import sec.project.domain.*;
import sec.project.repository.*;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
public class RESTController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    WriteAccessRepository writeAccessRepository;

    @Autowired
    CustomUserDetailsService manager;

    @RequestMapping("/")
    public String defaultMapping(Model model, Principal auth) {
        User user = userRepository.findOneByLoginname(auth.getName());
        Book latest = user.getLatestRead();
        if (latest == null) {
            /** Assign any book as latest. */
            for (WriteAccess w : user.getWriteAccessSet()) {
                latest = w.getBook();
                user.setLatestRead(latest);
                userRepository.save(user);
                break;
            }
        }
        if (latest == null) {
            /** If user has deleted all their books. */
            latest = manager.setUpNewBook("New book", user);
            user.setLatestRead(latest);
            userRepository.save(user);
        }
        model.addAttribute("book", latest);
        model.addAttribute("expenses", expenseRepository.findFirst10ByBookAndCurrentOrderByTimeAddedDesc(latest, true));
        return "index";
    }

    /** Adding or modifying an expense. */
    @Transactional
    @PostMapping(value = "/postExpense")
    public String processRequestToPostExpense(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam long bookId,
            @RequestParam long lowestSubCategoryId,
            @RequestParam long amountCents,
            @RequestParam String previousVersion,
            Principal auth
    ) {
        Book book = bookRepository.findOne(bookId);
        Category lowestSubCategory = categoryRepository.findOne(lowestSubCategoryId);
        User user = userRepository.findOneByLoginname(auth.getName());
        WriteAccess writeAccess = writeAccessRepository.findOneByBookAndUser(book, user);
        if (writeAccess == null) {
            return "redirect:/";
        }

        Expense current = new Expense(year, month, book, lowestSubCategory, amountCents, user);
        if (!previousVersion.isEmpty()) {
            /** If modifying, maintain a version history of changes. */
            Long prevId = Long.parseLong(previousVersion);
            Expense previous = expenseRepository.findOne(prevId);
            previous.setCurrent(false);
            previous.setNextVersionId(current.getId());
            current.setPreviousVersionId(prevId);
            expenseRepository.save(previous);
        }
        expenseRepository.save(current);
        return "redirect:/";
    }

    @DeleteMapping(value = "/deleteExpense")
    public String processRequestToDeleteExpense(
            @RequestParam long id,
            Principal auth
    ) {
        User user = userRepository.findOneByLoginname(auth.getName());
        Expense expense = expenseRepository.findOne(id);
        WriteAccess writeAccess = writeAccessRepository.findOneByBookAndUser(expense.getBook(), user);
        if (writeAccess != null) {
            expense.setCurrent(false);
            expenseRepository.save(expense);
        }
        return "redirect:/";
    }

}
