package vip.efactory.embp.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import vip.efactory.common.base.entity.BaseSearchField;
import vip.efactory.common.base.enums.ConditionRelationEnum;
import vip.efactory.common.base.enums.SearchTypeEnum;
import vip.efactory.common.base.utils.CommUtil;
import vip.efactory.common.base.utils.MapUtil;
import vip.efactory.common.base.utils.SQLFilter;
import vip.efactory.embp.base.entity.BaseEntity;
import vip.efactory.embp.base.service.IBaseService;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

/**
 * Description:这个类是BaseServcie的实现类，组件的实现类可以继承这个类来利用可以用的方法
 * 当然，也可以在这里添加调用前的检查逻辑
 * by dbdu
 */
@SuppressWarnings("ALL")
@Slf4j
public class BaseServiceImpl<T extends BaseEntity<T>, M extends BaseMapper<T>>
        extends BaseObservable<M, T>
        implements IBaseService<T>, BaseObserver<M, T> {

    // 默认组的名称
    private final String DEFAULT_GROUP_NAME = "DEFAULT_NO_GROUP";

    /**
     * Description:获取T的Class对象是关键，看构造方法
     */
    private Class<T> clazz = null;

    /**
     * Description:无参构造函数，获得T的clazz对象
     */
    public BaseServiceImpl() {
        //为了得到T1的Class，采用如下方法
        //1得到该泛型类的子类对象的Class对象
        Class clz = this.getClass();
        //2得到子类对象的泛型父类类型（也就是BaseDaoImpl<T>）
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        //
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T>) types[1];
    }

    @Override
    public IPage<T> advancedQuery(T entity, IPage<T> page) {
        // 构建查询条件
        QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
        // 执行查询
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<T> advancedQuery(T entity) {
        // 构建查询条件
        QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
        // 执行查询，最多返回300条记录
        queryWrapper.last("limit 300");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Set advanceSearchProperty(String property, String value) {
        // 检查属性名是否合法
        if (isPropertyIllegal(property)) {
            return new HashSet();
        }

        // 属性名==>字段名,驼峰转下划线
        property = CommUtil.camelCase2Underscore(property);
        // 取第一个条件,来构建查询条件

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        // 最多返回300条记录
        queryWrapper.select(property)
                .like(property, value)
                .last("limit 300");
        List<Object> result = baseMapper.selectObjs(queryWrapper);
        if (result != null && result.size() > 0) {
            // 去除重复数据
            return new TreeSet<Object>(result);
        }

        return new TreeSet();
    }

    /**
     * 根据主键判断实体是否存在
     *
     * @param id 主键
     * @return boolean true存在，false 不存在
     */
    @Override
    public boolean existsById(Serializable id) {
        return null != getById(id);
    }

    /**
     * Description: 利用实体来构建查询条件
     *
     * @param entity 实体
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     */
    private QueryWrapper<T> getQueryWrapper(T entity) {
        // 查询条件集合
        Set<BaseSearchField> conditions = entity.getConditions();
        // 与或非,默认0或的关系
        int searchRelation = 0;
        // 检查属性名和属性值的合法性
        checkPropertyAndValueValidity(entity);

        // 属性名==>字段名,驼峰转下划线
        property2Column(conditions);

        // 将条件按照各自所在的组进行分组
        Map<String, List<BaseSearchField>> groups = checkHasGroup(conditions);

        // 判断条件是否只有一个默认组，若是一个组，则说明没有组
        if (groups.size() == 1) {
            return handleSingleGroupCondition(groups.get(DEFAULT_GROUP_NAME), entity);
        } else {
            // 有多个组
            return handleGroupsCondition(groups, entity);
        }
    }

    /**
     * Description:检查属性名和属性值的合法性,不合法的属性和值都会被移除
     *
     * @param entity 检查高级搜索条件是否合法的实体
     * @return void
     */
    private void checkPropertyAndValueValidity(T entity) {
        Set<BaseSearchField> conditions = entity.getConditions();
        if (conditions == null || conditions.size() == 0) {
            return;
        }

        // 检查属性名是否合法 非法，存放非法的查询条件
        Set<BaseSearchField> illegalConditions = new HashSet<>();
        Map<String, String> properties = (Map<String, String>) MapUtil.objectToMap1(entity);
        Set<String> keys = properties.keySet();
        // 如果条件的字段名称与属性名不符，则移除，不作为选择条件；
        conditions.forEach(condition -> {
            if (!keys.contains(condition.getName())) {
                illegalConditions.add(condition);
            }
        });
        // 移除非法的条件
        conditions.removeAll(illegalConditions);

        // 继续检查条件的值是否有非法敏感的关键字
        conditions.forEach(condition -> {
            String value1 = condition.getVal();
            if (SQLFilter.sqlInject(value1)) {
                illegalConditions.add(condition);
            }

            // 如果是范围需要检查两个值是否合法
            if (condition.getSearchType() == SearchTypeEnum.RANGE.getValue()) {
                String value2 = condition.getVal2();
                if (SQLFilter.sqlInject(value2)) {
                    illegalConditions.add(condition);
                }
            }
        });

        // 移除非法条件
        conditions.removeAll(illegalConditions);
    }

    /**
     * 检查属性名是否非法
     *
     * @param property 属性名
     * @return true--非法;false--合法
     */
    @SneakyThrows
    private boolean isPropertyIllegal(String property) {
        // 检查属性名是否合法 非法
        // k为属性名,v为属性值
        Map<String, String> properties = (Map<String, String>) MapUtil.objectToMap1(clazz.newInstance());
        return properties.keySet().contains(property) ? false : true;
    }


    /**
     * Description: 对象的属性名转换为数据表的字段名
     *
     * @param conditions 条件集合
     * @return void
     */
    private void property2Column(Set<BaseSearchField> conditions) {
        if (conditions != null && conditions.size() > 0) {
            conditions.forEach(condition -> {
                condition.setName(CommUtil.camelCase2Underscore(condition.getName()));
            });
        }

    }

    /**
     * Description: 每个条件构造SQL语句
     *
     * @param [wrapper, condition]
     * @return void
     */
    private void createFieldCondition(QueryWrapper<T> wrapper, BaseSearchField condition) {
        switch (SearchTypeEnum.getByValue(condition.getSearchType())) {
            case EQ:
                // 等于条件
                wrapper.eq(condition.getName(), condition.getVal());
                break;
            case RANGE:
                //  范围条件
                wrapper.between(condition.getName(), condition.getVal(), condition.getVal2());
                break;
            case NE:
                //  不等于条件
                wrapper.ne(condition.getName(), condition.getVal());
                break;
            case LT:
                //  小于
                wrapper.lt(condition.getName(), condition.getVal());
                break;
            case LE:
                //  小于等于
                wrapper.le(condition.getName(), condition.getVal());
                break;
            case GT:
                //  大于条件
                wrapper.gt(condition.getName(), condition.getVal());
                break;
            case GE:
                //  大于等于
                wrapper.ge(condition.getName(), condition.getVal());
                break;
            case IS_NULL:
                // IS_NULL(8, "Null值查询"),
                wrapper.isNull(condition.getName());
                break;
            case NOT_NULL:
                // NOT_NULL(9, "非Null值查询")
                wrapper.isNotNull(condition.getName());
                break;
            case LEFT_LIKE:
                // LEFT_LIKE(10, "左模糊查询")
                wrapper.likeLeft(condition.getName(), condition.getVal());
                break;
            case RIGHT_LIKE:
                // RIGHT_LIKE(11, "右模糊查询")
                wrapper.likeRight(condition.getName(), condition.getVal());
                break;
            case IN:
                // IN(12, "包含查询")
                wrapper.in(condition.getName(), getListFromStringValue(condition));
                break;
            case NOT_IN:
                // IN(13, "不包含查询") ejpa内置不支持
                // 切分属性值为集合
                wrapper.notIn(condition.getName(), getListFromStringValue(condition));
                break;
            case FUZZY:
                //  模糊条件
                wrapper.like(condition.getName(), condition.getVal());
                break;
            default:
                log.warn("未知的搜索条件！");
                break;
        }
    }

    private List<String> getListFromStringValue(BaseSearchField condition) {
        // 切分属性值为集合，支持的分隔符：中英文的逗号分号，和中文的顿号！
        String[] values = condition.getVal().split(",|;|、|，|；");
        return Arrays.asList(values);
    }

    /**
     * 检测条件中是否含有分组信息，例如：类似这样的条件：（A=3 || B=4） && （ C= 5 || D=6）
     */
    private Map<String, List<BaseSearchField>> checkHasGroup(Set<BaseSearchField> conditions) {
        Map<String, List<BaseSearchField>> groups = new HashMap<>();
        // 存放没有明确分组的条件
        groups.put(DEFAULT_GROUP_NAME, new ArrayList<BaseSearchField>());

        // 遍历所有的条件进行分组
        for (BaseSearchField searchField : conditions) {
            String groupName = searchField.getBracketsGroup();

            if (StringUtils.isEmpty(groupName)) {
                // 条件没有分组信息
                groups.get(DEFAULT_GROUP_NAME).add(searchField);
            } else {
                // 条件有分组信息
                // 检查groups是否有此分组，有则用，没有则创建
                if (groups.get(groupName) == null) {
                    // 创建新的分组
                    groups.put(groupName, new ArrayList<BaseSearchField>());
                }
                // 再将条件放进去
                groups.get(groupName).add(searchField);
            }
        }

        // 对所有的分组按照 order排序
        for (Map.Entry<String, List<BaseSearchField>> entry : groups.entrySet()) {
            // 条件排序,排序后默认是升序
            entry.getValue().sort(Comparator.comparingInt(BaseSearchField::getOrder));
        }

        return groups;
    }

    /**
     * 处理多个分组条件的，条件查询构造
     */
    private QueryWrapper<T> handleGroupsCondition(Map<String, List<BaseSearchField>> groups, T entity) {
        // 先处理默认组的queryWrapper
        QueryWrapper<T> defaultGroupP = handleSingleGroupCondition(groups.get(DEFAULT_GROUP_NAME), entity);
        // 处理其他组
        for (Map.Entry<String, List<BaseSearchField>> entry : groups.entrySet()) {
            if (DEFAULT_GROUP_NAME.equalsIgnoreCase(entry.getKey())) {
                continue;
            }

            QueryWrapper<T> tmpGroupP = handleSingleGroupCondition(entry.getValue(), entity);
            if (tmpGroupP == null) {
                // 若也为空则没有必要继续进行了！
                continue;
            }

            // 从组内的一个条件里找到组的逻辑关系
            if (defaultGroupP == null) {
                // 当默认组条件为空时，defaultGroupP为null，不处理会导致空指针异常！
                defaultGroupP = tmpGroupP;
            } else {
                Integer logicalTypeGroup = entry.getValue().get(0).getLogicalTypeGroup();
                if (logicalTypeGroup == ConditionRelationEnum.AND.getValue()) {
                    defaultGroupP.and((Consumer<QueryWrapper<T>>) tmpGroupP);
                } else {
                    defaultGroupP.or((Consumer<QueryWrapper<T>>) tmpGroupP);
                }
            }
        }

        return defaultGroupP;
    }

    /**
     * 处理同一个组内查询条件的查询条件转换
     */
    private QueryWrapper<T> handleSingleGroupCondition(List<BaseSearchField> conditions, T entity) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (conditions != null && conditions.size() > 0) {
            long size = conditions.size();
            for (int i = 0; i < size; i++) {
                BaseSearchField condition = conditions.get(i);
                // 根据condition的关联条件进行与其他条件的关联
                if (condition.getLogicalType() == 0) {
                    queryWrapper.or();
                }
                // 根据搜索条件不同构造不同的查询语句
                createFieldCondition(queryWrapper, condition);
            }
        }
        return queryWrapper;
    }


    // ######################################################################################
    // 注意下面的三个方法是是维护多表关联查询结果缓存的一致性的，除非你知道在做什么，否则不要去修改!         #
    // 三个方法是：registObservers,notifyOthers,update                                        #
    // 此处使用了jdk自带的观察者的设计模式。  当前对象既是被观察者，也是观察者!                          #
    // ######################################################################################

    /**
     * 注册观察者,即哪些组件观察自己，让子类调用此方法实现观察者注册
     */
    @Override
    public void registObservers(BaseObserver... baseObservers) {
        for (BaseObserver baseObserver : baseObservers) {
            this.addBaseObserver(baseObserver);
        }
    }

    /**
     * 自己的状态改变了，通知所有依赖自己的组件进行缓存清除，
     * 通常的增删改的方法都需要调用这个方法，来维持 cache right!
     */
    @Override
    public void notifyOthers() {
        //注意在用Java中的Observer模式的时候i下面这句话不可少
        this.setChanged();
        // 然后主动通知， 这里用的是推的方式
        // this.notifyObservers(this.content);
        // 如果用拉的方式，这么调用
        this.notifyBaseObservers();
    }

    /**
     * 这是观察别人，别人更新了之后来更新自己的
     * 其实此处不需要被观察者的任何数据，只是为了知道被观察者状态变了，自己的相关缓存也就需要清除了，否则不一致
     * 例如：观察Ａ对象，但是Ａ对象被删除了，那个自己这边关联查询与Ａ有关的缓存都应该清除
     * 子类重写此方法在方法前面加上清除缓存的注解，或者在方法体内具体执行一些清除缓存的代码。
     *
     * @param o   被观察的对象
     * @param arg 传递的数据
     */
    @Override
    public void update(BaseObservable o, Object arg) {

    }

}

