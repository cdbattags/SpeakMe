package speak.me;

import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class SpeakMeReader extends Service {
	public SpeakMeReader m_service;
	private TextToSpeech tts;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		m_service = this;
		
		if(intent.hasExtra("text")) {
			final String textToSpeak = intent.getStringExtra("text");
			tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status == TextToSpeech.SUCCESS) {
			            int result = tts.setLanguage(Locale.US);
			 
			            if (result == TextToSpeech.LANG_MISSING_DATA
			                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			                Log.e("TTS", "This Language is not supported");
			            } else {
			                m_service.speakOut(textToSpeak);
			            }
			 
			        } else {
			            Log.e("TTS", "Initilization Failed!");
			        }
				}
			});
		}
		
		return START_STICKY;
	}
	
	public void speakOut(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
