package vip.efactory.embp.base.service.impl;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 本类中的泛型参数，其实没有使用到，只是为了子类继承传递的需要而已
 * @param <M> mapper
 * @param <T> entity
 */
public interface BaseObserver<M extends BaseMapper<T>, T> {
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the <code>notifyObservers</code>
     *                 method.
     */
    void update(BaseObservable<M, T> o, Object arg);
}
