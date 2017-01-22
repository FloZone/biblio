package fr.frodriguez.biblio.book;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Book;
import fr.frodriguez.biblio.utils.IntentData;

/**
 * Created by Florian Rodriguez on 21/02/16.
 */
public class BookEditActivity extends AppCompatActivity {

    private Book book;

    private EditText editTextTitle;
    private EditText editTextSubtitle;
    private EditText editTextNumber;
    private EditText editTextYear;
    private EditText editTextBookshelfnumber;
    private EditText editTextDescription;
    private Switch aSwitch;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);

        // récupérer l'id passé à l'activité
        book = Book.getById(getIntent().getLongExtra(IntentData.BOOK_ID, IntentData.BOOK_ID_DEFAULT));

        // récupérer tous les edittexts
        editTextTitle = (EditText) findViewById(R.id.booksDetailsTitleEdit);
        editTextSubtitle = (EditText) findViewById(R.id.booksDetailsSubtitleEdit);
        editTextNumber = (EditText) findViewById(R.id.booksDetailsNumberEdit);
        editTextYear = (EditText) findViewById(R.id.booksDetailsYearEdit);
        editTextBookshelfnumber = (EditText) findViewById(R.id.booksDetailsBookshelfnumberEdit);
        editTextDescription = (EditText) findViewById(R.id.booksDetailsDescriptionEdit);
        aSwitch = (Switch) findViewById(R.id.booksDetailsGot);
        ratingBar = (RatingBar) findViewById(R.id.booksDetailsRate);

        // remplir les infos
        fillEditTexts();
    }


    @Override
    // créer le menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère le clic sur les items du menu
        int id = item.getItemId();

        // valider les modifications
        if (id == R.id.itemSaveEdit) {
            // vérifier que le titre n'est pas vide
            if (editTextTitle.getText().toString().length() < 1) {
                editTextTitle.setError(getResources().getString(R.string.booksDialogErrorTitle));
            }
            // sinon vérifier que le livre n'existe pas déjà
            else {
                // chercher un livre identique
                /*Book existingBook = Book.getByTitleAndSubtitle(
                        editTextTitle.getText().toString(),
                        editTextSubtitle.getText().toString()
                );
                // si on trouve un livre identique avec un id différent
                if ( existingBook != null && !(existingBook.getId().equals(book.getId())) ) {
                    editTextTitle.setError(getResources().getString(R.string.booksDialogErrorExists));
                }
                // sinon, sauvegarder les modifications
                else {*/

                saveModifications();

                // fermer l'activité (retour sur BookDetailsActivity)
                finish();

                //}
                return true;
            }
        }
        // annuler les modifications: fermer l'activité (retour sur BookDetailsActivity)
        else if(id == R.id.itemCancelEdit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // remplir les textViews avec les infos du livre
    private void fillEditTexts() {
        // remplir les textviews
        editTextTitle.setText(book.getTitle());
        editTextSubtitle.setText(book.getSubtitle());
        editTextNumber.setText(String.valueOf(book.getNumber()));
        editTextYear.setText(String.valueOf(book.getYear()));
        editTextBookshelfnumber.setText(String.valueOf(book.getBookshelfnumber()));
        editTextDescription.setText(book.getDescription());
        aSwitch.setChecked(book.getGot());
        ratingBar.setRating(book.getRate());
    }


    // enregistrer les modifications du livre
    private void saveModifications() {
        // récupérer les informations du livre
        book.setTitle(editTextTitle.getText().toString());
        book.setSubtitle(editTextSubtitle.getText().toString());
        book.setNumber(Integer.parseInt(editTextNumber.getText().toString()));
        book.setYear(Integer.parseInt(editTextYear.getText().toString()));
        book.setBookshelfnumber(Integer.parseInt(editTextBookshelfnumber.getText().toString()));
        book.setDescription(editTextDescription.getText().toString());
        book.setGot(aSwitch.isChecked());
        book.setRate((int) ratingBar.getRating());
        // sauvegarder en base
        book.save();
    }
}
