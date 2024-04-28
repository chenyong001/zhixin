package com.tansci.common.enums;
/**
 *  枚举基类
 *
 * @author xinwei
 * @since 2023/7/25
 */

public interface CommonEnumClass<K,V>  {
    K getCode();
    V getMessage();



    // 默认实现，根据 key 获取枚举值
    static <E extends Enum<E> & CommonEnumClass<K,V>, K,V> E fromKey(Class<E> enumClass, K key) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getCode().equals(key)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumClass.getSimpleName() + " with key " + key);
    }
    // 默认实现，根据 display 获取枚举值
    static <E extends Enum<E> & CommonEnumClass<K,V>, K,V> E fromDisplay(Class<E> enumClass, V display) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getMessage().equals(display)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumClass.getSimpleName() + " with display " + display);
    }
    // 默认实现，根据 name 获取枚举值
    static <E extends Enum<E> & CommonEnumClass<K,V>, K,V> E fromName(Class<E> enumClass, String name) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumClass.getSimpleName() + "." + name);
    }
}