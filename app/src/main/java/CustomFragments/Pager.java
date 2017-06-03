package CustomFragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by viper on 12/09/16.
 */
public class Pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int pos) {
        switch(pos) {

            case 0: return FirstFragment.newInstance("News Feeds, #1");
            case 1: return FriendsFragment.newInstance("My Friends, #1");
            default: return ThirdFragment.newInstance("ThirdFragment, Default");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
