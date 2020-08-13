package vip.efactory.embp.base.service.impl;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;

/**
 * Description: 尝试基于观察者模式的缓存一致性基类service
 * 注意，此处就是jdk的java.util.Observable的实现，之所以存在是为了解开java中类不能多继承的死结
 * 本类中的泛型参数，其实没有使用到，只是为了子类继承传递的需要而已
 * @author dbdu
 */
@Slf4j
public abstract class BaseObservable<M extends BaseMapper<T>, T> extends ServiceImpl<M,T> {

    private boolean changed = false;
    private Vector<BaseObserver<M, T>> obs;

    /**
     * Construct an Observable with zero BaseObservers.
     */

    public BaseObservable() {
        obs = new Vector<>();
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    public synchronized void addBaseObserver(BaseObserver<M, T> o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param o the observer to be deleted.
     */
    public synchronized void deleteBaseObserver(BaseObserver<M, T> o) {
        obs.removeElement(o);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyBaseObservers(null)</tt></blockquote>
     */
    public void notifyBaseObservers() {
        notifyBaseObservers(null);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param arg any object.
     */
    public void notifyBaseObservers(Object arg) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current BaseObservers.
         */
        Object[] arrLocal;

        synchronized (this) {
            /* We don't want the BaseBaseObserver doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each Observable from
             * the Vector and store the state of the BaseBaseObserver
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added BaseBaseObserver will miss a
             *   notification in progress
             * 2) a recently unregistered BaseBaseObserver will be
             *   wrongly notified when it doesn't care
             */
            if (!changed) {
                return;
            }
            arrLocal = obs.toArray();
            clearChanged();
        }

        for (int i = arrLocal.length - 1; i >= 0; i--) {
            ((BaseObserver<M, T>) arrLocal[i]).update(this, arg);
        }
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteBaseObservers() {
        obs.removeAllElements();
    }

    /**
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change,
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
     * This method is called automatically by the
     * <code>notifyBaseObservers</code> methods.
     *
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * Tests if this object has changed.
     *
     * @return <code>true</code> if and only if the <code>setChanged</code>
     * method has been called more recently than the
     * <code>clearChanged</code> method on this object;
     * <code>false</code> otherwise.
     */
    public synchronized boolean hasChanged() {
        return changed;
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return the number of observers of this object.
     */
    public synchronized int countBaseObservers() {
        return obs.size();
    }
}
