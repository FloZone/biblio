package fr.frodriguez.biblio.book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Book;
import fr.frodriguez.biblio.utils.IntentData;

/**
 * Created by Florian Rodriguez on 07/02/16.
 */
public class BookDetailsActivity extends AppCompatActivity {

    private long bookId;

    private TextView textViewTitle;
    private TextView textViewSubtitle;
    private TextView textViewNumber;
    private TextView textViewYear;
    private TextView textViewBookshelfnumber;
    private TextView textViewDescription;
    private Switch aSwitch;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // récupérer l'id passé à l'activité
        bookId = getIntent().getLongExtra(IntentData.BOOK_ID, IntentData.BOOK_ID_DEFAULT);

        // récupérer tous les textviews
        textViewTitle = (TextView) findViewById(R.id.booksDetailsTitle);
        textViewSubtitle = (TextView) findViewById(R.id.booksDetailsSubtitle);
        textViewNumber = (TextView) findViewById(R.id.booksDetailsNumber);
        textViewYear = (TextView) findViewById(R.id.booksDetailsYear);
        textViewBookshelfnumber = (TextView) findViewById(R.id.booksDetailsBookshelfnumber);
        textViewDescription = (TextView) findViewById(R.id.booksDetailsDescription);
        aSwitch = (Switch) findViewById(R.id.booksDetailsGot);
        ratingBar = (RatingBar) findViewById(R.id.booksDetailsRate);

        // remplir les infos
        fillTextViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // recharger les infos du livre
        fillTextViews();
    }


    @Override
    // créer le menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère le clic sur les items du menu
        int id = item.getItemId();

        // suppirmer
        if (id == R.id.itemDelete) {
            deleteBook();
            return true;
        }
        // modifier
        else if (id == R.id.itemEdit) {
            Intent intent = new Intent(BookDetailsActivity.this, BookEditActivity.class);
            intent.putExtra(IntentData.BOOK_ID, bookId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // remplir les textViews avec les infos du livre
    private void fillTextViews() {
        // récupérer les infos du livre
        Book book = Book.getById(bookId);

        // remplir les textviews
        textViewTitle.setText(book.getTitle());
        textViewSubtitle.setText(book.getSubtitle());
        textViewNumber.setText(String.valueOf(book.getNumber()));
        textViewYear.setText(String.valueOf(book.getYear()));
        textViewBookshelfnumber.setText(String.valueOf(book.getBookshelfnumber()));
        textViewDescription.setText(book.getDescription());
        aSwitch.setChecked(book.getGot());
        ratingBar.setRating(book.getRate());
    }


    // supprimer le livre
    private void deleteBook() {
        // dialog de confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // texte
        builder.setMessage(R.string.booksDetailsDelete);
        // bouton ok
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // supprimer
                Book.delete(Book.class, bookId);
                // fermer l'activité (retour sur BookListActivity)
                finish();
            }
        });
        // bouton annuler
        builder.setNegativeButton(R.string.cancel, null);

        // afficher
        builder.create().show();
    }

}
