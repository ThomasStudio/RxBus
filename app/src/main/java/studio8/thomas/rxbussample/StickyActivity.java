package studio8.thomas.rxbussample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import io.reactivex.functions.Consumer;
import studio8.thomas.rxbus.ConsumerT;
import studio8.thomas.rxbus.RxBus;

public class StickyActivity extends AppCompatActivity {
    private static final String TAG = "RxBus";

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky);
    }

    Consumer consumerTAG = new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            Log.d(TAG, "StickyActivity accept: " + o.toString() + " tag=" + TAG);
            Toast.makeText(StickyActivity.this, "StickyActivity receive message: " + o.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    ConsumerT consumerTString = new ConsumerT<String>() {
        @Override
        public void accept(String s) throws Exception {
            Log.d(TAG, "StickyActivity accept: " + s);
            Toast.makeText(StickyActivity.this, "StickyActivity receive message: " + s, Toast.LENGTH_SHORT).show();
        }
    };

    ConsumerT consumerTActivity = new ConsumerT<StickyActivity>() {
        @Override
        public void accept(StickyActivity StickyActivity) throws Exception {
            Log.d(TAG, "accept: " + StickyActivity.toString());
            Toast.makeText(StickyActivity, "receive message: " + StickyActivity.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void subscribeString(View view) {
        RxBus.subscribeSticky(consumerTString);
    }

    static int i = 0;

    public void sendString(View view) {
        RxBus.sendSticky(new String("RxBus message : " + i++));
    }

    public void sendManyString(View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendString(null);
                sendManyString(null);
            }
        }, 500);
    }

    public void DeleteString(View view) {
        RxBus.deleteStickyList(String.class);
    }


    public void UnSubscribeAll(View view) {
        RxBus.unSubscribe(consumerTAG);
        RxBus.unSubscribe(consumerTString);
        RxBus.unSubscribe(consumerTActivity);
    }

    public void finishPage(View view) {
        this.finish();
    }

    public void subscribeStringTag(View view) {
        RxBus.subscribeSticky(TAG, consumerTAG);
    }

    static int k = 0;

    public void sendStringTag(View view) {
        RxBus.sendSticky(TAG, "sticky with tag : " + k++);
    }

    public void sendManyStringTag(View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendStringTag(null);
                sendManyStringTag(null);
            }
        }, 500);

    }

    public void DeleteStringTag(View view) {
        RxBus.deleteStickyList(TAG);
    }
}