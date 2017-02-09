package fr.frodriguez.biblio.element.serie;

import android.os.Bundle;

import fr.frodriguez.biblio.simpleelement.SimpleListActivity;
import fr.frodriguez.biblio.model.Serie;

/**
 * By FloZone on 19/01/2017.
 */

public class SerieListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Serie.class);

        super.onCreate(savedInstanceState);
    }
}
