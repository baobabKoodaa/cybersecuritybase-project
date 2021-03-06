package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.*;
import sec.project.logic.DAO;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DAO dao;

    @PostConstruct
    public void init() {
        /** Populate database for testing convenience. */

        User atte = dao.createUser("atte", "1");
        Book bookA = dao.createBook("Atte's expenses", atte);
        dao.createExpense(2016, 10, bookA, "food", 1083, atte);
        dao.createExpense(2016, 11, bookA, "food", 2045, atte);
        dao.createExpense(2016, 12, bookA, "food", 830, atte);
        dao.createExpense(2016, 12, bookA, "food", 666, atte);
        dao.createExpense(2016, 12, bookA, "entertainment", 3000, atte);

        User mikko = dao.createUser("mikko", "masa");
        Book bookM = dao.createBook("Mikko's expenses", mikko);
        dao.createExpense(2016, 10, bookM, "food", 23, mikko);
        dao.createExpense(2016, 11, bookM, "food", 67, mikko);
        dao.createExpense(2016, 12, bookM, "food", 55, mikko);
        dao.createExpense(2016, 12, bookM, "food", 99, mikko);
        dao.createExpense(2016, 12, bookM, "entertainment", 10, mikko);
        dao.createExpense(2016, 12, bookM, "entertainment", 30, mikko);
        dao.createExpense(2016, 12, bookM, "entertainment", 30, mikko);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = dao.findUserByLoginname(username);
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

}
