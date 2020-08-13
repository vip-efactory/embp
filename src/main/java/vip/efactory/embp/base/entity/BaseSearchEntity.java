package vip.efactory.embp.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.efactory.common.base.entity.BaseSearchField;

import java.util.Set;

/**
 * Description: 基础的高级搜索实体
 *
 * @author dbdu
 */
@Setter
@Getter
public class BaseSearchEntity<T extends Model> extends Model {

    /**
     * 数据库不存这个字段
     * 所有的搜索条件字段,不允许重复
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<BaseSearchField> conditions;
}
