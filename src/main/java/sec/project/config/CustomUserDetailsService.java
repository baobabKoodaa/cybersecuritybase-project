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
import sec.project.domain.Book;
import sec.project.domain.BookUser;
import sec.project.domain.User;
import sec.project.repository.BookRepository;
import sec.project.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        /** Hard coded credentials for convenience. Not intended as a flag-to-be-found. */
        User atte = new User("mikko", encode("masa"));
        User mikko = new User("atte", encode("123"));
        userRepository.save(atte);
        userRepository.save(mikko);

        Book bookA = new Book("A");
        test(atte, bookA);
        //test2(atte, bookA);
    }

    @Transactional
    private void test(User user, Book bookA) {
        BookUser conjoined = new BookUser();
        conjoined.setBook(bookA);
        conjoined.setUser(user);
        conjoined.setReadAccess(true);
        conjoined.setWriteAccess(true);
        bookA.getBookUsers().add(conjoined);
        userRepository.save(user);
        user.getBookUsers().add(conjoined);
        bookRepository.save(bookA);

        bookA = bookRepository.findOneByName("A");
        System.out.println("should be 1: " + bookA.getBookUsers().size());
        //user = userRepository.findOneByLoginname("atte");
        System.out.println("should be 1: " + user.getBookUsers().size());
    }

    @Transactional
    private void test2(User atte, Book bookA) {
        BookUser conjoined = null;
        for (BookUser bookUser : atte.getBookUsers()) {
            conjoined = bookUser;
            break;
        }
        if (conjoined == null) {
            System.out.println("DAFUCK");
        }
        conjoined.setWriteAccess(false);
        bookRepository.save(bookA);
        userRepository.save(atte);
        System.out.println("meh ");
        // tallenna miten, minne?
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
