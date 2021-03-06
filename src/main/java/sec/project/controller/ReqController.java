package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sec.project.domain.*;
import sec.project.logic.DAO;

import javax.transaction.Transactional;
import java.security.Principal;

@Controller
public class ReqController {

    @Autowired
    DAO dao;

    @RequestMapping("/")
    public String defaultMapping(Model model, Principal auth) {
        User user = dao.findUserByLoginname(auth.getName());
        Book book = dao.detLatestBookForUser(user);

        model.addAttribute("book", book);
        model.addAttribute("categories", dao.findAllCategories());
        model.addAttribute("expenses", dao.findSomeRecentExpenses(book));
        return "index";
    }

    /** Adding or modifying an expense. */
    @Transactional
    @PostMapping("/postExpense")
    public String processRequestToPostExpense(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam long bookId,
            @RequestParam String category,
            @RequestParam long amountCents,
            @RequestParam String previousVersion,
            Principal auth
    ) {
        User user = dao.findUserByLoginname(auth.getName());
        Book book = dao.findBookById(bookId);
        if (dao.hasWriteAccess(user, book)) {
            Expense current = dao.createExpense(year, month, book, category, amountCents, user);
            if (!previousVersion.isEmpty()) {
                dao.updateVersionHistory(current, previousVersion);
            }
        }
        return "redirect:/";
    }

    @DeleteMapping("/deleteExpense")
    public String processRequestToDeleteExpense(
            @RequestParam long id,
            Principal auth
    ) {
        User user = dao.findUserByLoginname(auth.getName());
        Expense expense = dao.findExpenseById(id);
        Book book = expense.getBook();
        if (dao.hasWriteAccess(user, book)) {
            dao.deleteExpense(expense);
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
