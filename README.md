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

    public void subscribeTag(View view) {
        RxBus.subscribe(TAG, new ConsumerT<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Toast.makeText(MainActivity.this, "receive message: " + o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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

    public void subscribeActivity(View view) {
        RxBus.subscribe(new ConsumerT<MainActivity>() {
            @Override
            public void accept(MainActivity mainActivity) throws Exception {
                Toast.makeText(mainActivity, "receive message: " + mainActivity.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

====== send message without tag ======

    public void sendActivity(View view) {
        RxBus.send(MainActivity.this);
    }
