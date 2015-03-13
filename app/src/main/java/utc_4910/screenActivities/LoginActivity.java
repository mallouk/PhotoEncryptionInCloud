package utc_4910.screenActivities;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class LoginActivity extends ActionBarActivity {

    private Button loginButton = null;
    private Button createAccountButton = null;
    private Button changePassButton = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginButton = (Button)findViewById(R.id.loginButton);
        createAccountButton = (Button)findViewById(R.id.createAccountButton);
        changePassButton = (Button)findViewById(R.id.changePassButton);

        this.runButtonListeners();
    }

    public void runButtonListeners(){
        loginButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(LoginActivity.this, GestureActivity.class);
                //Launch the next activity.
                finish();
                startActivity(i);
            }
        });
    }

}
