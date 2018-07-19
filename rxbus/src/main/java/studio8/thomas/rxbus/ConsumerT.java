package studio8.thomas.rxbus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.functions.Consumer;


public abstract class ConsumerT<T> implements Consumer<T> {
    private Class<T> theClass;

    public ConsumerT() {
        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            theClass = (Class) params[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<T> getT() {
        return theClass;
    }
}