package sec.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sec.project.domain.Book;
import sec.project.domain.User;
import sec.project.repository.BookRepository;
import sec.project.repository.UserRepository;

import javax.transaction.Transactional;

@SpringBootApplication
public class CyberSecurityBaseProjectApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(CyberSecurityBaseProjectApplication.class);
    }
}
