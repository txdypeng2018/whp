package com.proper.enterprise.isj.function.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class BuildPageRequestFunction implements IFunction<PageRequest> {

    public static interface SortBuilder {
        Sort build();
    }

    @Override
    public PageRequest execute(Object... params) throws Exception {
        return buildPageRequest(((Number) params[0]).intValue(), ((Number) params[1]).intValue(),
                params.length >= 3 ? (SortBuilder) params[2] : null);
    }

    public static PageRequest buildPageRequest(int pageNo, int pageSize, SortBuilder builder) {
        return new PageRequest(pageNo - 1, pageSize, builder == null ? null : builder.build());
    }

}