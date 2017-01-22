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

        listView = (ListView) findViewById(R.id.listBooks);

        // Click d'un élément dans la liste = ouvrir activité pour le consulter
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book) listView.getItemAtPosition(position);
                Intent intent = new Intent(BookListActivity.this, BookDetailsActivity.class);
                intent.putExtra(IntentData.BOOK_ID, book.getId());
                startActivity(intent);
            }
        });

        // remplir la listview avec les éléments en bdd
        populateListView();

        // créer le menu contextuel (= appui long sur un élément dans la liste)
        registerForContextMenu(listView);
    }

    // remplir la listview
    private void populateListView() {
        // récupérer tous les livres en base
        List<Book> books = Book.getAll();
        bookAdapter = new BookAdaptater(this, books);
        listView.setAdapter(bookAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // rafraichir la liste quand on retourne sur cette activité
        populateListView();
    }


    @Override
    // créer menu contextuel (= appui long)
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listBooks) {
            // Titre de la popup = titre du livre
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String title = (bookAdapter.getItem(acmi.position)).getTitle();
            menu.setHeaderTitle(title);

            // boutons annuler et supprimer
            menu.add(Menu.NONE, ContextMenuDefine.CANCEL, Menu.NONE, R.string.cancel);
            menu.add(Menu.NONE, ContextMenuDefine.DELETE, Menu.NONE, R.string.delete);
        }
    }

    @Override
    // click sur un élément du menu contextuel
    public boolean onContextItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case ContextMenuDefine.DELETE:
                // récupérer l'id de l'élément
                int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                // le supprimer de la bdd et de la liste
                Book.delete(Book.class, bookAdapter.getItem(position).getId());
                bookAdapter.remove(position);
                return true;
            case ContextMenuDefine.CANCEL:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    // créer le menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    // gère le clic les items du menu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemMenuAdd) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder
                    .setTitle(R.string.booksDialogTitle)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save, null) // override après
                    .setNegativeButton(R.string.cancel, null);
            // charger le layout
            dialogBuilder.setView(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_add_book, null));

            final AlertDialog alertDialog = dialogBuilder.create();

            // redimensionner pour pouvoir scroller
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            // click sur bouton valider (passer par le dialogBuilder ferme automatiquement, ici non)
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    // remplir la liste des auteurs
                    populateAuthorsSpinner((Dialog)dialog);

                    // ajouter la validation
                    ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // sauvegarder le livre
                            saveBook((AlertDialog) dialog);
                        }
                    });
                }
            });
            alertDialog.show();

        }
        return true;
    }


    // sauvegarder le livre
    private void saveBook(Dialog dialog) {
        EditText title = (EditText) dialog.findViewById(R.id.dialogBookTitle);
        String titleStr = title.getText().toString();

        EditText subtitle = (EditText) dialog.findViewById(R.id.dialogBookSubtitle);
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
                EditText editText = (EditText) dialog.findViewById(R.id.dialogBookYear);
                String value = editText.getText().toString();
                if (value.length() > 0) book.setYear(Integer.parseInt(value));

                // numéro
                editText = (EditText) dialog.findViewById(R.id.dialogBookNumber);
                value = editText.getText().toString();
                if (value.length() > 0) book.setNumber(Integer.parseInt(value));

                // cote
                editText = (EditText) dialog.findViewById(R.id.dialogBookshelfnumber);
                value = editText.getText().toString();
                if (value.length() > 0) book.setBookshelfnumber(Integer.parseInt(value));

                // possédé
                Switch aSwitch = (Switch) dialog.findViewById(R.id.dialogBookGot);
                book.setGot(aSwitch.isChecked());

                // note
                RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.dialogBookRate);
                book.setRate((int) ratingBar.getRating());

                // description
                editText = (EditText) dialog.findViewById(R.id.dialogBookDescription);
                value = editText.getText().toString();
                if (value.length() > 0) book.setDescription(value);

                // le sauvegarder
                book.save();
                Book savedBook = Book.getById(book.getId());

                // l'ajouter à la liste et l'actualiser
                bookAdapter.add(savedBook);

                dialog.dismiss();
            //}
        } else {
            title.setError(getResources().getString(R.string.booksDialogErrorTitle));
        }
    }

    // TODO à terminer
    // remplir la liste des auteurs
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
