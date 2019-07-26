package vip.efactory.embp.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Description: 基础的高级搜索实体
 *
 * @author dbdu
 */
@Setter
@Getter
public class BaseAdvanceSearchEntity<T extends Model> extends Model<T> {
    /**
     * Description: 关联类型：数据库不存这个字段
     * ０--或的关系，满足任意一个条件即可；
     * １--与的关系，满足所有条件；
     * －１--非的关系，条件取反；
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Integer relationType;

    /**
     * 数据库不存这个字段
     * 所有的搜索条件字段,不允许重复
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Set<BaseSearchField> conditions;


}
