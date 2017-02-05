package fr.frodriguez.biblio.element.theme;

import android.os.Bundle;

import fr.frodriguez.biblio.simpleelement.SimpleListActivity;
import fr.frodriguez.biblio.model.Theme;

/**
 * By Florian on 19/01/2017.
 */

public class ThemeListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Theme.class);

        super.onCreate(savedInstanceState);
    }
}
