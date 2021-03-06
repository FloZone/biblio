package fr.frodriguez.biblio.element.book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import fr.frodriguez.biblio.model.utils.Defines;
import fr.frodriguez.biblio.model.Format;
import fr.frodriguez.biblio.model.Serie;
import fr.frodriguez.biblio.model.Theme;
import fr.frodriguez.biblio.simpleelement.SimpleAdpater;
import fr.frodriguez.biblio.utils.ContextMenuDefine;
import fr.frodriguez.biblio.utils.IntentExtra;
import fr.frodriguez.library.utils.StringUtils;

/**
 * By FloZone on 07/01/16.
 */
public class BookListActivity extends AppCompatActivity {

    private ListView listView;
    private BookAdaptater bookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Get the books listview and populate it
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
                    populateAuthorsSpinner((Spinner) popup.findViewById(R.id.dialogBookAuthors));
                    populateFormatsSpinner((Spinner) popup.findViewById(R.id.dialogBookFormats));
                    populateSeriesSpinner((Spinner) popup.findViewById(R.id.dialogBookSeries));
                    populateThemesSpinner((Spinner) popup.findViewById(R.id.dialogBookSeries));


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
     * Check if the title and subtitle are valid for a new book
     * @param etTitle editText containing the book title
     * @param etSubtitle editText containing the book subtitle
     * @return true if the title and subtitle are valid
     */
    private boolean checkUserInput(EditText etTitle, EditText etSubtitle) {
        int titleAvailable = Book.isTitleAvailable(etTitle.getText().toString());
        int coupleAvailable = Book.isTitleAndSubtitleAvailable(etTitle.getText().toString(), etSubtitle.getText().toString());

        // If the title is available
        if(titleAvailable == Defines.VALUE_OK) {
            return true;
        }
        // If the title is empty
        else if(titleAvailable == Defines.VALUE_ERROR_EMPTY) {
            etTitle.setError(getResources().getString(R.string.errorMustSetTitle));
            return false;
        }
        // If the couple title/subtitle is available
        else if(coupleAvailable == Defines.VALUE_OK) {
            return true;
        }
        else if(coupleAvailable == Defines.VALUE_ERROR_USED) {
            etTitle.setError(getResources().getString(R.string.errorTitleSubtitleNotAvailable));
            return false;
        }
        return false;
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

            // Author
            Spinner spinner = (Spinner) popup.findViewById(R.id.dialogBookAuthors);
            Author author = (Author) spinner.getSelectedItem();
            if(author != null) {
                Log.d("Croustade", "Selected author: " + author.toString());
                book.setAuthor(author);
            }

            // Format
            spinner = (Spinner) popup.findViewById(R.id.dialogBookFormats);
            Format format = (Format) spinner.getSelectedItem();
            if(format != null) {
                Log.d("Croustade", "Selected format: " + format.toString());
                book.setFormat(format);
            }

            // Serie
            spinner = (Spinner) popup.findViewById(R.id.dialogBookSeries);
            Serie serie = (Serie) spinner.getSelectedItem();
            if(serie != null) {
                Log.d("Croustade", "Selected serie: " + serie.toString());
                book.setSerie(serie);
            }

            // Theme
            spinner = (Spinner) popup.findViewById(R.id.dialogBookThemes);
            Theme theme = (Theme) spinner.getSelectedItem();
            if(theme != null) {
                Log.d("Croustade", "Selected theme: " + theme.toString());
                book.setTheme(theme);
            }

            // Save the new element
            book.save();
            // Add the element to the listview
            bookAdapter.add(book);
            return true;
        }
        return false;
    }

    /**
     * Fill the spinner with authors from the database
     * @param authorsSpinner the spinner which will contains the authors list
     */
    private void populateAuthorsSpinner(Spinner authorsSpinner) {
        // Get all authors in the database
        List<Author> authors = Author.getAll(Author.class);

        SimpleAdpater<Author> spinnerAdpater = new SimpleAdpater<>(this, authors);
        authorsSpinner.setAdapter(spinnerAdpater);
    }

    /**
     * Fill the spinner with formats from the database
     * @param formatsSpinner the spinner which will contains the formats list
     */
    private void populateFormatsSpinner(Spinner formatsSpinner) {
        // Get all authors in the database
        List<Format> formats = Format.getAll(Format.class);

        SimpleAdpater<Format> spinnerAdpater = new SimpleAdpater<>(this, formats);
        formatsSpinner.setAdapter(spinnerAdpater);
    }

    /**
     * Fill the spinner with series from the database
     * @param seriesSpinner the spinner which will contains the series list
     */
    private void populateSeriesSpinner(Spinner seriesSpinner) {
        // Get all authors in the database
        List<Serie> series = Serie.getAll(Serie.class);

        SimpleAdpater<Serie> spinnerAdpater = new SimpleAdpater<>(this, series);
        seriesSpinner.setAdapter(spinnerAdpater);
    }


    /**
     * Fill the spinner with themes from the database
     * @param themesSpinner the spinner which will contains the themes list
     */
    private void populateThemesSpinner(Spinner themesSpinner) {
        // Get all authors in the database
        List<Theme> themes = Theme.getAll(Theme.class);

        SimpleAdpater<Theme> spinnerAdpater = new SimpleAdpater<>(this, themes);
        themesSpinner.setAdapter(spinnerAdpater);
    }
}
