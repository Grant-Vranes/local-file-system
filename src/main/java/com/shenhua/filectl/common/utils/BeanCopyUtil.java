package com.shenhua.filectl.common.utils;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.core.Converter;
import org.springframework.objenesis.ObjenesisStd;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用CGLIB实现高效的Bean copy
 */
@Slf4j
public final class BeanCopyUtil {

    public static final Set<String> BEANCOPY_IGNOREFIELDS = Sets.newHashSet("setId", "setCreateBy", "setCreateTime", "setCreateName",
            "setUpdateBy", "setUpdateName", "setUpdateTime", "setDeleted");
    private BeanCopyUtil() {}
    private static ThreadLocal<ObjenesisStd> objenesisStdThreadLocal = ThreadLocal.withInitial(ObjenesisStd::new);
    private static ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<?>, BeanCopier>> cache = new ConcurrentHashMap<>();


    public static <T> T copy(Object source, Class<T> target) {
        return copy(source, objenesisStdThreadLocal.get().newInstance(target));
    }

    public static <T> T copy(Object source, Class<T> target, Set<String> ignoreFields) {
        return copy(source, objenesisStdThreadLocal.get().newInstance(target), ignoreFields);
    }

    public static <T> T copy(Object source, T target) {
        BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
        return target;
    }

    public static <T> List<T> copyList(List<?> sources, Class<T> target) {
        if (sources.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<T> list = new ArrayList<>(sources.size());
        ObjenesisStd objenesisStd = objenesisStdThreadLocal.get();
        for (Object source : sources) {
            if (source == null) {
                log.error("sources List have null object => {}", source.toString());
            }
            T newInstance = objenesisStd.newInstance(target);
            BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target);
            beanCopier.copy(source, newInstance, null);
            list.add(newInstance);
        }
        return list;
    }

    public static <T> T mapToBean(Map<?, ?> source, Class<T> target) {
        T bean = objenesisStdThreadLocal.get().newInstance(target);
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(source);
        return bean;
    }

    public static <T> Map<?, ?> beanToMap(T source) {
        return BeanMap.create(source);
    }

    private static <S, T> BeanCopier getCacheBeanCopier(Class<S> source, Class<T> target) {
        ConcurrentHashMap<Class<?>, BeanCopier> copierConcurrentHashMap = cache.computeIfAbsent(source, aClass -> new ConcurrentHashMap<>(16));
        return copierConcurrentHashMap.computeIfAbsent(target, aClass -> BeanCopier.create(source, target, false));
    }

    private static <S, T> BeanCopier getCacheBeanCopier(Class<S> source, Class<T> target, Boolean userConverter) {
        ConcurrentHashMap<Class<?>, BeanCopier> copierConcurrentHashMap = cache.computeIfAbsent(source, aClass -> new ConcurrentHashMap<>(16));
        return copierConcurrentHashMap.computeIfAbsent(target, aClass -> BeanCopier.create(source, target, userConverter));
    }

    public static <T> T copy(Object source, T target, Set<String> ignoreFields) {
        BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target.getClass(), true);
        beanCopier.copy(source, target, new Converter() {
            // context是 setter方法的名字如 setId
            @Override
            public Object convert(Object value, Class targetClass, Object context) {
                String propertyName = (String) context;
                if (ignoreFields.contains(propertyName)) {
                    return null; // 返回 null 表示忽略该字段
                }
                return value;
            }
        });
        return target;
    }

    public static <T> List<T> copyList(List<?> sources, Class<T> target, Set<String> ignoreFields) {
        if (sources.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<T> list = new ArrayList<>(sources.size());
        ObjenesisStd objenesisStd = objenesisStdThreadLocal.get();
        for (Object source : sources) {
            if (source == null) {
                log.error("sources List have null object => {}", source.toString());
            }
            T newInstance = objenesisStd.newInstance(target);
            BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target, true);
            beanCopier.copy(source, newInstance, (value, targetObject, context) -> {
                String fieldName = (String) context;
                if (ignoreFields.contains(fieldName)) {
                    return null; // 返回 null 表示忽略该字段
                }
                return value;
            });
            list.add(newInstance);
        }
        return list;
    }
}