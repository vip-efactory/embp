package vip.efactory.embp.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:图像文件上传下载的基础类
 *
 * @author dbdu
 * @date 2020-11-29 上午8:32
 */
@Getter
@Setter
public abstract class ImageBaseEntity<T extends OssBaseEntity<T>> extends OssBaseEntity<T> {
    private static final long serialVersionUID = 1L;

    /**
     * 分辨率信息
     */
    @ApiModelProperty(value = "分辨率信息", name = "resolution")
    private String resolution;

    /**
     * Description:后缀,图片格式
     */
    @ApiModelProperty(value = "后缀,图片格式", name = "suffix")
    private String suffix;

}
