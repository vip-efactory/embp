package vip.efactory.embp.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:文件上传下载的基础类
 *
 * @author dbdu
 * @date 2020-11-29 上午8:32
 */
@Setter
@Getter
public abstract class OssBaseEntity<T extends BaseEntity<T>> extends BaseEntity<T> {
    private static final long serialVersionUID = 1L;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名", name = "name")
    private String name;

    /**
     * url
     */
    @ApiModelProperty(value = "文件url地址", name = "url")
    private String url;

    /**
     * 文件的大小(文本描述)，单位B,KB,MB，...
     */
    @ApiModelProperty(value = "文件的大小，单位B,KB,MB，...,是字符串类型", name = "fileSize")
    private String fileSize;

    /**
     * 文件的大小(字节的数字)，单位B,KB,MB，...
     */
    @ApiModelProperty(value = "文件的大小，单位B,KB,MB，...,是Long类型", name = "byteSize")
    private Long byteSize;

    /**
     * 文件MD5值
     */
    @ApiModelProperty(value = "文件MD5值", name = "md5")
    private String md5;

}
