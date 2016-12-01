package com.shizhefei.test.controllers.mvchelpers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shizhefei.test.controllers.mvchelpers.cool.JellyHeaderFragment;
import com.shizhefei.test.controllers.mvchelpers.cool.MaterialHeaderFragment;
import com.shizhefei.test.controllers.mvchelpers.cool.PinContentMaterialHeaderFragment;
import com.shizhefei.test.controllers.mvchelpers.cool.StateHeaderFragment;
import com.shizhefei.test.view.adapters.PagesAdapter;
import com.shizhefei.utils.DisplayUtil;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.mvc.demo.R;

public class CoolActivity extends AppCompatActivity {

    private IndicatorViewPager indicatorViewPager;
    private TextView headTextView;
    private PagesAdapter pagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_views);
        Indicator indicator = (Indicator) findViewById(R.id.moreviews_indicatorView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.moreviews_viewPager);
        headTextView = (TextView) findViewById(R.id.moreviews_headtext_textView);

        Class[] fragments = {
                StateHeaderFragment.class,
                JellyHeaderFragment.class,
                MaterialHeaderFragment.class,
                PinContentMaterialHeaderFragment.class
        };

        indicator.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.primary), DisplayUtil.dipToPix(this, 3)));
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.BLACK, Color.GRAY));
        viewPager.setOffscreenPageLimit(2);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(pagesAdapter = new PagesAdapter(getSupportFragmentManager(), fragments));
    }
}
