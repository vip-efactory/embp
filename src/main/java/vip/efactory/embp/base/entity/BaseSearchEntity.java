package vip.efactory.embp.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.efactory.common.base.entity.BaseSearchField;

import java.io.Serializable;
import java.util.Set;

/**
 * Description: 基础的高级搜索实体
 * Model 已经实现的序列化接口，此处不需要再实现了
 * @author dbdu
 */
@Setter
@Getter
public class BaseSearchEntity<T extends Model<T>> extends Model<T> {

    /**
     * 数据库不存这个字段
     * 所有的搜索条件字段,不允许重复
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<BaseSearchField> conditions;
}
