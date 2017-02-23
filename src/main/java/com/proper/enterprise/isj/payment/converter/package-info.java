/**
 * 日志转换器包.
 * <p>
 * 主要内容是{@linkplain com.proper.enterprise.isj.payment.converter.Converter Converter}类
 * 与其扩展类。<br/>
 * 包括：
 * <ul>
 * <li>
 * 用于组合转换任务的{@linkplain com.proper.enterprise.isj.payment.converter.GroupConverter GroupConverter}类
 * 和{@linkplain com.proper.enterprise.isj.payment.converter.MultiConverter MultiConverter}类;
 * </li>
 * <li>
 * 用于根据输入自动寻找匹配转换器的{@linkplain com.proper.enterprise.isj.payment.converter.ConverterManager ConverterManager}转换器管理接口和
 * {@linkplain com.proper.enterprise.isj.payment.converter.ManagedConverter}受管转换器接口,以及他们的子接口和实现类，并且提供了基于Spring IoC容器
 * 管理类和受管类的实现;
 * </li>
 * <li>
 * 各种业务类对应的转换器;
 * </li>
 * <li>
 * 其他工具类,例如：支付日志类，支付日志的数据库实体类,各种工程类,支付渠道和退款渠道的定义等等。
 * </li>
 * </p>
 */
package com.proper.enterprise.isj.payment.converter;
