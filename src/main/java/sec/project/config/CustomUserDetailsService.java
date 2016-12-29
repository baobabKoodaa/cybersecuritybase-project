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

    @PostConstruct
    public void init() {
        /** Populate database for testing convenience. */
        User atte = new User("atte", encode("1"));
        User mikko = new User("mikko", encode("masa"));
        userRepository.save(atte);
        userRepository.save(mikko);
        setUpNewBook("Aten tilikirja", atte);
        categoryRepository.save(new Category("Ruokamenot"));
        categoryRepository.save(new Category("Mikrobitin kestotilaus"));
    }

    public Book setUpNewBook(String name, User user) {
        Book book = new Book(name, user);
        bookRepository.save(book);
        readAccessRepository.save(new ReadAccess(book, user));
        writeAccessRepository.save(new WriteAccess(book, user));
        return book;
    }

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

}
