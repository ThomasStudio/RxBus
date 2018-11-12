package studio8.thomas.rxbus;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * RxBus has two pair of subscribe/send methond
 * one. subscribe/send with tag
 * tow. subscribe/send with Class
 * <p>
 * pair one : it is flexible. Subscribe a consumer with a tag,
 * and you can send any data(subclass of Object) to this consumer with this tag.
 * so the consumer should check the data(with instanceof) in accept method
 * <p>
 * pair two : it is strict. Subscribe a consumer with Class type,
 * and you can only this kind of Class to the consumer, send subclass does not accept.
 * so the consumer doesnot need to check the data in accept method.
 * <p>
 * 2018.11.12
 * Use the WeakReference instead of normal reference, avoid the memory leak.
 */
public class RxBus {
    public static final String TAG = "RxBus";

    private static boolean sticky;

    private static Map<WeakReference<Consumer>, Disposable> disposableMap = new ConcurrentHashMap<>();

    private static Map<String, PublishSubject<Object>> publishes = new ConcurrentHashMap();
    private static Map<Class<?>, PublishSubject> publishesT = new ConcurrentHashMap();

    private static Map<String, List<Object>> stickyMessages = new ConcurrentHashMap<>();
    private static Map<Class<?>, List<Object>> stickyMessagesT = new ConcurrentHashMap<>();

//============================== get PublishSubject ==============================

    /**
     * get Publishes for message with tag
     *
     * @param tag
     * @return
     */
    private static PublishSubject<Object> getPublishes(String tag) {
        PublishSubject<Object> publish;

        synchronized (publishes) {
            if (!publishes.containsKey(tag)) {
                publish = PublishSubject.create();
                publishes.put(tag, publish);
            } else {
                publish = publishes.get(tag);
            }

            return publish;
        }
    }

    /**
     * get Publishes without tag
     *
     * @param theClass
     * @return
     */
    private static PublishSubject getPublishes(Class<?> theClass) {
        PublishSubject publish;

        synchronized (publishesT) {
            if (!publishesT.containsKey(theClass)) {
                publish = PublishSubject.create();
                publishesT.put(theClass, publish);
            } else {
                publish = publishesT.get(theClass);
            }

            return publish;
        }
    }

    //============================== get sticky message list ==============================

    public static List<Object> getStickyList(Class<?> theClass) {
        synchronized (stickyMessagesT) {
            if (stickyMessagesT.containsKey(theClass)) {
                return stickyMessagesT.get(theClass);
            } else {
                List<Object> list = new Vector<>();
                stickyMessagesT.put(theClass, list);
                return list;
            }
        }
    }

    public static List<Object> getStickyList(String tag) {
        synchronized (stickyMessages) {
            if (stickyMessages.containsKey(tag)) {
                return stickyMessages.get(tag);
            } else {
                List<Object> list = new Vector<>();
                stickyMessages.put(tag, list);
                return list;
            }
        }
    }

//============================== subscribe/send message with tag ==============================

    /**
     * specialize the tag string
     *
     * @param tag
     * @param consumer
     */
    public static void subscribe(String tag, Consumer<? super Object> consumer) {
        if (null == tag || tag.length() < 1) return;

        if (null == consumer) return;

        unSubscribe(consumer);

        Log.d(TAG, "subscribe: tag = " + tag);

        if (null == tag || tag.length() < 1) {
            throw new IllegalArgumentException("tag is null or empty");
        }

        PublishSubject<Object> publish = getPublishes(tag);
        Disposable disposable = publish.subscribe(consumer);
        synchronized (disposableMap) {
            disposableMap.put(new WeakReference<Consumer>(consumer), disposable);
        }
    }

    /**
     * specialize the tag string
     *
     * @param tag
     * @param message
     */
    public static void send(String tag, Object message) {
        if (null == tag || tag.length() < 1) return;
        if (null == message) return;

        Log.d(TAG, "send: tag = " + tag + " message = " + message.toString());
        getPublishes(tag).onNext(message);
    }

//============================== subscribe/send message ==============================

    /**
     * use Class as tag
     *
     * @param consumer
     * @param <T>
     */
    public static <T> void subscribe(ConsumerT<T> consumer) {
        if (null == consumer) return;
        Log.d(TAG, "subscribe: class = " + consumer.getT());

        unSubscribe(consumer);

        Disposable disposable = getPublishes(consumer.getT()).subscribe(consumer);
        synchronized (disposableMap) {
            disposableMap.put(new WeakReference<Consumer>(consumer), disposable);
        }
    }


    /**
     * send a message
     *
     * @param message
     */
    public static <T> void send(T message) {
        if (null == message) return;

        Class<?> c = message.getClass();
        Log.d(TAG, "send: class = " + c.getName() + " message = " + message.toString());

        getPublishes(c).onNext(message);
    }

//============================== unSubscribe message ==============================

    /**
     * unsubscribe a consumer from PublishSubject
     *
     * @param consumer
     */
    public static void unSubscribe(Consumer consumer) {
        Log.d(TAG, "unSubscribe: consumer=" + consumer.toString());
        if (null == consumer) return;

        synchronized (disposableMap) {
            for (WeakReference<Consumer> weak : disposableMap.keySet()) {
                if (weak.get() == consumer) {
                    disposableMap.get(weak).dispose();
                    disposableMap.remove(weak);
                }
            }
        }
    }

    //============================== sticky message without tag ==============================
    public static <T> void subscribeSticky(ConsumerT<T> consumer) {
        Log.d(TAG, "subscribeSticky: ");

        subscribe(consumer);

//        send sticky message to consumer
        List<Object> list = getStickyList(consumer.getT());
        synchronized (list) {
            for (Object obj : list) {
                try {
                    consumer.accept((T) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> void sendSticky(T message) {
        Log.d(TAG, "sendSticky: ");

        addSticky(message);
        send(message);
    }

    /**
     * Delete sticky list by Class
     *
     * @param theClass
     * @param <T>
     * @return
     */
    public static <T> boolean deleteStickyList(Class<T> theClass) {
        Log.d(TAG, "deleteStickyList: theClass = " + theClass.getName());

        synchronized (stickyMessagesT) {
            if (stickyMessagesT.containsKey(theClass)) {
                stickyMessagesT.remove(theClass);
                return true;
            }
            return false;
        }
    }

    /**
     * Delete one sticky by Class.
     *
     * @param message
     * @param <T>
     * @return if deleted, return true; if not, return false
     */
    public static <T> boolean deleteSticky(T message) {
        Log.d(TAG, "deleteStickyList: theClass = " + message.toString());

        synchronized (stickyMessagesT) {
            if (stickyMessagesT.containsKey(message.getClass())) {
                return getStickyList(message.getClass()).remove(message);
            }

            return false;
        }
    }

    private static void addSticky(Object message) {
        if (null == message) {
            Log.d(TAG, "addSticky: message is null");
            return;
        }

        Log.d(TAG, "addSticky: message = " + message.toString());

        synchronized (stickyMessagesT) {
            if (!getStickyList(message.getClass()).contains(message))
                getStickyList(message.getClass()).add(message);
        }
    }

    //============================== sticky message with tag ==============================
    public static void subscribeSticky(String tag, Consumer<Object> consumer) {
        Log.d(TAG, "subscribeSticky");

        subscribe(tag, consumer);

//        send sticky message to consumer
        List<Object> list = getStickyList(tag);
        synchronized (list) {
            for (Object obj : list) {
                try {
                    consumer.accept(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendSticky(String tag, Object message) {
        Log.d(TAG, "sendSticky: ");

        addSticky(tag, message);
        send(tag, message);
    }

    public static boolean deleteStickyList(String tag) {
        Log.d(TAG, "deleteStickyList: tag = " + tag);

        synchronized (stickyMessages) {
            if (stickyMessages.containsKey(tag)) {
                stickyMessages.remove(tag);
                return true;
            }
            return false;
        }
    }

    public static boolean deleteSticky(String tag, Object message) {
        if (null == tag || null == message || tag.length() < 1) return false;

        Log.d(TAG, "deleteSticky: tag=" + tag + ", message=" + message.toString());

        synchronized (stickyMessages) {
            if (stickyMessages.containsKey(tag)) {
                return getStickyList(tag).remove(message);
            }

            return false;
        }
    }

    private static void addSticky(String tag, Object message) {
        if (null == message || tag == null || tag.length() < 1) {
            Log.d(TAG, "addSticky: message is null or tag is null(empty)");
            return;
        }

        Log.d(TAG, "addSticky: message = " + message.toString() + ", tag = " + tag);

        synchronized (stickyMessages) {
            if (!getStickyList(tag).contains(message))
                getStickyList(tag).add(message);
        }
    }

}