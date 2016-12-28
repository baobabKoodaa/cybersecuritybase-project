package sec.project.config;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.*;
import sec.project.repository.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadAccessRepository readAccessRepository;

    @Autowired
    private WriteAccessRepository writeAccessRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByLoginname(username);
        if (user == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLoginname(),
                user.getEncodedPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(user.getRole())));
    }

    private String encode(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    @PostConstruct
    public void init() {
        /** Populate database for testing convenience. */
        User atte = new User("atte", encode("1"));
        User mikko = new User("mikko", encode("masa"));
        userRepository.save(atte);
        userRepository.save(mikko);

        Book bookA = new Book("Aten tilikirja");
        bookRepository.save(bookA);
        testR(atte, bookA);
        test2(atte, bookA);

        atte = userRepository.findOneByLoginname(atte.getLoginname());
        atte.setLatestRead(bookA);
        userRepository.save(atte);
    }

    private void testR(User user, Book book) {
        ReadAccess r = new ReadAccess(book, user);
        readAccessRepository.save(r);
        user = userRepository.findOneByLoginname(user.getLoginname());
        System.out.println("User + " + user.getLoginname() + " HAS ACCESS TO " + user.getReadAccessSet().size());
        for (ReadAccess bah : user.getReadAccessSet()) {
            r = bah;
        }
        readAccessRepository.delete(r);
        System.out.println("SIZE " + readAccessRepository.findAll().size());
        user = userRepository.findOneByLoginname(user.getLoginname());
        System.out.println("User + " + user.getLoginname() + " HAS ACCESS TO " + user.getReadAccessSet().size());

        System.out.println(" ----------------- BOOK BELOW ----------------------");

        r = new ReadAccess(book, user);
        readAccessRepository.save(r);
        book = bookRepository.findOneByName(book.getName());
        System.out.println("ACCESS TO " + book.getReadAccessSet().size());
        for (ReadAccess bah : user.getReadAccessSet()) {
            r = bah;
        }
        readAccessRepository.delete(r);
        System.out.println("SIZE " + readAccessRepository.findAll().size());
        book = bookRepository.findOneByName(book.getName());
        System.out.println("ACCESS TO " + book.getReadAccessSet().size());

        System.out.println(" ----------------- MULTIPLE BELOW ----------------------");

        Book bookB = new Book("B");
        bookRepository.save(bookB);
        ReadAccess r2 = new ReadAccess(bookB, user);
        readAccessRepository.save(r2);
        r = new ReadAccess(book, user);
        readAccessRepository.save(r);
        System.out.println("ACCESS TO " + user.getReadAccessSet().size());
        user = userRepository.findOneByLoginname(user.getLoginname());
        System.out.println("ACCESS TO " + user.getReadAccessSet().size());

        System.out.println(" ----------------- WRITE BELOW ----------------------");

        WriteAccess w = new WriteAccess(book, user);
        writeAccessRepository.save(w);
        user = userRepository.findOneByLoginname(user.getLoginname());
        System.out.println("ACCESS TO " + user.getWriteAccessSet().size());
        writeAccessRepository.delete(w);
        user = userRepository.findOneByLoginname(user.getLoginname());
        System.out.println("ACCESS TO " + user.getWriteAccessSet().size());

        System.out.println("BOOK ID: " + book.getId());
    }

    @Transactional
    public void test2(User user, Book book) {
        System.out.println(" ----------------- EXPENSE TEST ----------------------");

        Category category = new Category();
        categoryRepository.save(category);
        Expense e = new Expense(2016, 12, book, category, 1238, user);
        expenseRepository.save(e);

        System.out.println("EXPENSE COUNT " + expenseRepository.findByBook(book).size());
    }


}
