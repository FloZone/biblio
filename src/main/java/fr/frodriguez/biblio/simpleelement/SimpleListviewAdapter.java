package fr.frodriguez.biblio.simpleelement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.frodriguez.biblio.model.utils.SimpleNamedElement;

/**
 * By Florian on 10/01/2017.
 */

/**
 * An adapter which manages a simple listview
 * @param <Element> the type of the element to manage which implement SimpleNamedElement
 */
//TODO ordre alphabétique
public class SimpleListviewAdapter<Element extends SimpleNamedElement> extends ArrayAdapter<Element> {

    // List of elements to display
    private List<Element> displayedElements;
    // List of all elements
    private List<Element> allElements;
    // Current filter value
    private String filterValue = "";


    public SimpleListviewAdapter(Context context, List<Element> displayedElements) {
        super(context, 0, displayedElements);
        this.displayedElements = displayedElements;
        this.allElements = new ArrayList<>(displayedElements);
    }

    @Override
    public int getCount() {
        return displayedElements.size();
    }

    @Nullable
    @Override
    public Element getItem(int position) {
        return displayedElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Get a simple default layout
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Set the display value of the element
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(displayedElements.get(position).toString());

        return convertView;
    }

    /**
     * Add an element to the list
     * @param element the element to add
     */
    @Override
    public void add(Element element) {
        // Add the element to the list
        allElements.add(element);
        // Add to the displayed elements only if it matches to the filter value
        if(element.getName().toLowerCase().contains(filterValue)) {
            displayedElements.add(element);
            notifyDataSetChanged();
        }
    }

    /**
     * Remove an element from the list
     * @param element the element to remove
     */
    @Override
    public void remove(Element element) {
        displayedElements.remove(element);
        allElements.remove(element);
        notifyDataSetChanged(); // update the listview
    }

    /**
     * Update an element
     * @param position position of the element to update
     * @param newName new name value to set
     */
    public void updateElement(int position, String newName) {
        // Update the element from the displayed list
        Element element = displayedElements.get(position);
        element.updateName(newName);
        // Also update the allElements list
        allElements.set(position, element);
        // Refresh the listview
        notifyDataSetChanged();
    }

    /**
     * Filter the displayed list on the given filter value
     * @param filter
     */
    public void filter(@NonNull String filter) {
        // Non case-sensitive filtering
        filterValue = filter.toLowerCase();

        // Clear the displayed list
        displayedElements.clear();

        // Display all elements if the filter value is empty
        if(filter.isEmpty()) {
            displayedElements.addAll(allElements);
        }
        // Else filter
        else {
            // For each database element
            for(Element element : allElements) {
                // If its name contains the given filter value, display it
                if(element.getName().toLowerCase().contains(filterValue)) {
                    displayedElements.add(element);
                }
            }
        }
        // Refresh the listview
        notifyDataSetChanged();
    }

}
