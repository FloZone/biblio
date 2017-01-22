package fr.frodriguez.biblio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.activeandroid.ActiveAndroid;

import fr.frodriguez.biblio.author.AuthorListActivity;
import fr.frodriguez.biblio.book.BookListActivity;
import fr.frodriguez.biblio.format.FormatListActivity;
import fr.frodriguez.biblio.serie.SerieListActivity;
import fr.frodriguez.biblio.theme.ThemeListActivity;
import fr.frodriguez.biblio.utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ORM library (database)
        ActiveAndroid.initialize(this);

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showSnackbar(view, "Surprise motherfucker !");
            }
        });
    }

    // Add a button to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle click on the button added just above
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action1:
                Utils.showToast(this, "Croustade");
                return true;
            case R.id.action2:
                Utils.showToast(this, "Madafaka");
                return true;
            case R.id.action3:
                Utils.showToast(this, "Croustade again mada");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // bouton recherche
    public void buttonSearch(View view) {
        EditText editText = (EditText) findViewById(R.id.textSearch);
        Utils.showSnackbar(view, "Search: " + editText.getText().toString());
    }

    // bouton "livres"
    public void buttonBooks(View view) {
        Intent intent = new Intent(MainActivity.this, BookListActivity.class);
        startActivity(intent);
    }

    // bouton "auteurs"
    public void buttonAuthors(View view) {
        Intent intent = new Intent(MainActivity.this, AuthorListActivity.class);
        startActivity(intent);
    }

    // bouton "formats"
    public void buttonFormats(View view) {
        Intent intent = new Intent(MainActivity.this, FormatListActivity.class);
        startActivity(intent);
    }

    // bouton "series"
    public void buttonSeries(View view) {
        Intent intent = new Intent(MainActivity.this, SerieListActivity.class);
        startActivity(intent);
    }

    // bouton "themes"
    public void buttonThemes(View view) {
        Intent intent = new Intent(MainActivity.this, ThemeListActivity.class);
        startActivity(intent);
    }
}
