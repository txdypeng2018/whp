package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 温馨提示Service接口实现类.
 */
@Service
public class PromptTipsServiceImpl implements PromptTipsService{

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Override
    public void saveTipInfo(BaseInfoEntity tipInfo) throws Exception {
        baseInfoRepo.save(tipInfo);
    }

    @Override
    public void deleteTipInfo(List<String> idList) throws Exception {
        List<BaseInfoEntity> baseInfoList = baseInfoRepo.findAll(idList);
        baseInfoRepo.delete(baseInfoList);
    }

    @Override
    public BaseInfoEntity getTipInfoById(String id) throws Exception {
        return baseInfoRepo.findById(id);
    }

    /**
     * 获取温馨提示信息.
     *
     * @param infoType
     *        温馨提示类型编码.
     * @param typeName
     *        温馨提示类型名称.
     * @param info
     *        温馨提示内容.
     * @param pageNo
     *        当前页码.
     * @param pageSize
     *        每页数量.
     * @return 温馨提示信息.
     * @throws Exception 异常.
     */
    @Override
    public PromptTipsEntity getTipsInfo(String infoType, String typeName, String info, String pageNo, String pageSize)
            throws Exception {

        PromptTipsEntity retObj = new PromptTipsEntity();

        PageRequest pageReq = buildPageRequest(Integer.parseInt(pageNo), Integer.parseInt(pageSize));

        if(StringUtil.isEmpty(infoType)) {
            infoType = "%%";
        } else {
            infoType = "%" + infoType + "%";
        }

        if(StringUtil.isEmpty(typeName)) {
            typeName = "%%";
        } else {
            typeName = "%" + typeName + "%";
        }

        if(StringUtil.isEmpty(info)) {
            info = "%%";
        } else {
            info = "%" + info + "%";
        }

        int count = baseInfoRepo.findByInfoTypeLikeAndTypeNameLikeAndInfoLike(infoType, typeName, info).size();
        Page<BaseInfoEntity> pageInfo = baseInfoRepo.findByInfoTypeLikeAndTypeNameLikeAndInfoLike(infoType, typeName, info, pageReq);

        List<BaseInfoEntity> tipsList = pageInfo.getContent();

        // 设置总数
        retObj.setCount(count);
        // 设置列表
        retObj.setData(tipsList);

        return retObj;
    }

    /**
      * 创建分页请求.
      */
    private PageRequest buildPageRequest(int pageNo, int pageSize) {
        return new PageRequest(
                pageNo - 1, pageSize,
                new Sort(Sort.Direction.DESC, "createTime")
                        .and(new Sort(Sort.Direction.DESC, "typeName"))
        );
    }

}
