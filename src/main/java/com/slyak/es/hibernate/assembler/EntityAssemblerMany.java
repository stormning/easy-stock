package com.slyak.es.hibernate.assembler;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/22.
 */
public abstract class EntityAssemblerMany<T, K, V> implements EntityAssembler<T> {

    @Override
    public void assemble(T bean) {
        if (bean == null) {
            return;
        }
        setValue(bean, safeGetValue(getKeys(bean)));
    }

    @Override
    public void massemble(Iterable<T> beans) {
        if (beans == null || !beans.iterator().hasNext()) {
            return;
        }
        Set<K> keys = new HashSet<>();
        for (T bean : beans) {
            if (bean != null) {
                List<K> ks = getKeys(bean);
                if (!CollectionUtils.isEmpty(ks)) {
                    for (K k : ks) {
                        keys.add(k);
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(keys)) {
            Map<K, V> valueMap = mgetValue(keys);

            for (T bean : beans) {
                if (bean != null) {
                    List<K> ks = getKeys(bean);
                    if (!CollectionUtils.isEmpty(ks)) {
                        List<V> values = new ArrayList<>();
                        for (K k : ks) {
                            values.add(valueMap.get(k));
                        }
                        setValue(bean, values);
                    }
                }
            }
        }
    }

    protected abstract void setValue(T bean, List<V> value);

    private List<V> safeGetValue(List<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        return getValue(keys);
    }

    protected abstract List<V> getValue(List<K> keys);

    protected abstract List<K> getKeys(T bean);

    protected abstract Map<K, V> mgetValue(Collection<K> keys);
}
