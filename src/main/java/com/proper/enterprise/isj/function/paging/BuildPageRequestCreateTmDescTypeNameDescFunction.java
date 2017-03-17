package com.proper.enterprise.isj.function.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IFunction;

/**
 * 创建分页请求.
 * 旧的com.proper.enterprise.isj.proxy.service.impl.PromptTipsServiceImpl.buildPageRequest(int, int)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class BuildPageRequestCreateTmDescTypeNameDescFunction implements IFunction<PageRequest> {

    @Override
    public PageRequest execute(Object... params) throws Exception {
        return buildPageRequest(((Number) params[0]).intValue(), ((Number) params[1]).intValue());
    }

    public static PageRequest buildPageRequest(int pageNo, int pageSize) {
        return new PageRequest(pageNo - 1, pageSize,
                new Sort(Sort.Direction.DESC, "createTime").and(new Sort(Sort.Direction.DESC, "typeName")));
    }

}
