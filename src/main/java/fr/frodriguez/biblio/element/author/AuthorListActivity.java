package fr.frodriguez.biblio.element.author;

import android.os.Bundle;

import fr.frodriguez.biblio.simpleelement.SimpleListActivity;
import fr.frodriguez.biblio.model.Author;

/**
 * Created by Florian Rodriguez on 02/03/16.
 */

public class AuthorListActivity extends SimpleListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setElementClass(Author.class);

        super.onCreate(savedInstanceState);
    }
}