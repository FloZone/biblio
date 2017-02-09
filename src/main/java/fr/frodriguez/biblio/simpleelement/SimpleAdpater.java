package fr.frodriguez.biblio.simpleelement;

import android.content.Context;
import android.support.annotation.LayoutRes;
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
 * By Florian on 05/02/2017.
 */
//TODO ordre alphabétique
public class SimpleAdpater<Element extends SimpleNamedElement> extends ArrayAdapter<Element> {

    private List<Element> elements;

    public SimpleAdpater(Context context, List<Element> elements) {
        super(context, 0, elements);
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Nullable
    @Override
    public Element getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Get the view used in the dropdown view or when a element is selected
     */
    private View getView(int position, View convertView, @NonNull ViewGroup parent, @LayoutRes int resource) {
        if(convertView == null) {
            // Get a simple default layout
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(elements.get(position).toString());

        return convertView;
    }

    @Override
    public int getPosition(Element item) {
        return elements.indexOf(item);
    }

    @Override
    public void add(Element element) {
        elements.add(element);
    }

    @Override
    public void remove(Element element) {
        elements.remove(element);
    }
}
