package fr.frodriguez.biblio.model;

import android.support.annotation.NonNull;

import com.activeandroid.annotation.Table;

import fr.frodriguez.biblio.model.utils.SimpleNamedElement;

/**
 * Created by Florian Rodriguez on 30/01/16.
 */
@Table(name = "author")
public class Author extends SimpleNamedElement {

    private final static String TABLE_NAME  = "author";

    @Override
    @NonNull
    public String getTableName() {
        return TABLE_NAME;
    }
}
