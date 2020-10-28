package edu.temple.abrowser;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PagerFragment extends Fragment {

    private PagerInterface browserActivity;

    private ViewPager viewPager;

    private ArrayList<PageViewerFragment> pages;

    private static final String PAGES_KEY = "pages";

    public PagerFragment() {}

    public static PagerFragment newInstance(ArrayList<PageViewerFragment> pages) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(PAGES_KEY, pages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pages = (ArrayList) getArguments().getSerializable(PAGES_KEY);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof PagerInterface) {
            browserActivity = (PagerInterface) context;
        } else {
            throw new RuntimeException("You must implement PagerInterface to attach this fragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View l = inflater.inflate(R.layout.fragment_pager, container, false);

        viewPager = l.findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return pages.get(position);
            }

            @Override
            public int getCount() {
                return pages.size();
            }

        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                browserActivity.updateUrl((pages.get(position)).getUrl());
                browserActivity.updateTitle((pages.get(position)).getTitle());
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return l;
    }

    public void notifyWebsitesChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void showPage(int index) {
        viewPager.setCurrentItem(index);
    }

    public void go(String url) {
        pages.get(viewPager.getCurrentItem()).go(url);
    }

    public void back() {
        pages.get(viewPager.getCurrentItem()).back();
    }

    public void forward() {
        pages.get(viewPager.getCurrentItem()).forward();
    }

    public String getCurrentUrl() {
        return (pages.get(viewPager.getCurrentItem())).getUrl();
    }

    public String getCurrentTitle() {
        return (pages.get(viewPager.getCurrentItem())).getTitle();
    }

    public int getCurrentPagePosition() {
        return viewPager.getCurrentItem();
    }

    interface PagerInterface {
        void updateUrl(String url);
        void updateTitle(String title);
    }

}