package sec.project.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.io.Serializable;

/** This class exists to facilitate a joined SQL table from Book and User with additional columns. */
@Entity
@Table(name = "book_user")
public class BookUser implements Serializable {

    private User user;
    private Book book;
    private boolean readAccess;
    private boolean writeAccess;

    public BookUser() {
        super();
    }

    public BookUser(User user, Book book, boolean readAccess, boolean writeAccess) {
        this();
        this.user = user;
        this.book = book;
        this.readAccess = readAccess;
        this.writeAccess = writeAccess;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "book_id")
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isReadAccess() {
        return readAccess;
    }

    public void setReadAccess(boolean readAccess) {
        this.readAccess = readAccess;
    }

    public boolean isWriteAccess() {
        return writeAccess;
    }

    public void setWriteAccess(boolean writeAccess) {
        this.writeAccess = writeAccess;
    }
}
