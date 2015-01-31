package com.hehe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;


public class MainActivity extends ActionBarActivity {
	
	@InjectView(R.id.toolbar) Toolbar mToolbar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        
        setSupportActionBar(mToolbar);
        
        
    }

}
