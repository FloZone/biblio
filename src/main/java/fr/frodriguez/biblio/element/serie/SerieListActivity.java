package fr.frodriguez.biblio.serie;

import android.os.Bundle;

import fr.frodriguez.biblio.simplelistview.SimpleListActivity;
import fr.frodriguez.biblio.model.Serie;

/**
 * By Florian on 19/01/2017.
 */

public class SerieListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Serie.class);

        super.onCreate(savedInstanceState);
    }
}
