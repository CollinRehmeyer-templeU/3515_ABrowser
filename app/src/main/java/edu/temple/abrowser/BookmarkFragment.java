package edu.temple.abrowser;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {

    private BookmarkInterface callback;

    public BookmarkFragment() {

    }


    public static BookmarkFragment newInstance() {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof BookmarkInterface) {
            callback = (BookmarkInterface) context;
        } else {
            throw new RuntimeException("You must implement BookmarkInterface to attach this fragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_bookmark, container, false);

        v.findViewById(R.id.new_bookmark_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.createBookmark();
            }
        });

        return v;
    }


    public interface BookmarkInterface
    {
        public void createBookmark();
    }
}