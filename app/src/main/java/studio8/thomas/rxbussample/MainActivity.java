package studio8.thomas.rxbussample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import studio8.thomas.rxbus.ConsumerT;
import studio8.thomas.rxbus.RxBus;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RxBus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void subscribeTag(View view) {
        Log.d(TAG, "subscribeTag: tag=" + TAG);

        RxBus.subscribe(TAG, new ConsumerT<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "accept: " + o.toString() + " tag=" + TAG);
                Toast.makeText(MainActivity.this, "receive message: " + o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SendStringTag(View view) {
        RxBus.send(TAG, "RxBus message");
    }

    public void sendStringIntTag(View view) {
        RxBus.send(TAG, "RxBus message");
        RxBus.send(TAG, 123456);
    }

    public void subscribeActivity(View view) {
        RxBus.subscribe(new ConsumerT<MainActivity>() {
            @Override
            public void accept(MainActivity mainActivity) throws Exception {
                Log.d(TAG, "accept: " + mainActivity.toString());
                Toast.makeText(mainActivity, "receive message: " + mainActivity.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void subscribeString(View view) {
        RxBus.subscribe(new ConsumerT<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, "accept: " + s);
                Toast.makeText(MainActivity.this, "receive message: " + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendActivity(View view) {
        RxBus.send(this);
    }

    public void sendString(View view) {
        RxBus.send("RxBus message");
    }
}
