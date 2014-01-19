package speak.me;

import com.example.speakme.R;

import android.app.Activity;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SpeakMeMainActivity extends Activity {
	protected static final int RESULT_SPEECH = 1;
	
	private boolean askedForVoice;
	 
    private ImageButton btnSpeak;
    private TextView txtText;
    
    private HashMap<Intent,String[]> commandMap;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //this.getIntent().getExtras().get(SpeakMe,);
        setContentView(R.layout.activity_main);
        
        commandMap = (HashMap<Intent, String[]>) this.getIntent().getExtras().getSerializable("CommandMap");
        
        askedForVoice = false;
        txtText = (TextView) findViewById(R.id.txtText);
        txtText.setText("Hello\ntesting\n");

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        
        btnSpeak.setOnClickListener(new View.OnClickListener() {
        	 
            @Override
            public void onClick(View v) {
 
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
 
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if(!askedForVoice) {
	    	Intent intent = new Intent(
	    	        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    	 
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
	 
	        try {
	                    startActivityForResult(intent, RESULT_SPEECH);
	                    txtText.setText("");
	        } catch (ActivityNotFoundException a) {
	            Toast t = Toast.makeText(getApplicationContext(),
	                    "Opps! Your device doesn't support Speech to Text",
	                    Toast.LENGTH_SHORT);
	            t.show();
	        }
	        
	        askedForVoice = true;
    	} else {
    		askedForVoice = false;
    	}
    }
    
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
 
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
         
//                //Testing
//                for (int i=0; i< text.size(); i++)
//                	{
//                		System.out.println(text.get(i));
//                	}
                		

                txtText.setText(text.get(0));

//                Set<Intent> intents = commandMap.keySet();
//                Intent i = (Intent) intents.toArray()[0];
                for (Intent i : commandMap.keySet()) {
                	for (String t : text) {
                		// count the number of matches of commandSet in t
                		int matches = 0;
                		for (String s : commandMap.get(i)) {
                			if (t.toLowerCase().contains(s.toLowerCase())){
                				matches++;
                			}
                		}
                		if (matches == commandMap.get(i).length) {
                            i.putExtra("text",t);
                			this.startService(i);
                			break;
                		}
                	}
                	
                }
            }
        }
 
        }
    }
}




