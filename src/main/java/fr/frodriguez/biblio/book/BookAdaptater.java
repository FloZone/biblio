package fr.frodriguez.biblio.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.frodriguez.biblio.R;
import fr.frodriguez.biblio.model.Book;

/**
 * Created by Florian Rodriguez on 29/01/16.
 */
public class BookAdaptater extends ArrayAdapter<Book> {

    private List<Book> books;


    public BookAdaptater(Context context, List<Book> books) {
        super(context, 0, books);
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_book, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView firstLine = (TextView) convertView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) convertView.findViewById(R.id.secondLine);

        imageView.setImageResource(R.drawable.book);
        firstLine.setText(books.get(position).getTitle());
        secondLine.setText(books.get(position).getSubtitle());

        return convertView;
    }

    @Override
    public void add(Book book) {
        books.add(book);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Book book) {
        books.remove(book);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        books.remove(position);
        notifyDataSetChanged();
    }
}

