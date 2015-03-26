package utc_4910.screenActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import utc_4910.photoencryptionincloud.R;

/**
 * Created by Matthew Jallouk on 3/1/2015.
 */
public class MainActivity extends ActionBarActivity {

    private Button loginButton = null;
    private Button createAccountButton = null;
    private Button changePassButton = null;
    private Button deleteAccountButton = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        loginButton = (Button)findViewById(R.id.loginButton);
        createAccountButton = (Button)findViewById(R.id.createAccountButton);
        changePassButton = (Button)findViewById(R.id.changePassButton);
        deleteAccountButton = (Button)findViewById(R.id.deleteAccountButton);

        this.runButtonListeners();
    }

    public void runButtonListeners(){
        loginButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, LoginActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });

        createAccountButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, CreateAccountActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });

        changePassButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, ChangePassActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });

        deleteAccountButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, ChangePassActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });
    }

}
