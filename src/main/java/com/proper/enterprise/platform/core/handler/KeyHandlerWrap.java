/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/handler/KeyHandlerWrap.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年3月2日
 * </p>
 * @author: 王浩鹏<wanghaopeng@propersoft.cn>
 */
package com.proper.enterprise.platform.core.handler;

import com.proper.enterprise.platform.core.api.IHandler;
import com.proper.enterprise.platform.core.api.KeyHandler;

/**
 * 关键字标识的处理器包装类.
 * <p>
 * 用于为简单的处理器添加关键字属性。
 * </p>
 * 
 * @author 王浩鹏<wanghaopeng@propersoft.cn>
 * @version 0.2.0 新建;
 * @since 0.2.0
 */
public class KeyHandlerWrap<K> implements KeyHandler<K> {
    /**
     * 关键字.
     * 
     * @since 0.2.0
     */
    private K key;

    /**
     * 被包装对象.
     * 
     * @since 0.2.0
     */
    private IHandler innerWrap;

    /**
     * 默认构造函数.
     */
    public KeyHandlerWrap() {

    }

    /**
     * 指定唯一标识和被包装处理器的构造函数.
     * 
     * @param key 标识.
     * @param handler 被包装的处理器.
     * @since 0.2.0
     */
    public KeyHandlerWrap(K key, IHandler handler) {
        this();
        this.setKey(key);
        this.setInner(handler);
    }

    @Override
    public K getKey() {
        return key;
    }

    /**
     * 设置唯一标识.
     * 
     * @param key 唯一标识.
     * @since 0.2.0
     */
    protected void setKey(K key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public <H extends IHandler> H getInner() {
        return (H) innerWrap;
    }

    /**
     * 设置被包装处理器.
     * 
     * @param handler 被包装处理器.
     * @since 0.2.0
     */
    protected <H extends IHandler> void setInner(H handler) {
        this.innerWrap = handler;
    }

}
