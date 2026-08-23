package com.fire.mangareader.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fire.mangareader.fragment.FavoriteFragment;
import com.fire.mangareader.fragment.HistoryFragment;

public class LibraryPagerAdapter extends FragmentStateAdapter {

    public LibraryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new FavoriteFragment();
        return new HistoryFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
