package edu.temple.abrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity implements PageControlFragment.PageControlInterface, PageViewerFragment.PageViewerInterface, BrowserControlFragment.BrowserControlInterface, PagerFragment.PagerInterface, PageListFragment.PageListInterface, BookmarkFragment.BookmarkInterface {

    FragmentManager fm;

    private final String PAGES_KEY = "pages";
    public static final String BOOKMARK_URL_KEY = "edu.temple.mybrowser.bookmarkurlkey";
    public static final int PICK_BOOKMARK = 3271;
    private String bookmarkUrl;

    PageControlFragment pageControlFragment;
    BrowserControlFragment browserControlFragment;
    PageListFragment pageListFragment;
    PagerFragment pagerFragment;
    BookmarkFragment bookmarkFragment;

    ArrayList<PageViewerFragment> pages;

    boolean listMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            pages = (ArrayList) savedInstanceState.getSerializable(PAGES_KEY);
            Log.d("BROWSER ACTIVITY ", "Got pages list");
            for(PageViewerFragment p : pages)
            {
                Log.d("Page with url: ", p.getUrl());
            }
        }
        else
            pages = new ArrayList<>();

        fm = getSupportFragmentManager();

        listMode = findViewById(R.id.page_list) != null;

        Fragment tmpFragment;

        // If PageControlFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_control)) instanceof PageControlFragment)
            pageControlFragment = (PageControlFragment) tmpFragment;
        else {
            pageControlFragment = new PageControlFragment();
            fm.beginTransaction()
                    .add(R.id.page_control, pageControlFragment)
                    .commit();
        }

        // If BrowserFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.browser_control)) instanceof BrowserControlFragment)
            browserControlFragment = (BrowserControlFragment) tmpFragment;
        else {
            browserControlFragment = new BrowserControlFragment();
            fm.beginTransaction()
                    .add(R.id.browser_control, browserControlFragment)
                    .commit();
        }

        // If PagerFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.page_viewer)) instanceof PagerFragment)
            pagerFragment = (PagerFragment) tmpFragment;
        else {
            pagerFragment = PagerFragment.newInstance(pages);
            fm.beginTransaction()
                    .add(R.id.page_viewer, pagerFragment)
                    .commit();
            Log.d("BROWSER ACTIVITY ", "Created new PagerFragment");
        }

        // If BookmarkFragment already added (activity restarted) then hold reference
        // otherwise add new fragment. Only one instance of fragment is ever present
        if ((tmpFragment = fm.findFragmentById(R.id.bookmark_control)) instanceof BookmarkFragment)
            bookmarkFragment = (BookmarkFragment) tmpFragment;
        else {
            bookmarkFragment = BookmarkFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.bookmark_control, bookmarkFragment)
                    .commit();
        }


        // If fragment already added (activity restarted) then hold reference
        // otherwise add new fragment IF container available. Only one instance
        // of fragment is ever present
        if (listMode) {
            if ((tmpFragment = fm.findFragmentById(R.id.page_list)) instanceof PageListFragment)
                pageListFragment = (PageListFragment) tmpFragment;
            else {
                pageListFragment = PageListFragment.newInstance(pages);
                fm.beginTransaction()
                        .add(R.id.page_list, pageListFragment)
                        .commit();
            }
        }
    }


    /**
     * Clear the url bar and activity title
     */
    private void clearIdentifiers() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        pageControlFragment.updateUrl("");
    }

    // Notify all observers of collections
    private void notifyWebsitesChanged() {
        pagerFragment.notifyWebsitesChanged();
        if (listMode)
            pageListFragment.notifyWebsitesChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list of open pages for activity restart
        outState.putSerializable(PAGES_KEY, pages);
    }

    /**
     * Update WebPage whenever PageControlFragment sends new Url
     * Create new page first if none exists
     * Alternatively, you can create an empty page when the activity first loads
     * @param url to load
     */
    @Override
    public void go(String url) {
        if (pages.size() > 0)
            pagerFragment.go(url);
        else {
            pages.add(PageViewerFragment.newInstance(url));
            notifyWebsitesChanged();
            pagerFragment.showPage(pages.size() - 1);
        }

    }

    /**
     * Go back to previous page when user presses Back in PageControlFragment
     */
    @Override
    public void back() {
        pagerFragment.back();
    }

    /**
     * Go forward to next page when user presses Forward in PageControlFragment
     */
    @Override
    public void forward() {
        pagerFragment.forward();
    }

    /**
     * Update displayed Url in PageControlFragment when Webpage Url changes
     * only if it is the currently displayed page, and not another page
     * @param url to display
     */
    @Override
    public void updateUrl(String url) {
        if (url != null && url.equals(pagerFragment.getCurrentUrl())) {
            pageControlFragment.updateUrl(url);

            // Update the ListView in the PageListFragment - results in updated titles
            notifyWebsitesChanged();
        }
    }

    /**
     * Update displayed page title in activity when Webpage Url changes
     * only if it is the currently displayed page, and not another page
     * @param title to display
     */
    @Override
    public void updateTitle(String title) {
        if (title != null && title.equals(pagerFragment.getCurrentTitle()) && getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        // Results in the ListView in PageListFragment being updated
        notifyWebsitesChanged();
    }

    /**
     * Add a new page/fragment to the list and display it
     */
    @Override
    public void newPage() {
        // Add page to list
        pages.add(new PageViewerFragment());
        // Update all necessary views
        notifyWebsitesChanged();
        // Display the newly created page
        pagerFragment.showPage(pages.size() - 1);
        // Clear the displayed URL in PageControlFragment and title in the activity
        clearIdentifiers();
    }

    /**
     * Display requested page in the PagerFragment
     * @param position of page to display
     */
    @Override
    public void pageSelected(int position) {
        pagerFragment.showPage(position);
    }


    public void createBookmark()
    {
        Bookmark b = new Bookmark(pagerFragment.getCurrentTitle(), pagerFragment.getCurrentUrl());
        Log.d("NEW BOOKMARK --- ", "TITLE: " + b.getTitle() + ", URL: " + b.getUrl());

        String filename = b.getTitle();
        FileOutputStream fos = null;
        FileInputStream fis = null;

        //Writing bookmark to file
        try
        {
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(b);
            Log.d("NEW BOOKMARK --- ", "BOOKMARK WRITTEN TO FILE");
            Toast.makeText(this, "New Bookmark: " + b.getTitle(), Toast.LENGTH_SHORT).show();
            fos.close();
            oos.close();
        }
        catch(Exception e)
        {
            Log.d("FILE ERROR", e.getMessage());
        }

        //List all bookmarks for debugging
        for(File f : getFilesDir().listFiles())
        {
            Log.d("BOOKMARK --- ", f.getName());
        }

        // Reading Bookmark from File for debugging
        /*
        try
        {
            fis = openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Bookmark b2 = (Bookmark)ois.readObject();
            fis.close();
            ois.close();
            Log.d("BOOKMARK FROM FILE --- ", "TITLE: " + b2.getTitle() + ", URL: " + b2.getUrl());

        }
        catch(Exception e)
        {
            Log.d("FILE ERROR", e.getMessage());
        }
        */
    }

    public void openBookmarks()
    {
        Intent intent = new Intent(this, BookmarkActivity.class);
        startActivityForResult(intent, PICK_BOOKMARK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_BOOKMARK) {
            if (resultCode == RESULT_OK) {
                String b = data.getStringExtra(BOOKMARK_URL_KEY);
                Log.d("ACTIVITY RESULT", "bookmark url is: " + b);

                newPage();
                go(b);
            }
        }
    }

}