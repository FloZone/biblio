package fr.frodriguez.biblio.element.book;

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
import fr.frodriguez.biblio.utils.IntentExtra;
import fr.frodriguez.library.utils.MessageUtils;

/**
 * Created by Florian Rodriguez on 07/02/16.
 */
public class BookViewActivity extends AppCompatActivity {

    private Book book;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvAuthor;
    private TextView tvFormat;
    private TextView tvSerie;
    private TextView tvTheme;
    private TextView tvNumber;
    private TextView tvYear;
    private TextView tvBookshelfnumber;
    private TextView tvDescription;
    private Switch aSwitch;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Get the book ID
        long bookId = getIntent().getLongExtra(IntentExtra.BOOK_ID, IntentExtra.BOOK_ID_DEFAULT);
        if(bookId == IntentExtra.BOOK_ID_DEFAULT) {
            MessageUtils.showToast(this, "Error: book ID is mandatory");
            finish();
        }
        book = Book.getById(bookId);
        if(book == null) {
            MessageUtils.showToast(this, "Error: this book does not exist");
            finish();
        }

        // Get all textview
        tvTitle           = (TextView) findViewById(R.id.booksDetailsTitle);
        tvSubtitle        = (TextView) findViewById(R.id.booksDetailsSubtitle);
        tvAuthor          = (TextView) findViewById(R.id.booksDetailsAuthor);
        tvFormat          = (TextView) findViewById(R.id.booksDetailsFormat);
        tvSerie           = (TextView) findViewById(R.id.booksDetailsSerie);
        tvTheme           = (TextView) findViewById(R.id.booksDetailsTheme);
        tvNumber          = (TextView) findViewById(R.id.booksDetailsNumber);
        tvYear            = (TextView) findViewById(R.id.booksDetailsYear);
        tvBookshelfnumber = (TextView) findViewById(R.id.booksDetailsBookshelfnumber);
        tvDescription     = (TextView) findViewById(R.id.booksDetailsDescription);
        aSwitch           = (Switch) findViewById(R.id.booksDetailsGot);
        ratingBar         = (RatingBar) findViewById(R.id.booksDetailsRate);

        // Fill them with book info
        populateEdittext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh book info
        populateEdittext();
    }

    /**
     * Populate the textview with the book info
     */
    private void populateEdittext() {
        tvTitle.setText(book.getTitle());
        tvSubtitle.setText(book.getSubtitle());
        tvNumber.setText(String.valueOf(book.getNumber()));
        tvYear.setText(String.valueOf(book.getYear()));
        tvBookshelfnumber.setText(String.valueOf(book.getBookshelfnumber()));
        tvDescription.setText(book.getDescription());
        aSwitch.setChecked(book.getGot());
        ratingBar.setRating(book.getRate());

        if(book.getAuthor() != null) {
            tvAuthor.setText(book.getAuthor().display());
        } else {
            //TODO valeur par défaut ou method StringUtils.getStringValue(String) qui retourne "" si null
            tvAuthor.setText("");
        }

        if(book.getFormat() != null) {
            tvFormat.setText(book.getFormat().display());
        } else {
            //TODO valeur par défaut ou method StringUtils.getStringValue(String) qui retourne "" si null
            tvFormat.setText("");
        }

        if(book.getSerie() != null) {
            tvSerie.setText(book.getSerie().display());
        } else {
            //TODO valeur par défaut ou method StringUtils.getStringValue(String) qui retourne "" si null
            tvSerie.setText("");
        }

        if(book.getTheme() != null) {
            tvTheme.setText(book.getTheme().display());
        } else {
            //TODO valeur par défaut ou method StringUtils.getStringValue(String) qui retourne "" si null
            tvTheme.setText("");
        }

    }

    /**
     * Add delete & edit buttons to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_edit, menu);
        return true;
    }

    /**
     * Handle click on the button added just above
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // If this is the delete button
            case R.id.itemDelete:
                deleteBook();
                return true;

            // If this is the edit button
            case R.id.itemEdit:
                Intent intent = new Intent(BookViewActivity.this, BookEditActivity.class);
                intent.putExtra(IntentExtra.BOOK_ID, book.getId());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ask the user a confirmation to delete this book
     */
    private void deleteBook() {
        // Confirmation dialog
        new AlertDialog.Builder(this)
            .setMessage(R.string.bookDeleteConfirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Book.delete(Book.class, book.getId());
                    finish(); // close this activity
                }
            })
            .create()
            .show();
    }

}
