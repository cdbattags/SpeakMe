package speak.me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class LauncherActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState)
    {   
        super.onCreate(savedInstanceState);
        startService(new Intent(this, SpeakMe.class));
        Toast.makeText(this, "Launching service...", Toast.LENGTH_SHORT).show();
        this.finish();
    }
	
}
