package com.ryx.epubtest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.readium.r2.shared.Link;

import java.util.List;

public class BookPagerAdapter extends FragmentStatePagerAdapter {

    private List<Link> links;
    private String mBookFileName;
    private int mPortNumber;

    public BookPagerAdapter(@NonNull FragmentManager fm, List<Link> links,
                            String mBookFileName, int mPortNumber) {
        super(fm);
        this.links = links;
        this.mBookFileName = mBookFileName;
        this.mPortNumber = mPortNumber;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ChapterFragment.newInstance(mBookFileName, mPortNumber, links.get(position));
    }

    @Override
    public int getCount() {
        return links.size();
    }
}
