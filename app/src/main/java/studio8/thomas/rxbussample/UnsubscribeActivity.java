package studio8.thomas.rxbussample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import studio8.thomas.rxbus.ConsumerT;
import studio8.thomas.rxbus.RxBus;

public class UnsubscribeActivity extends AppCompatActivity {

    private static final String TAG = "UnsubscribeActivity";

    ConsumerT consumerTAG = new ConsumerT<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            Log.d(TAG, "UnsubscribeActivity accept: " + o.toString() + " tag=" + TAG);
            Toast.makeText(UnsubscribeActivity.this, "UnsubscribeActivity receive message: " + o.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    ConsumerT consumerTString = new ConsumerT<String>() {
        @Override
        public void accept(String s) throws Exception {
            Log.d(TAG, "UnsubscribeActivity accept: " + s);
            Toast.makeText(UnsubscribeActivity.this, "UnsubscribeActivity receive message: " + s, Toast.LENGTH_SHORT).show();
        }
    };

    ConsumerT consumerTActivity = new ConsumerT<UnsubscribeActivity>() {
        @Override
        public void accept(UnsubscribeActivity UnsubscribeActivity) throws Exception {
            Log.d(TAG, "accept: " + UnsubscribeActivity.toString());
            Toast.makeText(UnsubscribeActivity, "receive message: " + UnsubscribeActivity.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextpage);
    }


    public void subscribeTag(View view) {
        Log.d(TAG, "subscribeTag: tag=" + TAG);

        RxBus.subscribe(TAG, consumerTAG);
    }

    public void SendStringTag(View view) {
        RxBus.send(TAG, "RxBus message");
    }

    public void sendStringIntTag(View view) {
        RxBus.send(TAG, "RxBus message");
        RxBus.send(TAG, 123456);
    }

    public void subscribeActivity(View view) {
        RxBus.subscribe(consumerTActivity);
    }

    public void subscribeString(View view) {
        RxBus.subscribe(consumerTString);
    }

    public void sendActivity(View view) {
        RxBus.send(this);
    }

    public void sendString(View view) {
        RxBus.send("RxBus message");
    }

    public void UnSubscribeAll(View view) {
        RxBus.unSubscribe(consumerTAG);
        RxBus.unSubscribe(consumerTString);
        RxBus.unSubscribe(consumerTActivity);
    }

    public void finishPage(View view) {
        this.finish();
    }
}
