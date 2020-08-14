package vip.efactory.embp.base.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Description:富文本实体基类
 *
 * @author dbdu
 */
@Setter
@Getter
public class BaseRichTextEntity<T extends BaseEntity<T>> extends BaseEntity<T> {

    /**
     * ID
     */
    @TableId
    private Long id;

    // 富文本标题,例如 SVF-65X型号的说明书
    private String title;

    // 富文本编辑器的内容
    private String content;

}
