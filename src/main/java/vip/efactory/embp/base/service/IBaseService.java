package vip.efactory.embp.base.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;
import vip.efactory.embp.base.service.impl.BaseObservable;
import vip.efactory.embp.base.service.impl.BaseObserver;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Description:服务层接口的父接口，继承此接口默认下面的这些方法要实现的，采用泛型的写法
 * T表示泛型
 *
 * @author dbdu
 */
@SuppressWarnings("ALL")
public interface IBaseService<T> extends IService<T> {

    /**
     * Description: 高级搜索不分页，最多显示300条记录,防止内存占用过高！
     *
     * @param entity 条件实体
     * @return java.util.List<T>
     */
    List<T> advancedQuery(T entity);

    /**
     * Description: 高级模糊查询及分页
     *
     * @param entity   包含高级查询条件的实体
     * @param page 分页参数对象
     * @return IPage<T>
     */
    IPage<T> advancedQuery(T entity, IPage<T> page);

    /**
     * 查询某个属性集合,不包含重复数据
     *
     * @param property 驼峰式的属性
     * @param value    模糊查询的value值
     * @return Set 集合
     */
    Set advanceSearchProperty(String property, String value);

    /**
     * Description: 根据ID检查实体是否存在
     *
     * @param var1 id主键
     * @return boolean true存在，false 不存在
     */
    boolean existsById(Serializable var1);


    /**
     * 注册观察者,即哪些组件观察自己，让子类调用此方法实现观察者注册
     */
    @Async
    void registObservers(BaseObserver... baseObservers);

    /**
     * 自己的状态改变了，通知所有依赖自己的组件进行缓存清除，
     * 通常的增删改的方法都需要调用这个方法，来维持缓存一致性!
     * @param arg 通知观察者时可以传递礼物arg，即数据，如果不需要数据就传递null;
     */
    @Async
    void notifyOthers(Object arg);

    /**
     * 这是观察别人，别人更新了之后来更新自己的
     * 其实此处不需要被观察者的任何数据，只是为了知道被观察者状态变了，自己的相关缓存也就需要清除了，否则不一致
     * 例如：观察Ａ对象，但是Ａ对象被删除了，那个自己这边关联查询与Ａ有关的缓存都应该清除
     * 子类重写此方法在方法前面加上清除缓存的注解，或者在方法体内具体执行一些清除缓存的代码。
     *
     * @param o   被观察的对象
     * @param arg 传递的数据
     */
    @Async
    void update(BaseObservable o, Object arg);

}
