package vip.efactory.embp.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * 所有实体的基类
 */
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)      //null值的属性JSON不输出
public abstract class BaseEntity<T extends BaseSearchEntity> extends BaseSearchEntity<T> {

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;

}
