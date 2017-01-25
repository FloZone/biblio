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
import fr.frodriguez.library.utils.MessageUtils;

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
                MessageUtils.showSnackbar(view, "Surprise motherfucker !");
            }
        });
    }

    // Add a menu button to the action bar
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
                MessageUtils.showToast(this, "Croustade");
                return true;
            case R.id.action2:
                MessageUtils.showToast(this, "Madafaka");
                return true;
            case R.id.action3:
                MessageUtils.showToast(this, "Croustade again madafaka");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Book list
    public void buttonBooks(View view) {
        Intent intent = new Intent(MainActivity.this, BookListActivity.class);
        startActivity(intent);
    }

    // Author list
    public void buttonAuthors(View view) {
        Intent intent = new Intent(MainActivity.this, AuthorListActivity.class);
        startActivity(intent);
    }

    // Format list
    public void buttonFormats(View view) {
        Intent intent = new Intent(MainActivity.this, FormatListActivity.class);
        startActivity(intent);
    }

    // Serie list
    public void buttonSeries(View view) {
        Intent intent = new Intent(MainActivity.this, SerieListActivity.class);
        startActivity(intent);
    }

    // Theme list
    public void buttonThemes(View view) {
        Intent intent = new Intent(MainActivity.this, ThemeListActivity.class);
        startActivity(intent);
    }
}
