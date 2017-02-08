package fr.frodriguez.biblio.element.book;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Book;
import fr.frodriguez.biblio.model.utils.Defines;
import fr.frodriguez.biblio.utils.IntentExtra;
import fr.frodriguez.library.utils.MessageUtils;
import fr.frodriguez.library.utils.StringUtils;

/**
 * Created by Florian Rodriguez on 21/02/16.
 */
public class BookEditActivity extends AppCompatActivity {

    private Book book;

    private EditText etTitle;
    private EditText etSubtitle;
    private EditText etNumber;
    private EditText etYear;
    private EditText etBookshelfnumber;
    private EditText etDescription;
    private Switch aSwitch;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);

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

        // Get all edittext
        etTitle           = (EditText) findViewById(R.id.booksDetailsTitleEdit);
        etSubtitle        = (EditText) findViewById(R.id.booksDetailsSubtitleEdit);
        etNumber          = (EditText) findViewById(R.id.booksDetailsNumberEdit);
        etYear            = (EditText) findViewById(R.id.booksDetailsYearEdit);
        etBookshelfnumber = (EditText) findViewById(R.id.booksDetailsBookshelfnumberEdit);
        etDescription     = (EditText) findViewById(R.id.booksDetailsDescriptionEdit);
        aSwitch           = (Switch) findViewById(R.id.booksDetailsGot);
        ratingBar         = (RatingBar) findViewById(R.id.booksDetailsRate);

        // Fill them with book info
        populateEdittext();
    }

    /**
     * Populate the edittext with the book info
     */
    private void populateEdittext() {
        etTitle.setText(book.getTitle());
        etSubtitle.setText(book.getSubtitle());
        etNumber.setText(String.valueOf(book.getNumber()));
        etYear.setText(String.valueOf(book.getYear()));
        etBookshelfnumber.setText(String.valueOf(book.getBookshelfnumber()));
        etDescription.setText(book.getDescription());
        aSwitch.setChecked(book.getGot());
        ratingBar.setRating(book.getRate());
    }


    /**
     * Add cancel & save buttons to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel_save, menu);
        return true;
    }

    /**
     * Handle click on the button added just above
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // If this is the cancel button
            case R.id.itemCancelEdit:
                finish();
                return true;

            // If this is the save button
            case R.id.itemSaveEdit:
                // If the input is valid
                if(checkUserInput(etTitle, etSubtitle)) {
                    // Title & subtitle
                    book.setTitle(etTitle.getText().toString());
                    book.setSubtitle(etSubtitle.getText().toString());

                    // Year
                    String value = etYear.getText().toString();
                    if(!StringUtils.isEmpty(value)) book.setYear(Integer.parseInt(value));

                    // Number
                    value = etNumber.getText().toString();
                    if(!StringUtils.isEmpty(value)) book.setNumber(Integer.parseInt(value));

                    // Bookshelf etNumber
                    value = etBookshelfnumber.getText().toString();
                    if(!StringUtils.isEmpty(value)) book.setBookshelfnumber(Integer.parseInt(value));

                    // Got
                    book.setGot(aSwitch.isChecked());

                    // Rating
                    book.setRate((int) ratingBar.getRating());

                    // Description
                    value = etDescription.getText().toString();
                    if(!StringUtils.isEmpty(value)) book.setDescription(value);

                    // Save the new element
                    book.save();

                    // Close this activity
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if the title and subtitle are valid for a new book
     * @param title
     * @param subtitle
     * @return true if the title and subtitle are valid
     */
    private boolean checkUserInput(EditText title, EditText subtitle) {
        // If there is no change on the title and subtitle
        if(book.getTitle().equals(title.getText().toString())
                && book.getSubtitle().equals(subtitle.getText().toString())) {
            return true;
        }

        int titleAvailable = Book.isTitleAvailable(title.getText().toString());
        int coupleAvailable = Book.isTitleAndSubtitleAvailable(title.getText().toString(), subtitle.getText().toString());

        // If the title is available
        if(titleAvailable == Defines.VALUE_OK) {
            return true;
        }
        // If the title is empty
        else if(titleAvailable == Defines.VALUE_ERROR_EMPTY) {
            title.setError(getResources().getString(R.string.errorMustSetTitle));
            return false;
        }
        // If the couple title/subtitle is available
        else if(coupleAvailable == Defines.VALUE_OK) {
            return true;
        }
        // If this couple title/subtitle is already used
        else if(coupleAvailable == Defines.VALUE_ERROR_USED) {
            title.setError(getResources().getString(R.string.errorTitleSubtitleNotAvailable));
            return false;
        }
        return false;
    }
}
