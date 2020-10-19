package vip.efactory.embp.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vip.efactory.common.base.entity.BaseSearchField;
import vip.efactory.common.base.enums.SearchTypeEnum;
import vip.efactory.common.base.page.EPage;
import vip.efactory.common.base.utils.CommUtil;
import vip.efactory.common.base.utils.R;
import vip.efactory.common.base.utils.ValidateModelUtil;
import vip.efactory.common.base.valid.Create;
import vip.efactory.common.base.valid.Update;
import vip.efactory.common.i18n.enums.CommAPIEnum;
import vip.efactory.common.i18n.enums.CommDBEnum;
import vip.efactory.common.i18n.service.ILocaleMsgSourceService;
import vip.efactory.embp.base.entity.BaseEntity;
import vip.efactory.embp.base.service.IBaseService;

import javax.validation.groups.Default;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Description:这是一个基础的控制器基类，包含常见的基本的CRUD的请求的处理以及高级搜索的处理，其他特殊的方法通过子类继承实现。
 * T1,是操作的实体类，T2是对应的service接口类继承IBaseService,
 *
 * @author dbdu
 */
@Slf4j
@SuppressWarnings("all")
public class BaseController<T1 extends BaseEntity<T1>, T2 extends IBaseService<T1>> {

    /**
     * 处理国际化资源的组件
     */
    @Autowired
    public ILocaleMsgSourceService msgSourceService;

    @Autowired
    protected T2 entityService;

    /**
     * Description:获取T的Class对象是关键，看构造方法
     */
    private Class<T1> clazz = null;

    /**
     * Description:无参构造函数，获得T1的clazz对象
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

    public R getByPage(Page page) {
        IPage<T1> entities = entityService.page(page);
        EPage ePage = new EPage(entities);
        return R.ok().setData(ePage);
    }

    /**
     * Description:所有实体的分页列表,
     * 如果有高级查询条件,优先使用高级查询
     *
     * @param page   分页对象
     * @param entity 含有高级搜索条件的实体
     * @return R
     */
    public R advancedQueryByPage(Page page, T1 entity) {
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
            return R.ok(new EPage(entityService.page(page, wrapper)));
        } else {
            // 高级搜索查询,支持分页
            return R.ok(new EPage(entityService.advancedQuery(entity, page)));
        }
    }


    /**
     * Description:高级搜索查询,返回最多300记录的列表,不分页
     *
     * @param entity 含有高级搜索条件的实体
     * @return R
     */
    public R advancedQuery(T1 entity) {
        // 高级搜索查询,返回最多300记录的列表
        return R.ok(entityService.advancedQuery(entity));
    }

    /**
     * Description:同一个值,在多个字段中模糊查询,不分页
     *
     * @param q      模糊查询的值
     * @param fields 例如:"name,address,desc",对这三个字段进行模糊匹配
     * @return R
     */
    public R queryMultiField(String q, String fields) {
        if (StringUtils.isEmpty(fields)){
            return R.error(CommDBEnum.SELECT_PROPERTY_NAME_NOT_EMPTY);
        }
        // 构造高级查询条件
        T1 be = buildQueryConditions(q, fields);
        List<T1> entities = entityService.advancedQuery(be);
        return R.ok().setData(entities);
    }

    /**
     * Description:同一个值,在多个字段中模糊查询,分页
     *
     * @param q      模糊查询的值
     * @param fields 例如:"name,address,desc",对这三个字段进行模糊匹配
     * @param page   分页参数对象
     * @return R
     */
    public R queryMultiField(String q, String fields, Page page) {
        if (StringUtils.isEmpty(fields)){
            return R.error(CommDBEnum.SELECT_PROPERTY_NAME_NOT_EMPTY);
        }
        // 构造高级查询条件
        T1 be = buildQueryConditions(q, fields);
        IPage<T1> entities = entityService.advancedQuery(be, page);
        EPage ePage = new EPage(entities);
        return R.ok().setData(ePage);
    }

    /**
     * Description:依据实体的id主键，来查询单个实体的信息
     *
     * @param entityId 实体id,主键
     * @return R
     */
    public R getById(Serializable entityId) {
        if (null == entityId) {
            return R.error(CommDBEnum.KEY_NOT_NULL);
        }
        T1 entity = entityService.getById(entityId);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(CommDBEnum.SELECT_NON_EXISTENT);
        }
    }

    /**
     * Description:保存单个实体的信息
     *
     * @param entity 要保存的对象
     * @return R
     */
    public R save(T1 entity) {
        // 实体校验支持传递组规则，不传递则为Default组！
        Map<String, String> errors = ValidateModelUtil.validateModel(entity, Default.class, Create.class);
        if (!errors.isEmpty()) {
            return R.error(CommAPIEnum.PROPERTY_CHECK_FAILED).setData(errors);
        }
        // 擦除基础字段的信息，基础字段只能由后端自己维护
        entity.setCreateTime(null);
        entity.setUpdateTime(null);
        entity.setCreatorNum(null);
        entity.setUpdaterNum(null);
        boolean isOK = entityService.save(entity);
        return isOK ? R.ok() : R.error(CommDBEnum.UNKNOWN);
    }

    /**
     * Description:更新实体的信息
     *
     * @param entity 要更新的对象
     * @return R
     */
    public R updateById(T1 entity) {
        // 检查实体的属性是否符合校验规则，使用Update组来校验实体，
        Map<String, String> errors = ValidateModelUtil.validateModel(entity, Default.class, Update.class); // 可以传递多个校验组！

        if (!errors.isEmpty()) {
            return R.error(CommAPIEnum.PROPERTY_CHECK_FAILED).setData(errors);
        }

        // 检查数据记录是否已经被删除了，被删除了，则不允许更新
        T1 entityDb = entityService.getById(entity.getPk());
        if (entityDb == null) {
            return R.error(CommDBEnum.UPDATE_NON_EXISTENT);
        } else {
            // 检查更新时间戳，避免用旧的数据更新数据库里的新数据
            LocalDateTime updateTime = entity.getUpdateTime();
            LocalDateTime dbUpdateTime = entityDb.getUpdateTime();
            if (updateTime != null && updateTime.compareTo(dbUpdateTime) != 0) {
                return R.error(CommDBEnum.UPDATE_NEW_BY_OLD_NOT_ALLOWED);
            }
        }
        //检查业务key的存在性，不应该存在重复的业务key,此处不知道业务key是什么属性，可以在在service层实现，重写方法即可！
        // 擦除基础字段的信息，基础字段只能由后端自己维护
        entity.setCreateTime(null);
        entity.setUpdateTime(null);
        entity.setCreatorNum(null);
        entity.setUpdaterNum(null);
        boolean isOK = entityService.updateById(entity);
        return isOK ? R.ok() : R.error(CommDBEnum.UNKNOWN);
    }

    /**
     * Description:使用entityId删除单个实体,
     *
     * @param entityId 使用实体的id来删除对象
     * @return R
     */
    public R deleteById(Serializable entityId) {

        if (null == entityId) {
            return R.error(CommDBEnum.KEY_NOT_NULL);
        }

        //进行关联性检查,调用对应的方法
        // 在删除前用id到数据库查询一次,不执行空删除，不检查就可能会在数据库层面报错，尽量不让用户见到看不懂的信息
        T1 entity = entityService.getById(entityId);
        if (entity == null) {
            return R.error(CommDBEnum.DELETE_NON_EXISTENT);
        }

        try {
            boolean isOK = entityService.removeById(entityId); // 关联关系可以在service层重写实现
            return isOK ? R.ok() : R.error(CommDBEnum.UNKNOWN);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * Description:使用id的Set集合来删除指定的实体，不使用数组防止存在重复的数据
     *
     * @param entityIds 使用主键Set集合
     * @return R
     */
    public R deleteByIds(Set<Serializable> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return R.ok();
        }

        try {
            boolean isOK = entityService.removeByIds(entityIds); // 关联关系可以在service层重写实现
            return isOK ? R.ok() : R.error(CommDBEnum.UNKNOWN);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * Description:检查操作的idd对应实体是否存在，因为多人操作，可能被其他人删除了！
     *
     * @param entityId 实体主键id
     * @return Boolean
     */
    public Boolean chkEntityIdExist(Serializable entityId) {
        return null != entityId && entityService.existsById(entityId);
    }

    /**
     * Description:根据查询值及多字段,来构建高级查询条件
     *
     * @param q      查询额值
     * @param fields 需要模糊匹配的字段，支持的分隔符：中英文的逗号分号，和中文的顿号！
     * @return T1 当前的泛型实体, 包含高级查询参数
     */
    @SneakyThrows
    private T1 buildQueryConditions(String q, String fields) {
        // 如果q不为空,则构造高级查询条件
        T1 entity = clazz.newInstance();
        if (!CommUtil.isMutiHasNull(q, fields)) {
            Set<BaseSearchField> conditions = new HashSet<>();
            // 判断filds是一个字段还是多个字段,若是多个字段则进行切分
            // 切分属性值为集合，支持的分隔符：中英文的逗号分号，和中文的顿号！
            String[] rawFields = fields.split(",|;|、|，|；");
            for (String c : rawFields) {
                // 构建默认OR的多字段模糊查询
                BaseSearchField condition = new BaseSearchField();
                condition.setName(c);
                condition.setSearchType(SearchTypeEnum.FUZZY.getValue());
                condition.setVal(q);
                conditions.add(condition);
            }
            entity.setConditions(conditions);
            return entity;
        }
        return null;
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
            return R.error(CommDBEnum.SELECT_PROPERTY_NAME_NOT_EMPTY);
        }

        return R.ok(entityService.advanceSearchProperty(property, value));
    }

}


