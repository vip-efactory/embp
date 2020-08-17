package vip.efactory.embp.base.fill;

import java.util.Optional;

/**
 * 知道应用程序当前审核程序的组件的接口。这主要是某种用户。
 *
 * @param <T> 审核实例的类型
 * @author dbdu
 */
public interface AuditorAware<T> {

	/**
	 * 返回应用程序的当前审核员。即，当前的用户
	 *
	 * @return 当前用户
	 */
	Optional<T> getCurrentAuditor();
}
