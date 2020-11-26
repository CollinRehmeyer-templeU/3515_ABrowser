package edu.temple.abrowser;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    private ListView bookmarkListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkListView = findViewById(R.id.bookmark_list);

        ArrayList<String> bookmarkNamesList = new ArrayList<String>();

        for(File f : getFilesDir().listFiles())
        {
            bookmarkNamesList.add(f.getName());
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookmarkNamesList);

        bookmarkListView.setAdapter(myAdapter);

        bookmarkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView v = (TextView)view;

                String filename = v.getText().toString();
                Bookmark b2 = null;

                try
                {
                    FileInputStream fis = openFileInput(filename);
                    ObjectInputStream ois = new ObjectInputStream(fis);

                    b2 = (Bookmark) ois.readObject();
                    fis.close();
                    ois.close();
                    Log.d("BOOKMARK ACTIVITY", "TITLE: " + b2.getTitle() + ", URL: " + b2.getUrl());
                }
                catch(Exception e)
                {
                    Log.d("BOOKMARK ACTIVITY", "ERROR READING FILE");
                }



                if(b2 != null)
                {
                    Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                    intent.putExtra(BrowserActivity.BOOKMARK_URL_KEY, b2.getUrl());

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                    intent.putExtra(BrowserActivity.BOOKMARK_URL_KEY, "");

                    setResult(RESULT_CANCELED, intent);
                    finish();
                }


            }

        });
    }
}