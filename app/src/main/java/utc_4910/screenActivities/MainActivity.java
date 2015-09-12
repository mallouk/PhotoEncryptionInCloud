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

    //Define instance variables
    private Button loginButton = null;
    private Button createAccountButton = null;

    /** Method that defines initial properties of the screen.
     *
     * @param savedInstanceState                saved state of the instance
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        loginButton = (Button)findViewById(R.id.loginButton);
        createAccountButton = (Button)findViewById(R.id.createAccountButton);

        //Listen for events
        this.runButtonListeners();
    }

    /** Method that contains all of the cases to handle events.
     *
     */
    public void runButtonListeners(){

        /** Function call to occur when the Login button is tapped.
         *  We switch over to the LoginActivity screen.
         *
         */
        loginButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, LoginActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });

        /** Function call to occur when the CreateAccount button is tapped.
         *  We switch over to the CreateAccountActivity screen.
         *
         */
        createAccountButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, CreateAccountActivity.class);
                //Launch the next activity.
                startActivity(i);
            }
        });
    }
}
