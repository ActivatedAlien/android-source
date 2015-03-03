package io.bloc.android.blocly.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class BloclyActivity extends Activity {
    private TextView mWelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        String fakeFeedName = BloclyApplication.getSharedDataSource().getFeeds().get(0).getTitle();

        mWelcomeText = (TextView) findViewById(R.id.welcome_text);
        mWelcomeText.setText(fakeFeedName);
        Toast.makeText(this, fakeFeedName, Toast.LENGTH_LONG).show();
    }
}
