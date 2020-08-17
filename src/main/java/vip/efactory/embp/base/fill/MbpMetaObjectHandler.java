package vip.efactory.embp.base.fill;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 实体属性自动填充功能
 * 详情参阅：https://mybatis.plus/guide/auto-fill-metainfo.html
 * @author dbdu
 */
@Slf4j
@AllArgsConstructor
public class MbpMetaObjectHandler implements MetaObjectHandler {
    AuditorAware<String> auditorAware;

    /**
     * 实体属性自动填充功能，插入填充,自动填充创建时间及创建人
     * @param metaObject 元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "creatorNum", String.class, auditorAware.getCurrentAuditor().get());
    }

    /**
     * 实体属性自动填充功能，更新填充,自动填充更新时间及更新人
     * @param metaObject 元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updaterNum", String.class, auditorAware.getCurrentAuditor().get());

    }
}
