package fr.frodriguez.biblio.generic;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.utils.ContextMenuDefine;
import fr.frodriguez.biblio.utils.Utils;

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
    private SimpleAdapter<Element> simpleAdapter;
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
        }

        // Layout with a listview, a '+' button and a popup to set element info
        setContentView(R.layout.activity_simple_name_list);

        // Get the listview and populate it
        listView = (ListView) findViewById(R.id.listItems);
        populateListView();

        // Create the context menu (long click on one element in the listview)
        registerForContextMenu(listView);

        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        final ImageButton clearButton = (ImageButton) findViewById(R.id.clearButton);

        // Manage the search editText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                simpleAdapter.filter(searchEditText.getText().toString());

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
        simpleAdapter = new SimpleAdapter<>(this, elements);
        listView.setAdapter(simpleAdapter);
    }

    /**
     * When user comes back to this activity, refresh the listview to keep up-to-date
     */
    @Override
    protected void onResume() {
        super.onResume();
        // refresh the listview data
        populateListView();
    }

    /**
     * Create a context menu which allows to edit or delete an element
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // If user long clicked the listview (i.e an element inside the listview)
        if(v.getId() == R.id.listItems) {
            // Create a context menu for this element (a little popup)
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            // The title of this popup is the name of the clicked element
            String title;
            Element element = simpleAdapter.getItem(acmi.position);
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
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get the selected element position
        int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;

        // Switch on the different buttons available
        switch (item.getItemId()) {
            case ContextMenuDefine.DELETE:
                Element element = simpleAdapter.getItem(position);
                if(element != null) {
                    // Delete the element from the database
                    Element.delete(elementClass, element.getId());
                    // Delete the element from the listview
                    simpleAdapter.remove(element);
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
                final String currentName = simpleAdapter.getItem(position).getName();

                // Fill the editText with the element name
                ((EditText) popup.findViewById(R.id.dialogName)).setText(currentName);

                // On save button, check the new name
                popup.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the new name
                        EditText editText = (EditText) popup.findViewById(R.id.dialogName);
                        String newName = editText.getText().toString();

                        // If the editText is empty
                        if(newName.isEmpty()) {
                            editText.setError(getResources().getString(R.string.errorMustSetName));
                        }
                        // Else if no change for the name, just close the popup
                        else if(currentName.equals(newName)) {
                            popup.dismiss();
                        }
                        // Else if the name is not available
                        else if(!Element.isNameAvailable(elementClass, newName)) {
                            editText.setError(getResources().getString(R.string.errorNameNotAvailable));
                        }
                        // Else, update the element
                        else {
                            simpleAdapter.updateElement(position, newName);
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
     * Add a +/add button to the action bar
     * @param menu
     * @return
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
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the selected item is the +/add button
        if(item.getItemId() == R.id.itemMenuAdd) {

            // Create a popup for adding a new alement
            final AlertDialog popup = new AlertDialog.Builder(this)
                    .setTitle(R.string.addElement)
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
                    // On save button, chech the new name
                    popup.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the new name
                            EditText editText = (EditText) popup.findViewById(R.id.dialogName);
                            String newName = editText.getText().toString();

                            // If the editText is empty
                            if(newName.isEmpty()) {
                                editText.setError(getResources().getString(R.string.errorMustSetName));
                            }
                            // Else if the name is not available
                            else if(!Element.isNameAvailable(elementClass, newName)) {
                                editText.setError(getResources().getString(R.string.errorNameNotAvailable));
                            }
                            // Else, create and save the new element
                            else {
                                Element newElement;
                                try {
                                    newElement = elementClass.newInstance();
                                    if(!newElement.updateName(newName)) {
                                        Utils.showToast(SimpleListActivity.this,
                                                "Error while creating the element");
                                    }
                                    // Add the element to the listview
                                    simpleAdapter.add(newElement);
                                    // Close the popup
                                    popup.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

}
