package vip.efactory.embp.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import vip.efactory.embp.base.entity.BaseEntity;
import vip.efactory.embp.base.entity.BaseSearchField;
import vip.efactory.embp.base.enums.SearchRelationEnum;
import vip.efactory.embp.base.enums.SearchTypeEnum;
import vip.efactory.embp.base.service.IBaseService;
import vip.efactory.embp.base.util.CommUtil;
import vip.efactory.embp.base.util.R;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;


/**
 * Description:这是一个基础的控制器基类，包含常见的基本的CRUD的请求的处理以及高级搜索的处理，其他特殊的方法通过子类继承实现。
 * T1,是操作的实体类，T2是对应的service接口类继承IBaseService,
 *
 * @author dbdu
 */
@Slf4j
@SuppressWarnings("unchecked")
public class BaseController<T1 extends BaseEntity, T2 extends IBaseService> {

    @Autowired
    protected T2 entityService;

    /**
     * Description:获取T的Class对象是关键，看构造方法
     *
     * @author dbdu
     */
    private Class<T1> clazz = null;

    /**
     * Description:无参构造函数，获得T1的clazz对象
     *
     * @author dbdu
     */
    public BaseController() {
        //为了得到T1的Class，采用如下方法
        //1得到该泛型类的子类对象的Class对象
        Class clz = this.getClass();
        //2得到子类对象的泛型父类类型（也就是BaseDaoImpl<T>）
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        //
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T1>) types[0];
    }


    /**
     * Description:高级搜索查询,返回最多300记录的列表,不分页
     *
     * @param entity 含有高级搜索条件的实体
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R getList(@RequestBody T1 entity) {
        // 高级搜索查询,返回最多300记录的列表
        return R.ok(entityService.advanceSearch(entity));
    }

    /**
     * 获取某个属性集合,去除重复,通常是前端选择需要,支持模糊匹配
     * 非法属性自动过滤掉
     *
     * @param property 驼峰式的属性
     * @param value    模糊查询的value值
     * @return R
     */
    public R getPropertySet(String property, String value) {
        // 属性名不允许为空
        if (StringUtils.isEmpty(property)) {
            return R.failed("查询的属性名不允许为空!");
        }

        return R.ok(entityService.advanceSearchProperty(property, value));
    }

    /**
     * Description:所有实体的分页列表,
     * 如果有高级查询条件,优先使用高级查询
     *
     * @param page   分页对象
     * @param entity 含有高级搜索条件的实体
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R getByPage(Page page, T1 entity) {
        // 过滤掉值为null或空串的无效高级搜索条件
        if (entity.getConditions() != null && entity.getConditions().size() > 0) {
            Set<Object> removeConditions = new HashSet<>();
            for (Object searchField : entity.getConditions()) {
                // 当搜索值为null或者空串时,这是一条无效的搜索条件,需要移除
                if (CommUtil.isEmptyString(((BaseSearchField) searchField).getVal())) {
                    removeConditions.add(searchField);
                }
            }
            entity.getConditions().removeAll(removeConditions);
        }

        if (entity.getConditions() == null || entity.getConditions().size() == 0) {
            // 默认的分页功能
            QueryWrapper<T1> wrapper = new QueryWrapper<>(entity);
            // 检查 是否有升序或者降序规则,没有则使用create_time降序
            if (page.ascs() == null && page.descs() == null) {
                page.setDesc("create_time");
            }

            return R.ok(entityService.page(page, wrapper));
        } else {
            // 高级搜索查询,支持分页
            return R.ok(entityService.advanceSearch(page, entity));
        }

    }

    /**
     * Description:依据实体的id主键，来查询单个实体的信息
     *
     * @param entityId 实体id,主键
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R getById(Long entityId) {
        return R.ok(entityService.getById(entityId));
    }

    /**
     * Description:保存单个实体的信息
     *
     * @param entity 要保存的对象
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R save(T1 entity) {
        entityService.save(entity);
        return R.ok(entity);
    }

    /**
     * Description:更新实体的信息
     *
     * @param entity 要更新的对象
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R updateById(T1 entity) {
        entity.setUpdateTime(null);    // 数据库自己更新此字段
        return R.ok(entityService.updateById(entity));
    }

    /**
     * Description:使用entityId删除单个实体,
     *
     * @param 使用实体的id来删除对象
     * @return vip.efactory.embp.base.util.R
     * @author dbdu
     */
    public R deleteById(Long entityId) {

        return R.ok(entityService.removeById(entityId));
    }

    /**
     * Description:根据查询值及多字段,来构建高级查询条件
     *
     * @param q      查询额值
     * @param fields 需要模糊匹配的字段
     * @return 当前的泛型实体, 包含高级查询参数
     * @author dbdu
     */
    @SneakyThrows
    private T1 buildQueryConditions(String q, String fields) {
        // 如果q不为空,则构造高级查询条件
        T1 entity = clazz.newInstance();
        if (!CommUtil.isMutiHasNull(q, fields)) {
            Set<BaseSearchField> conditions = new HashSet<>();
            // 判断filds是一个字段还是多个字段,若是多个字段则进行切分
            if (fields.contains(",")) {
                String[] rawFields = StringUtils.split(fields, ",");
                for (String c : rawFields) {
                    BaseSearchField condition = new BaseSearchField();
                    condition.setName(c);
                    condition.setSearchType(SearchTypeEnum.FUZZY.getValue());
                    condition.setVal(q);
                    conditions.add(condition);
                }
            } else {
                // 构建模糊查询的条件
                BaseSearchField condition = new BaseSearchField();
                condition.setName(fields);
                condition.setSearchType(SearchTypeEnum.FUZZY.getValue());
                condition.setVal(q);
                conditions.add(condition);
            }
            entity.setRelationType(SearchRelationEnum.OR.getValue());  // 所有条件或的关系
            entity.setConditions(conditions);
        }
        return entity;
    }


}


