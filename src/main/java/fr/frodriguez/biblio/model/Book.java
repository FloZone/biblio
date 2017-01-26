package fr.frodriguez.biblio.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

import fr.frodriguez.library.utils.StringUtils;

import static fr.frodriguez.biblio.model.Defines.VALUE_OK;
import static fr.frodriguez.biblio.model.Defines.VALUE_ERROR_EMPTY;
import static fr.frodriguez.biblio.model.Defines.VALUE_ERROR_USED;

/**
 * Created by linux on 12/01/16.
 */
@Table(name = "book")
public class Book extends Model {
    @Column(name = "title", notNull = true)
    private String title;
    @Column(name = "subtitle")
    private String subtitle;
    @Column(name = "year")
    private int year;
    @Column(name = "number")
    private int number;
    @Column(name = "bookshelfnumber")
    private int bookshelfnumber;
    @Column(name = "got")
    private Boolean got;
    @Column(name = "rate")
    private int rate;
    @Column(name = "description")
    private String description;
    // relations
    @Column(name = "author")
    private Author author;
    @Column(name = "serie")
    private Serie serie;
    @Column(name = "theme")
    private Theme theme;
    @Column(name = "format")
    private Theme format;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getBookshelfnumber() {
        return bookshelfnumber;
    }

    public void setBookshelfnumber(int bookshelfnumber) {
        this.bookshelfnumber = bookshelfnumber;
    }

    public Boolean getGot() {
        return got;
    }

    public void setGot(Boolean got) {
        this.got = got;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }


    public static int isTitleAvailable(String title) {
        if(StringUtils.isEmpty(title)) {
            return VALUE_ERROR_EMPTY;
        }

        // Get a book from the database with the title to check
        Book existingBook = Book.getByTitle(title);
        // If there is a book, the title is not available
        return (existingBook == null) ? VALUE_OK : VALUE_ERROR_USED;
    }

    public static int isTitleAndSubtitleAvailable(String title, String subtitle) {
        if(StringUtils.isEmpty(title)) {
            return VALUE_ERROR_EMPTY;
        }

        // Get a book from the database with the title and subtitle to check
        Book existingBook = Book.getByTitleAndSubtitle(title, subtitle);
        // If there is a book, the subtitle is not available
        return (existingBook == null) ? VALUE_OK : VALUE_ERROR_USED;
    }
    
    public static Book getById(Long id) {
        return new Select()
                .from(Book.class)
                .where("id = ?", id)
                .executeSingle();
    }

    public static Book getByTitle(String title) {
        return new Select()
                .from(Book.class)
                .where("title = ?", title)
                .executeSingle();
    }

    public static Book getByTitleAndSubtitle(String title, String subtitle) {
        return new Select()
                .from(Book.class)
                .where("title = ? AND subtitle = ?", title, subtitle)
                .executeSingle();
    }

    public static List<Book> getAll() {
        return new Select()
                .from(Book.class)
                .execute();
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title=" + title +
                ", subtitle=" + subtitle +
                ", year=" + year +
                ", number=" + number +
                ", got=" + got +
                '}';
    }
}
