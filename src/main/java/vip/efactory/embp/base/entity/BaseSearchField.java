package vip.efactory.embp.base.entity;

import lombok.Data;

/**
 * Description: 需要搜索的字段定义,这里的字段是指实体的属性,
 * 例1:实体属性名称为zhangsan的精确条件,则name值为name,searchType值为1,val值为zhangsan
 * 例2:实体属性年龄在25-35之间的范围条件,则name值为age,searchType值为2,val值为25,val2值为35
 *
 * @author dbdu
 */
@Data
public class BaseSearchField {

    /**
     * 字段名，例如，name,password等
     */
    private String name;

    /**
     * 搜索类型：
     * ０－－模糊查询；
     * １－－精准查询，严格一样
     * ２－－范围查询
     * 更多类型,参见SearchTypeEnum
     */
    private Integer searchType;

    /**
     * 搜索字段值,暂时用String来接收所有类型的值！
     * 搜索类型为０或１默认取此值，为２范围查询时，此值是开始值
     */
    private String val;

    /**
     * 搜索类型为０或１时此值不用，为２范围查询时，此值是结束值
     */
    private String val2;

    /**
     * name名称对应的字段的数据类型，例如：String,Integer,Date,Number等
     */
//    private String type;

}
