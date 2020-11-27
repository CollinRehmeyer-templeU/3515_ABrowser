package edu.temple.abrowser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    private ListView bookmarkListView;
    private boolean DELETE_MODE;
    private ArrayList<String> bookmarkNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        DELETE_MODE = false;
        bookmarkListView = findViewById(R.id.bookmark_list);
        bookmarkNamesList = new ArrayList<String>();

        loadBookmarks();

        bookmarkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!DELETE_MODE) {
                    TextView v = (TextView) view;

                    String filename = v.getText().toString();
                    Bookmark b2 = null;

                    try {
                        FileInputStream fis = openFileInput(filename);
                        ObjectInputStream ois = new ObjectInputStream(fis);

                        b2 = (Bookmark) ois.readObject();
                        fis.close();
                        ois.close();
                        Log.d("BOOKMARK ACTIVITY", "TITLE: " + b2.getTitle() + ", URL: " + b2.getUrl());
                    } catch (Exception e) {
                        Log.d("BOOKMARK ACTIVITY", "ERROR READING FILE");
                    }


                    if (b2 != null) {
                        Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                        intent.putExtra(BrowserActivity.BOOKMARK_URL_KEY, b2.getUrl());

                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                        intent.putExtra(BrowserActivity.BOOKMARK_URL_KEY, "");

                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                }
                else {
                    //Delete this bookmark
                    TextView v = (TextView) view;

                    String filename = v.getText().toString();
                    deleteFile(filename);

                    loadBookmarks();

                }
            }

        }); //End OnClickListener

        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DELETE_MODE = !(DELETE_MODE);
                Log.d("DELETE MODE: ", String.valueOf(DELETE_MODE));


                Button b = (Button)v;
                if(DELETE_MODE)
                    b.setText("Done");
                else
                    b.setText("Delete Bookmarks");

            }
        });

    } //OnCreate()


    public void loadBookmarks()
    {
        bookmarkNamesList.clear();

        for(File f : getFilesDir().listFiles())
        {
            bookmarkNamesList.add(f.getName());
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookmarkNamesList);

        bookmarkListView.setAdapter(myAdapter);

    }
}