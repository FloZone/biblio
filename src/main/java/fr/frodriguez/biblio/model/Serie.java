package fr.frodriguez.biblio.model;

import android.support.annotation.NonNull;

import com.activeandroid.annotation.Table;

import fr.frodriguez.biblio.generic.SimpleNamedElement;

/**
 * Created by Florian Rodriguez on 30/01/16.
 */
@Table(name = "serie")
public class Serie extends SimpleNamedElement {

    private final static String TABLE_NAME  = "serie";

    @Override
    @NonNull
    public String getTableName() {
        return TABLE_NAME;
    }
}
