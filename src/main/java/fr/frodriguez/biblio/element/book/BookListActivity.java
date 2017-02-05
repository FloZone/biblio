package fr.frodriguez.biblio.element.book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Author;
import fr.frodriguez.biblio.model.Book;
import fr.frodriguez.biblio.model.Defines;
import fr.frodriguez.biblio.simpleelement.SimpleSpinnerAdpater;
import fr.frodriguez.biblio.utils.ContextMenuDefine;
import fr.frodriguez.biblio.utils.IntentExtra;
import fr.frodriguez.library.utils.StringUtils;

/**
 * Created by linux on 07/01/16.
 */
public class BookListActivity extends AppCompatActivity {

    private ListView listView;
    private BookAdaptater bookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Get the listview and populate it
        listView = (ListView) findViewById(R.id.listBooks);
        populateListView();

        // Create the context menu (long click on one element in the listview)
        registerForContextMenu(listView);

        // Open the book activity when clicking on it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the book id
                long bookId = ((Book) listView.getItemAtPosition(position)).getId();
                // Open the details activity
                Intent intent = new Intent(BookListActivity.this, BookViewActivity.class);
                intent.putExtra(IntentExtra.BOOK_ID, bookId);
                startActivity(intent);
            }
        });

        // Manage the search editText
        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        final ImageButton clearButton = (ImageButton) findViewById(R.id.clearButton);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                bookAdapter.filter(searchEditText.getText().toString());

                // If the edit text is empty, hide the clear button
                if(searchEditText.getText().toString().isEmpty()) {
                    clearButton.setVisibility(View.GONE);
                } else {
                    clearButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        // Manage the clear button
        clearButton.setVisibility(View.GONE);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });
    }


    /**
     * Populate the listview with elements from the database
     */
    private void populateListView() {
        // Get all elements in the database
        List<Book> books = Book.getAll();
        // Populate the listview thanks the adapter
        bookAdapter = new BookAdaptater(this, books);
        listView.setAdapter(bookAdapter);
    }

    /**
     * When user comes back to this activity, refresh the listview to keep it up-to-date
     */
    @Override
    protected void onResume() {
        super.onResume();
        // refresh the listview data
        populateListView();
    }

    /**
     * Create a context menu which allows to edit or delete an element
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // If user long clicked the listview (i.e an element inside the listview)
        if(v.getId() == R.id.listBooks) {
            // Create a context menu for this element (a little popup)
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            // The title of this popup is the name of the clicked element
            String title;
            Book book  = bookAdapter.getItem(acmi.position);
            if(book != null) {
                title = book.getTitle();
            }
            else {
                return;
            }
               menu.setHeaderTitle(title);

            // Add an edit button
            menu.add(Menu.NONE, ContextMenuDefine.EDIT, Menu.NONE, R.string.edit);
            // Add a delete button
            menu.add(Menu.NONE, ContextMenuDefine.DELETE, Menu.NONE, R.string.delete);
        }
    }

    /**
     * Called when a button of the context menu is clicked
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get the selected element position
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;

        // Switch on the different buttons available
        switch (item.getItemId()) {
            case ContextMenuDefine.DELETE:
                Book book = bookAdapter.getItem(position);
                if(book != null) {
                    // Delete the element from the listview
                    bookAdapter.remove(book);
                    // Delete the element from the database
                    Book.delete(Book.class, book.getId());

                }
                return true;

            case ContextMenuDefine.EDIT:
                // Open the 'edit' activity for the element
                // Get the book id
                long bookId = ((Book) listView.getItemAtPosition(position)).getId();
                Intent intent = new Intent(BookListActivity.this, BookEditActivity.class);
                intent.putExtra(IntentExtra.BOOK_ID, bookId);
                startActivity(intent);

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Add a +/add button to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the +/add button
        getMenuInflater().inflate(R.menu.menu_add, menu);
        // Return true to open the menu
        return true;
    }

    /**
     * Handle click on the button added just above
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the selected item is the +/add button
        if(item.getItemId() == R.id.itemMenuAdd) {

            // Create a popup for adding a new alement
            final AlertDialog popup = new AlertDialog.Builder(this)
                    .setTitle(R.string.addBook)
                    .setPositiveButton(R.string.save, null) // override below to prevent autoclosing
                    .setNegativeButton(R.string.cancel, null)
                    .setView(R.layout.dialog_add_book)
                    .create();

            // Auto open keyboard
            if(popup.getWindow() != null) {
                popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            popup.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    populateAuthorsSpinner(popup);

                    // On save button
                    popup.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Try to add the new element
                            if(validateNewElement(popup)) {
                                // On success, close the popup
                                popup.dismiss();
                            }
                        }
                    });
                }
            });
            popup.show();
        }
        return true;
    }

    /**
     * Try to add a new element
     * @param popup the popup containing the form to validate
     * @return true if the element is added and the popup has to be closed, else false
     */
    private boolean validateNewElement(AlertDialog popup) {
        // Get the editTexts and the values to check (title and subtitle)
        EditText title = (EditText) popup.findViewById(R.id.dialogBookTitle);
        EditText subtitle = (EditText) popup.findViewById(R.id.dialogBookSubtitle);

        // If the input is valid
        if(checkUserInput(title, subtitle)) {
            // Save the new element
            Book book = new Book();

            // Title & subtitle
            book.setTitle(title.getText().toString());
            book.setSubtitle(subtitle.getText().toString());

            // Year
            EditText editText = (EditText) popup.findViewById(R.id.dialogBookYear);
            String value = editText.getText().toString();
            if(!StringUtils.isEmpty(value)) book.setYear(Integer.parseInt(value));

            // Number
            editText = (EditText) popup.findViewById(R.id.dialogBookNumber);
            value = editText.getText().toString();
            if(!StringUtils.isEmpty(value)) book.setNumber(Integer.parseInt(value));

            // Bookshelf number
            editText = (EditText) popup.findViewById(R.id.dialogBookshelfnumber);
            value = editText.getText().toString();
            if(!StringUtils.isEmpty(value)) book.setBookshelfnumber(Integer.parseInt(value));

            // Got
            Switch aSwitch = (Switch) popup.findViewById(R.id.dialogBookGot);
            book.setGot(aSwitch.isChecked());

            // Rating
            RatingBar ratingBar = (RatingBar) popup.findViewById(R.id.dialogBookRate);
            book.setRate((int) ratingBar.getRating());

            // Description
            editText = (EditText) popup.findViewById(R.id.dialogBookDescription);
            value = editText.getText().toString();
            if(!StringUtils.isEmpty(value)) book.setDescription(value);

            // Save the new element
            book.save();
            // Add the element to the listview
            bookAdapter.add(book);
            return true;
        }
        return false;
    }

    /**
     * Check if the title and subtitle are valid for a new book
     * @param title
     * @param subtitle
     * @return true if the title and subtitle are valid
     */
    private boolean checkUserInput(EditText title, EditText subtitle) {
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
        else if(coupleAvailable == Defines.VALUE_ERROR_USED) {
            title.setError(getResources().getString(R.string.errorTitleSubtitleNotAvailable));
            return false;
        }
        return false;
    }

    //TODO populate author and other lists
    private void populateAuthorsSpinner(AlertDialog dialog) {
        // Get all authors in the database
        List<Author> authors = Author.getAll(Author.class);

        Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogBookAuthors);
        SimpleSpinnerAdpater<Author> spinnerAdpater = new SimpleSpinnerAdpater<>(this, authors);
        spinner.setAdapter(spinnerAdpater);
    }
}
