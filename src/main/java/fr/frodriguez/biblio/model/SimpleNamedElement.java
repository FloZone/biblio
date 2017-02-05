package fr.frodriguez.biblio.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.util.List;

import fr.frodriguez.library.utils.StringUtils;

import static fr.frodriguez.biblio.model.Defines.VALUE_OK;
import static fr.frodriguez.biblio.model.Defines.VALUE_ERROR_EMPTY;
import static fr.frodriguez.biblio.model.Defines.VALUE_ERROR_USED;

/**
 * By Florian on 10/01/2017.
 */

abstract public class SimpleNamedElement extends Model implements Comparable<SimpleNamedElement> {

    @Column(name = "name", notNull = true)
    private String name;

    public String getName() {
        return name;
    }

    /**
     * Update the name and save it to the database
     * @param name the new name to set
     * @return true if updated successfully, false if not
     */
    public void updateName(String name) {
        this.name = name;
        this.save();
    }

    /**
     * Return information to display in a list
     * @return information to display
     */
    public String display() {
        return name;
    }

    /**
     * Return the table name of the element. Used to get the list of associated books
     * @return the table name of the class which implements this method
     */
    @NonNull
    abstract public String getTableName();

    /**
     * Check if the given name if available in the database or not
     * @param type the type of the element implementing this class
     * @param name the name to check
     * @param <Element> a class implementing this class
     * @return true if the name is available, false if the name is already used
     */
    public static <Element extends SimpleNamedElement> int isNameAvailable(Class<Element> type, String name) {
        if(StringUtils.isEmpty(name)) {
            return VALUE_ERROR_EMPTY;
        }

        // Get an element from the database with the name to check
        Element existingElement = Element.getByName(type, name);
        // If there is an element, the name is not available
        return (existingElement == null) ? VALUE_OK : VALUE_ERROR_USED;
    }


    /******************
     * Select methods *
     ******************/

    /**
     * Return the element with the given ID
     * @param type the type of the element implementing this class
     * @param id the database id of the element to get
     * @param <Element> a class implementing this class
     * @return Element with the given ID or null if no one exists
     */
    public static <Element extends SimpleNamedElement> Element getById(Class<Element> type, Long id) {
        return new Select()
                .from(type)
                .where("id = ?", id)
                .executeSingle();
    }

    /**
     * Return the element with the given name
     * @param type the type of the element implementing this class
     * @param name the name of the element to get
     * @param <Element> a class implementing this class
     * @return Element with the given name or null if no one exists
     */
    public static <Element extends SimpleNamedElement> Element getByName(Class<Element> type, String name) {
        return new Select()
                .from(type)
                .where("name = ?", name)
                .executeSingle();
    }

    /**
     * Return all the elements in the database
     * @param type the type of the element implementing this class
     * @param <Element> a class implementing this class
     * @return a list of all the elements
     */
    public static <Element extends SimpleNamedElement> List<Element> getAll(Class<Element> type) {
        return new Select()
                .from(type)
                .execute();
    }

    /**
     * Return all the associated books
     * @return a list of all the associated books
     */
    public List<Book> getBooks() {
        return getMany(Book.class, getTableName());
    }


    /********************
     * Override methods *
     ********************/

    @Override
    public String toString() {
        return this.getTableName() + "{id=" + this.getId() + ", name=" + this.name + "}";
    }

    @Override
    public int compareTo(@NonNull SimpleNamedElement o) {
        return this.name.compareTo(o.getName());
    }
}
