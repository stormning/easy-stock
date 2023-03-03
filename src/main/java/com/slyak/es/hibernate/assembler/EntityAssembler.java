package com.slyak.es.hibernate.assembler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 对象组装器.
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/19.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public interface EntityAssembler<T> {

	/**
	 * 组装
	 *
	 * @param bean bean
	 */
	void assemble(T bean);

	/**
	 * 批量组装
	 *
	 * @param beans beans
	 */
	void massemble(Iterable<T> beans);
}
