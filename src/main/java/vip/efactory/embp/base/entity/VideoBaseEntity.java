package vip.efactory.embp.base.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:视频文件上传下载的基础类
 *
 * @author dbdu
 * @date 2020-11-29 上午8:32
 */
@Setter
@Getter
public class VideoBaseEntity<T extends OssBaseEntity<T>> extends OssBaseEntity<T> {
    private static final long serialVersionUID = 1L;

    /**
     * Description: 视频时长的时分秒信息
     */
    @ApiModelProperty(value = "时长,小时信息", name = "hours")
    private Integer hours;
    @ApiModelProperty(value = "时长,分钟信息", name = "minutes")
    private Integer minutes;
    @ApiModelProperty(value = "时长,秒信息", name = "seconds")
    private Float seconds;

    /**
     * Description:分辨率信息,宽与高
     */
    @ApiModelProperty(value = "视频分辨率,宽", name = "width")
    private Integer width;
    @ApiModelProperty(value = "视频分辨率,高", name = "height")
    private Integer height;

    /**
     * Description:后缀,视频格式
     */
    @ApiModelProperty(value = "视频后缀格式", name = "suffix")
    private String suffix;

    /**
     * Description:封面图片URL
     */
    @ApiModelProperty(value = "视频封面图片url", name = "faceUrl")
    private String faceUrl;

    /**
     * Description:视频分类,例如:维修类,配置类,等
     */
    @ApiModelProperty(value = "视频分类,例如:维修类,配置类,等", name = "videoType", required = true)
    private String videoType;

    /**
     * Description:视频的标签信息,方便检索,多个标签用逗号分隔
     */
    @ApiModelProperty(value = "视频的标签信息,方便检索,多个标签用逗号分隔", name = "tags", required = true)
    private String tags;

    /**
     * Description:视频描述
     */
    @ApiModelProperty(value = "视频详细描述", name = "videoDesc", required = true)
    private String videoDesc;

    @Override
    public String toString() {
        return "VideoBaseEntity{" +
                "hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                ", width=" + width +
                ", height=" + height +
                ", suffix='" + suffix + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", videoType='" + videoType + '\'' +
                ", tags='" + tags + '\'' +
                ", videoDesc='" + videoDesc + '\'' +
                "} " + super.toString();
    }
}
