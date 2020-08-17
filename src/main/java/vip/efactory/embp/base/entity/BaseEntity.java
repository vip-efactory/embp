package vip.efactory.embp.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 所有实体的基类
 */
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)      //null值的属性JSON不输出
public abstract class BaseEntity<T extends BaseSearchEntity<T>> extends BaseSearchEntity<T> {

    public Serializable getPk() {
        return pkVal();
    }

    /**
     * Description:备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;

    /**
     * Description:创建人编号
     */

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private String creatorNum;

    /**
     * Description:更新人编号或者姓名,//不使用id，如果人员被删除，看到一个数字是无意义的。
     * 修改人
     */

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(hidden = true)
    private String updaterNum;
}
