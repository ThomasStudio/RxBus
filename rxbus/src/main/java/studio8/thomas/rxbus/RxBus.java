package studio8.thomas.rxbus;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 */
public class RxBus {
    public static final String TAG = "RxBus";

    private static Map<String, PublishSubject<Object>> publishes = new ConcurrentHashMap();
    private static Map<Class<?>, PublishSubject> publishesT = new ConcurrentHashMap();
    private static Map<Consumer, Disposable> disposableMap = new ConcurrentHashMap<>();

    /**
     * get Publishes for event with tag
     *
     * @param tag
     * @return
     */
    private static PublishSubject<Object> getPublishes(String tag) {
        PublishSubject<Object> publish;

        if (!publishes.containsKey(tag)) {
            publish = PublishSubject.create();
            publishes.put(tag, publish);
        } else {
            publish = publishes.get(tag);
        }

        return publish;
    }

    /**
     * get Publishes without tag
     *
     * @param theClass
     * @return
     */
    private static PublishSubject getPublishes(Class<?> theClass) {
        PublishSubject publish;

        if (!publishesT.containsKey(theClass)) {
            publish = PublishSubject.create();
            publishesT.put(theClass, publish);
        } else {
            publish = publishesT.get(theClass);
        }

        return publish;
    }

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
        disposableMap.put(consumer, disposable);
    }

    /**
     * specialize the tag string
     *
     * @param tag
     * @param event
     */
    public static void send(String tag, Object event) {
        if (null == tag || tag.length() < 1) return;
        if (null == event) return;

        Log.d(TAG, "send: tag = " + tag + " event = " + event.toString());
        getPublishes(tag).onNext(event);
    }

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
        disposableMap.put(consumer, disposable);
    }

    /**
     * unsubscribe a consumer from PublishSubject
     *
     * @param consumer
     */
    public static void unSubscribe(Consumer consumer) {
        if (disposableMap.containsKey(consumer)) {
            disposableMap.get(consumer).dispose();
            disposableMap.remove(consumer);
        }
    }

    /**
     * send a event
     *
     * @param event
     */
    public static <T> void send(T event) {
        if (null == event) return;

        Class<?> c = event.getClass();
        Log.d(TAG, "send: class = " + c.getName() + " event = " + event.toString());

        getPublishes(c).onNext(event);
    }

}