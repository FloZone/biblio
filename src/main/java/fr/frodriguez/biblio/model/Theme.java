package fr.frodriguez.biblio.model;

import android.support.annotation.NonNull;

import com.activeandroid.annotation.Table;

/**
 * Created by Florian Rodriguez on 30/01/16.
 */
@Table(name = "theme")
public class Theme extends SimpleNamedElement {

    private final static String TABLE_NAME  = "theme";

    @Override
    @NonNull
    public String getTableName() {
        return TABLE_NAME;
    }
}
