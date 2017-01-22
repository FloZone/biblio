package fr.frodriguez.biblio.model;

import android.support.annotation.NonNull;

import com.activeandroid.annotation.Table;

import fr.frodriguez.biblio.generic.SimpleNamedElement;

/**
 * By Florian on 08/01/2017.
 */

@Table(name = "format")
public class Format extends SimpleNamedElement {

    private final static String TABLE_NAME  = "format";

    @Override
    @NonNull
    public String getTableName() {
        return TABLE_NAME;
    }
}
