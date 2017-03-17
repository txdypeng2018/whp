/*
 * 源文件:/pep-core/src/main/java/com/proper/enterprise/platform/core/api/IContext.java
 * <p>
 * Copyright(c)2017 沈阳普日软件技术有限公司 保留所有权利
 * </p>
 * <p>
 * date: 2017年2月22日
 * </p>
 * @author: 王东石<wangdongshi@propersoft.cn>
 */

package com.proper.enterprise.platform.core.api;

/**
 * {@code IContext}接口用于表示一个上下文类型.
 * <p>
 * 上下文对象用于保存同意任务中跨过程的状态，可能是内存级的，也可能是被持久化的。
 * 上下文中的状态会在统一任务中共享，会影响任务中其他处理过程的执行。<br/>
 * 类似称呼：{@code Environment}(环境)。<br/>
 * 使用场景：
 * <ul>
 * <li>用于在流程的各步骤间传递数据;</li>
 * <li>保存应用的设置等等.</li>
 * </ul>
 * </p>
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 * @version 0.1.0 2017-02-22 新建;
 * @version 0.2.0 2017-02-28 移植到平台.
 */

public interface IContext {

}
