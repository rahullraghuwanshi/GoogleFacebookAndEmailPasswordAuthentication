package com.thisrahul.quantumassignment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class LoginSignUpAdapter extends FragmentPagerAdapter {

    //arraylist for fragments
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    //arraylist for titles
    private final ArrayList<String> titleArrayList = new ArrayList<>();

    //constructor
    public LoginSignUpAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    //method to add fragment and title in list
    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        titleArrayList.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleArrayList.get(position);
    }
}
