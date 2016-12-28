package sec.project.domain;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class Expense {

    private int id;
    private int year;
    private int month;
    private Book book;
    private Category lowestSubCategory;
    private long amountCents;
    private User user;
    private Integer previousVersionId;
    private Integer nextVersionId;
    private long timeAdded;

    public Expense() {
        super();
    }

    public Expense(int year, int month, Book book, Category lowestSubCategory, long amountCents, User user) {
        this();
        this.year = year;
        this.month = month;
        this.book = book;
        this.lowestSubCategory = lowestSubCategory;
        this.amountCents = amountCents;
        this.user = user;
        this.timeAdded = DateTime.now().getMillis();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(long amountCents) {
        this.amountCents = amountCents;
    }

    public Integer getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(Integer previousVersionId) {
        this.previousVersionId = previousVersionId;
    }

    public Integer getNextVersionId() {
        return nextVersionId;
    }

    public void setNextVersionId(Integer nextVersionId) {
        this.nextVersionId = nextVersionId;
    }

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    public Category getLowestSubCategory() {
        return lowestSubCategory;
    }

    public void setLowestSubCategory(Category lowestSubCategory) {
        this.lowestSubCategory = lowestSubCategory;
    }
}
