package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sec.project.domain.*;
import sec.project.repository.BookRepository;
import sec.project.repository.CategoryRepository;
import sec.project.repository.ExpenseRepository;
import sec.project.repository.UserRepository;

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

    @RequestMapping("/")
    public String defaultMapping(Model model, Principal auth) {
        User user = userRepository.findOneByLoginname(auth.getName());
        Book latest = user.getLatestRead();
        model.addAttribute("bookName", latest.getName());
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

        Expense current = new Expense(year, month, book, lowestSubCategory, amountCents, user);
        if (!previousVersion.isEmpty()) {
            /** If modifying, maintain a version history of changes. */
            Long prevId = Long.parseLong(previousVersion);
            current.setPreviousVersionId(prevId);
            Expense previous = expenseRepository.findOne((long)prevId);
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
        expense.setCurrent(false);
        expenseRepository.save(expense);
        return "redirect:/";
    }

}
