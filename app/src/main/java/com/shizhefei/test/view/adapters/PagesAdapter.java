package com.shizhefei.test.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.mvc.demo.R;

public class PagesAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
    private Class[] fragmentsClass;

    public PagesAdapter(FragmentManager fragmentManager, Class[] fragmentsClass) {
        super(fragmentManager);
        this.fragmentsClass = fragmentsClass;
    }

    @Override
    public int getCount() {
        return fragmentsClass.length;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.tab_top, container, false);
        }
        TextView textView = (TextView) convertView;
        String tabName = fragmentsClass[position].getSimpleName().replace("Fragment", "");
        textView.setText(tabName);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        try {
            return (Fragment) fragmentsClass[position].newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Fragment();
    }
}