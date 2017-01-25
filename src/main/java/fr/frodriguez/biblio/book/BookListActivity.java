package fr.frodriguez.biblio.book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Author;
import fr.frodriguez.biblio.model.Book;
import fr.frodriguez.biblio.utils.ContextMenuDefine;
import fr.frodriguez.biblio.utils.IntentData;

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
                Intent intent = new Intent(BookListActivity.this, BookDetailsActivity.class);
                intent.putExtra(IntentData.BOOK_ID, bookId);
                startActivity(intent);
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
                    // Delete the element from the database
                    Book.delete(Book.class, book.getId());
                    // Delete the element from the listview
                    bookAdapter.remove(position);

                }
                return true;

            case ContextMenuDefine.EDIT:
                // Open an 'edit' popup for the element
                editElement(position);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Open a popup for editing the given element
     * @param position position of the element to edit
     */
    private void editElement(final int position) {
        //TODO create popup etc, same thing than create
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
                    .setTitle(R.string.booksDialogTitle)
                    .setPositiveButton(R.string.save, null) // override below to prevent autoclosing
                    .setNegativeButton(R.string.cancel, null)
                    .setView(R.layout.dialog_add_book)
                    .create();

            // Auto open keyboard
            if(popup.getWindow() != null) {
                popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            // click sur bouton valider (passer par le dialogBuilder ferme automatiquement, ici non)
            popup.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    //TODO populate author list
                    //populateAuthorsSpinner((Dialog)dialog);

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

    //TODO here
    // sauvegarder le livre
    private boolean validateNewElement(AlertDialog popup) {
        EditText title = (EditText) popup.findViewById(R.id.dialogBookTitle);
        String titleStr = title.getText().toString();

        EditText subtitle = (EditText) popup.findViewById(R.id.dialogBookSubtitle);
        String subtitleStr = subtitle.getText().toString();

        if (titleStr.length() > 0) {

            // si on a déjà un livre avec le même titre (& sous titre si renseigné)
            /* non fonctionnel:
             ouvrir l'appli, créer deux livres "A": erreur pour le second
             fermer l'appli et l'ouvrir à nouveau, on peut créer un livre "A" (alors que le premier existe toujours)
             mais erreur si on en créer un 3ème (impossible d'en créer deux identiques sur la même session)
            Book existingBook = Book.getByTitleAndSubtitle(titleStr, subtitleStr);
            if (existingBook != null) {
                title.setError(getResources().getString(R.string.booksDialogErrorExists));
            } else {*/
                // sinon créer le livre
                Book book = new Book();

                // titre
                book.setTitle(titleStr);

                // sous titre
                if (subtitleStr.length() > 0) book.setSubtitle(subtitleStr);

                // année
                EditText editText = (EditText) popup.findViewById(R.id.dialogBookYear);
                String value = editText.getText().toString();
                if (value.length() > 0) book.setYear(Integer.parseInt(value));

                // numéro
                editText = (EditText) popup.findViewById(R.id.dialogBookNumber);
                value = editText.getText().toString();
                if (value.length() > 0) book.setNumber(Integer.parseInt(value));

                // cote
                editText = (EditText) popup.findViewById(R.id.dialogBookshelfnumber);
                value = editText.getText().toString();
                if (value.length() > 0) book.setBookshelfnumber(Integer.parseInt(value));

                // possédé
                Switch aSwitch = (Switch) popup.findViewById(R.id.dialogBookGot);
                book.setGot(aSwitch.isChecked());

                // note
                RatingBar ratingBar = (RatingBar) popup.findViewById(R.id.dialogBookRate);
                book.setRate((int) ratingBar.getRating());

                // description
                editText = (EditText) popup.findViewById(R.id.dialogBookDescription);
                value = editText.getText().toString();
                if (value.length() > 0) book.setDescription(value);

                // le sauvegarder
                book.save();
                Book savedBook = Book.getById(book.getId());

                // l'ajouter à la liste et l'actualiser
                bookAdapter.add(savedBook);

                return true;
            //}
        } else {
            title.setError(getResources().getString(R.string.booksDialogErrorTitle));
            return false;
        }
    }

    // TODO liste des auteurs
    private void populateAuthorsSpinner(Dialog dialog) {
        Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogBookAuthors);
        // récupérer les auteurs
        List<Author> authors = Author.getAll(Author.class);
        // créer la liste
        List<String> authorsStr = new ArrayList<>();
        for(Author author : authors) {
            authorsStr.add(author.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, authorsStr);
        spinner.setAdapter(adapter);
    }
}
