package fr.frodriguez.biblio.element.book;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Book;

/**
 * By FloZone on 29/01/16.
 */
public class BookAdaptater extends ArrayAdapter<Book> {

    // List of books to display
    private List<Book> displayedBooks;
    // List of all books
    private ArrayList<Book> allBooks;
    // Current filter value
    private String filterValue = "";


    public BookAdaptater(Context context, List<Book> displayedBooks) {
        super(context, 0, displayedBooks);
        this.displayedBooks = displayedBooks;
        this.allBooks = new ArrayList<>(displayedBooks);
    }

    @Override
    public int getCount() {
        return displayedBooks.size();
    }

    @Nullable
    @Override
    public Book getItem(int position) {
        return displayedBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Get the book layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_book, parent, false);
        }

        // Set the image, title and subtitle
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.book);
        TextView firstLine = (TextView) convertView.findViewById(R.id.firstLine);
        firstLine.setText(displayedBooks.get(position).getTitle());
        TextView secondLine = (TextView) convertView.findViewById(R.id.secondLine);
        secondLine.setText(displayedBooks.get(position).getSubtitle());

        return convertView;
    }

    /**
     * Add an book to the list
     * @param book the book to add
     */
    @Override
    public void add(Book book) {
        // Add the book to the list
        allBooks.add(book);
        // Add to the displayed books only if it matches to the filter value
        if(book.getTitle().toLowerCase().contains(filterValue)
                || book.getSubtitle().toLowerCase().contains(filterValue)) {
            displayedBooks.add(book);
            notifyDataSetChanged();
        }
    }

    /**
     * Remove an book from the list
     * @param book the book to remove
     */
    @Override
    public void remove(Book book) {
        displayedBooks.remove(book);
        allBooks.remove(book);
        notifyDataSetChanged(); // update the listview
    }

    /**
     * Filter the displayed list on the given filter value
     */
    public void filter(@NonNull String filter) {
        // Non case-sensitive filtering
        filterValue = filter.toLowerCase();

        // Clear the displayed list
        displayedBooks.clear();

        // Display all books if the filter value is empty
        if(filter.isEmpty()) {
            displayedBooks.addAll(allBooks);
        }
        // Else filter
        else {
            // For each database book
            for(Book element : allBooks) {
                // If its title or the subtitle contains the given filter value, display it
                if(element.getTitle().toLowerCase().contains(filterValue)
                        || element.getSubtitle().toLowerCase().contains(filterValue)) {
                    displayedBooks.add(element);
                }
            }
        }
        // Refresh the listview
        notifyDataSetChanged();
    }
}

