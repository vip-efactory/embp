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

}
