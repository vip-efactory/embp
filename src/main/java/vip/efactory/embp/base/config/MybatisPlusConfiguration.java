package vip.efactory.embp.base.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import vip.efactory.embp.base.fill.CurrentUserIdAuditorAware;
import vip.efactory.embp.base.fill.MbpMetaObjectHandler;

/**
 * Mybatis-plus的一些全局配置
 *
 * @author dbdu
 */
public class MybatisPlusConfiguration {

    /**
     * 分页插件
     *
     * @return PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 识别系统中当前用户的bean
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserIdAuditorAware currentUserIdAuditorAware() {
        return new CurrentUserIdAuditorAware();
    }

    /**
     * 表公共字段填充处理
     * @param currentUserIdAuditorAware 当前用户的标识件，项目中需要自己实现
     */
    @Bean
    public MbpMetaObjectHandler mbpMetaObjectHandler(CurrentUserIdAuditorAware currentUserIdAuditorAware) {
        return new MbpMetaObjectHandler(currentUserIdAuditorAware);
    }

}
