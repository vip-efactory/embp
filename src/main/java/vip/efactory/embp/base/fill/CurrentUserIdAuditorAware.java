package vip.efactory.embp.base.fill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Description: 这是当前用户的监听器,用来更新用户操作时,记录到创建人或者更新人的字段
 * String 存储用户名或者工号等,不建议存储用户id,一旦这个用户被删,将无从查起!
 * 此处只是一个范例，项目中要重写此方法以便返回真实的当前用户。
 * @author dbdu
 */
@Slf4j
public class CurrentUserIdAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 此处是可以用户名或者工号,默认值为System
        String username = "System";
//        try {
//            username = SecurityUtils.getUsername();
//        } catch (Exception e) {
//            log.info(e.getMessage());
//        }
        return Optional.of(username);

    }
}
