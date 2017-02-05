package fr.frodriguez.biblio.format;

import android.os.Bundle;

import fr.frodriguez.biblio.simplelistview.SimpleListActivity;
import fr.frodriguez.biblio.model.Format;

/**
 * By Florian on 08/01/2017.
 */

public class FormatListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Format.class);

        super.onCreate(savedInstanceState);
    }
}