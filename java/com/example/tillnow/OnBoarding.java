package com.example.tillnow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoarding extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    private Button letsGetStarted;
    private Animation animation;
    private int currentPos;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }
    public void skip(View view){
        startActivity(new Intent(getApplicationContext(),Welcome.class));
        sharedPreferences = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("new",false);
        editor.commit();
        finish();
    }

    public void next(View view){
        currentPos++;
        if(currentPos>=sliderAdapter.getCount()){
            currentPos=sliderAdapter.getCount()-1;
        }
        viewPager.setCurrentItem(currentPos);
    }

    public void prev(View view){
        currentPos--;
        if(currentPos<0){
            currentPos=0;
        }
        viewPager.setCurrentItem(currentPos);
    }

    private void addDots(int position){
        dots = new TextView[sliderAdapter.getCount()];
        dotsLayout.removeAllViews();

        for(int i=0;i<dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
    }
    ViewPager.OnPageChangeListener changeListener= new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos=position;
            if(position == sliderAdapter.getCount()-1){
                animation = AnimationUtils.loadAnimation(OnBoarding.this,R.anim.button_animation);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);

            }else{
                letsGetStarted.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
