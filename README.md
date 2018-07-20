# RxBus
A better RxBus, convenient and powerful message router.


Usage:

====== Import lib ======

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
 Step 2. Add the dependency
 
	dependencies {
		implementation 'com.github.thomasstudio:RxBus:0.1'
	}


====== subscribe message with tag ======
    ConsumerT consumerTAG = new ConsumerT<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            Toast.makeText(NextpageActivity.this, "NextpageActivity receive message: " + o.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void subscribeTag(View view) {
        RxBus.subscribe(TAG, consumerTAG);
    }

====== send message with tag ======

    public void SendStringTag(View view) {
        RxBus.send(TAG, "RxBus message");
    }

    public void sendStringIntTag(View view) {
        RxBus.send(TAG, "RxBus message");
        RxBus.send(TAG, 123456);
    }


====== subscribe message without tag ======

    ConsumerT consumerTString = new ConsumerT<String>() {
        @Override
        public void accept(String s) throws Exception {
            Toast.makeText(NextpageActivity.this, "NextpageActivity receive message: " + s, Toast.LENGTH_SHORT).show();
        }
    };

    ConsumerT consumerTActivity = new ConsumerT<NextpageActivity>() {
        @Override
        public void accept(NextpageActivity nextpageActivity) throws Exception {
            Toast.makeText(nextpageActivity, "receive message: " + nextpageActivity.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void subscribeActivity(View view) {
        RxBus.subscribe(consumerTActivity);
    }

    public void subscribeString(View view) {
        RxBus.subscribe(consumerTString);
    }


====== send message without tag ======

    public void sendActivity(View view) {
        RxBus.send(MainActivity.this);
    }

    public void sendString(View view) {
        RxBus.send("RxBus message");
    }

====== unsubscribe message ======

    public void UnSubscribeAll(View view) {
        RxBus.unSubscribe(consumerTAG);
        RxBus.unSubscribe(consumerTString);
        RxBus.unSubscribe(consumerTActivity);
    }

