package com.example.andrius.findatweet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.andrius.findatweet.Fragments.OnboardingFragment1;
import com.example.andrius.findatweet.Fragments.OnboardingFragment2;
import com.example.andrius.findatweet.Fragments.OnboardingFragment3;

/**
 * Created by Andrius on 2015-10-14.
 */
public class Onboarding extends FragmentActivity {

    private ViewPager pager;

    private ImageView img_page1,img_page2,img_page3;

    private Button skip;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //We calling setContentView to display the layout defined
        // in activity_onboarding.xml and using the findViewById method
        // to initialize the member variables (ViewPager pager; SmartTabLayout indicator; ButtonFlat skip;ButtonFlat next;

        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager)findViewById(R.id.pager);

        skip = (Button)findViewById(R.id.skip);
        next = (Button)findViewById(R.id.next);

        img_page1 = (ImageView) findViewById(R.id.dot1);
        img_page2 = (ImageView) findViewById(R.id.dot2);
        img_page3 = (ImageView) findViewById(R.id.dot3);

        pager.setAdapter(adapter);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });
    //A handler of the next button, use setCurrentItem along with getCurrentItem to move to the next onboarding screen.
    // Also, if getCurrentItem returns the last onboarding screen, call a method finishOnboarding.
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() == 2) { // The last screen
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        img_page1.setImageResource(R.drawable.dotfill);
                        img_page2.setImageResource(R.drawable.dothalf);
                        img_page3.setImageResource(R.drawable.dothalf);
                        next.setText("Next");
                        break;

                    case 1:
                        img_page1.setImageResource(R.drawable.dothalf);
                        img_page2.setImageResource(R.drawable.dotfill);
                        img_page3.setImageResource(R.drawable.dothalf);
                        next.setText("Next");
                        break;

                    case 2:
                        img_page1.setImageResource(R.drawable.dothalf);
                        img_page2.setImageResource(R.drawable.dothalf);
                        img_page3.setImageResource(R.drawable.dotfill);
                        next.setText("Start");

                        break;




                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
//  FragmentStatePagerAdapter that the ViewPager can use to display the onboarding screens.
    FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            //A switch statement to the getItem method to return the correct Fragment based on the value of position
            switch (position) {
                case 0 : return new OnboardingFragment1();
                case 1 : return new OnboardingFragment2();
                case 2 : return new OnboardingFragment3();
                default: return null;
            }
        }

        @Override
        //returns the number of onboarding screens.
        public int getCount() {
            return 3;
        }
    };

    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }
}

