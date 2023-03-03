package com.slyak.es.hibernate.assembler;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 单对象组装器.
 * <p/>
 */
public abstract class EntityAssemblerOne<T, K, V> implements EntityAssembler<T> {

    @Override
    public void assemble(T bean) {
        if (bean == null) {
            return;
        }
        K key = getKey(bean);
        if (key != null) {
            setValue(bean, getValue(key));
        }
    }

    @Override
    public void massemble(Iterable<T> beans) {
        if (beans == null) {
            return;
        }

        List<K> keys = new ArrayList<>(32);
        for (T bean : beans) {
            K k = getKey(bean);
            if (bean != null && k != null) {
                keys.add(k);
            }
        }
        if (keys.isEmpty()) {
            return;
        }

        if (!CollectionUtils.isEmpty(keys)) {
            Map<K, V> map = mgetValue(keys);
            if (map != null) {
                for (T bean : beans) {
                    K k = getKey(bean);
                    if (bean != null && k != null) {
                        setValue(bean, map.get(k));
                    }
                }
            }
        }
    }

    /**
     * 从原对象中提取键
     *
     * @param bean 原对象
     * @return 键
     */
    protected abstract K getKey(T bean);

    /**
     * 设置原对象目标值
     *
     * @param bean  原对象
     * @param value 目标值
     */
    protected abstract void setValue(T bean, V value);

    /**
     * 根据键获取目标值
     *
     * @param key 键
     * @return 目标值
     */
    protected abstract V getValue(K key);

    /**
     * 批量获取值
     *
     * @param keys 键列表
     * @return 目标值map
     */
    protected abstract Map<K, V> mgetValue(Collection<K> keys);
}
