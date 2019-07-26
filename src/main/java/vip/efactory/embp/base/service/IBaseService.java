package vip.efactory.embp.base.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * Description:服务层接口的父接口，继承此接口默认下面的这些方法要实现的，采用泛型的写法
 * T表示泛型
 *
 * @author dbdu
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * Description: 高级搜索＋分页处理
     *
     * @param [page, entity]
     * @return com.baomidou.mybatisplus.core.metadata.IPage<T>
     * @author dbdu
     */
    IPage<T> advanceSearch(IPage<T> page, T entity);


    /**
     * Description: 高级搜索不分页，最多显示300条记录,防止内存占用过高！
     *
     * @param [entity]
     * @return java.util.List<T>
     * @author dbdu
     */
    List<T> advanceSearch(T entity);

    /**
     * 查询某个属性集合,不包含重复数据
     *
     * @param property 驼峰式的属性
     * @param value    模糊查询的value值
     * @return Set 集合
     */
    Set advanceSearchProperty(String property, String value);

}
