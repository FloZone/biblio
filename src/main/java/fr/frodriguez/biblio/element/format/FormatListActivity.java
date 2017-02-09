package fr.frodriguez.biblio.element.format;

import android.os.Bundle;

import fr.frodriguez.biblio.simpleelement.SimpleListActivity;
import fr.frodriguez.biblio.model.Format;

/**
 * By FloZone on 08/01/2017.
 */

public class FormatListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Format.class);

        super.onCreate(savedInstanceState);
    }
}