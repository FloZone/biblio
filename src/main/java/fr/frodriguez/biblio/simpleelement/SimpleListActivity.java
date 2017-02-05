package fr.frodriguez.biblio.simpleelement;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Defines;
import fr.frodriguez.biblio.model.SimpleNamedElement;
import fr.frodriguez.biblio.utils.ContextMenuDefine;

/**
 * By Florian on 11/01/2017.
 */

/**
 * An activity which displays a listview of element and allow to add/edit/remove them
 * @param <Element> the type of the element to manage which implement SimpleNamedElement
 */
public abstract class SimpleListActivity<Element extends SimpleNamedElement> extends AppCompatActivity {

    // The type of the elements to manage
    private Class<Element> elementClass = null;
    // The listview adapter
    private SimpleListviewAdapter<Element> simpleListviewAdapter;
    // And the listview
    private ListView listView;


    /**
     * This method must be used before calling super.onCreate(savedInstanceState).
     * It is necessary to set the type of element to manage.
     * @param type the type of element to manage
     */
    public void setElementClass(Class<Element> type) {
        this.elementClass = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(elementClass == null) {
            //TODO faire une class logger + creer une exception perso
            Log.e("TAG", "Error, you must call setElementClass(Element.class) " +
                    "before calling super.onCreate(savedInstanceState)");
            return;
        }

        // Layout with a listview, a '+' button and a popup to set element info
        setContentView(R.layout.activity_simple_name_list);

        // Get the listview and populate it
        listView = (ListView) findViewById(R.id.listItems);
        populateListView();

        // Create the context menu (long click on one element in the listview)
        registerForContextMenu(listView);

        // Manage the search editText
        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        final ImageButton clearButton = (ImageButton) findViewById(R.id.clearButton);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                simpleListviewAdapter.filter(searchEditText.getText().toString());

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
        List<Element> elements = Element.getAll(elementClass);
        // Populate the listview thanks the simple adapter
        simpleListviewAdapter = new SimpleListviewAdapter<>(this, elements);
        listView.setAdapter(simpleListviewAdapter);
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
        if(v.getId() == R.id.listItems) {
            // Create a context menu for this element (a little popup)
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            // The title of this popup is the name of the clicked element
            String title;
            Element element = simpleListviewAdapter.getItem(acmi.position);
            if(element != null) {
                title = element.getName();
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
                Element element = simpleListviewAdapter.getItem(position);
                if(element != null) {
                    // Delete the element from the listview
                    simpleListviewAdapter.remove(element);
                    // Delete the element from the database
                    Element.delete(elementClass, element.getId());
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
     * Check if the element form is valid of not
     * @param editText editText containing the value to check
     * @return true if the form is valid, else false
     */
    private boolean checkUserInput(EditText editText) {
        switch (Element.isNameAvailable(elementClass, editText.getText().toString())) {
            case Defines.VALUE_ERROR_EMPTY:
                // If the name is empty
                editText.setError(getResources().getString(R.string.errorMustSetName));
                return false;

            case Defines.VALUE_ERROR_USED:
                // If the name is not available
                editText.setError(getResources().getString(R.string.errorNameNotAvailable));
                return false;

            case Defines.VALUE_OK:
                return true;

            default:
                Log.d("Croustade", "Error code:" );
                return false;
        }
    }

    /**
     * Open a popup for editing the given element
     * @param position position of the element to edit
     */
    private void editElement(final int position) {
        // Create the popup
        final AlertDialog popup = new AlertDialog.Builder(this)
                .setTitle(R.string.edit)
                .setPositiveButton(R.string.save, null) // override after to prevent autoclosing
                .setNegativeButton(R.string.cancel, null)
                .setView(R.layout.dialog_simple_name)
                .create();

        // Auto open keyboard
        if(popup.getWindow() != null) {
            popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        popup.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final String currentName = simpleListviewAdapter.getItem(position).getName();

                // Fill the editText with the element name
                ((EditText) popup.findViewById(R.id.dialogName)).setText(currentName);

                // On save button
                popup.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Try to save the edited element
                        if(validateEditElement(popup, position)) {
                            // Close the popup
                            popup.dismiss();
                        }
                    }
                });
            }
        });
        // Show the popup
        popup.show();
    }

    /**
     * Try to save the edited element
     * @param popup the popup containing the form to validate
     * @param position the position in the listview of the element to edit
     * @return true if the element is savec and the popup has to be closed, else false
     */
    private boolean validateEditElement(AlertDialog popup, int position) {
        // Get the editText and its value
        EditText editText = (EditText) popup.findViewById(R.id.dialogName);
        String newName = editText.getText().toString();

        // Element to edit is null or no change
        Element elementToEdit = simpleListviewAdapter.getItem(position);
        if(elementToEdit == null || elementToEdit.getName().equals(newName)) {
            return true;
        }

        // If the input is valid
        if(checkUserInput(editText)) {
            simpleListviewAdapter.updateElement(position, newName);
            return true;
        }
        return false;
    }

    /**
     * Add a +/add button to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    .setTitle(R.string.addElement)
                    .setPositiveButton(R.string.save, null) // override below to prevent autoclosing
                    .setNegativeButton(R.string.cancel, null)
                    .setView(R.layout.dialog_simple_name)
                    .create();

            // Auto open keyboard
            if(popup.getWindow() != null) {
                popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            popup.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
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
            // Show the popup
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
        // Get the editText
        EditText editText = (EditText) popup.findViewById(R.id.dialogName);

        // If the input is valid
        if(checkUserInput(editText)) {
            try {
                // Save the new element
                String newName = editText.getText().toString();
                Element newElement = elementClass.newInstance();
                newElement.updateName(newName);
                // Add the element to the listview
                simpleListviewAdapter.add(newElement);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
