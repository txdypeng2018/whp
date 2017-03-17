/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IStrategy.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月21日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * 策略模式接口.
 * <p>
 * 策略模式是用于分离算法和对象的.当开发中的某一功能有多种算法或实现，可以使用策略模式来完成
 * 不同算法或实现的选择。在业务层面，例如：
 * <ul>
 * <li>不同科室不同的挂号规则;</li>
 * <li>不同医院不同放号规则;</li>
 * <li>不同医院不同后台退费规则。</li>
 * </ul>
 * 在具体的代码中也会面临相似的问题。例如：
 * <ul>
 * <li>不同支付渠道的交易编码生成；</li>
 * <li>{@link GroupHandler}中多个子过程调用顺序，在不同业务中不一样,有的需要并发调用,有的需要根据条件决定是否执行;</li>
 * <li>工厂类有不同的实例化实现方式，通过new操作创建实例，或者从Spring容器中获取实例，或者从原型clone等等.</li>
 * </ul>
 * 策略模式是代码重构时常用的模式，用于归纳同一功能的不同实现。在首次开发的功能中，可以不必过多考虑此模式的使用.
 * </p>
 * @author 王东石<wangdongshi@propersoft.cn>
 *  
 * @version 0.1.0 新建;
 * @version 0.2.0 迁移到平台.
 * @since 0.1.0
 *
 */
public interface IStrategy extends IHandler {

}
