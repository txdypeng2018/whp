package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.NavInfoIdsContext;
import com.proper.enterprise.isj.proxy.repository.NavInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class DelWebNavInfoBusiness<M extends NavInfoIdsContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    NavInfoRepository navRepo;

    @Override
    public void process(M ctx) {
        navRepo.delete(navRepo.findAll(ctx.getNavInfoIds()));
    }

}