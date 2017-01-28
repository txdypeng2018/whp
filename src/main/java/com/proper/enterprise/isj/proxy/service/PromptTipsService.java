package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.entity.PromptTipsEntity;

import java.util.List;

/**
 * 温馨提示Service接口.
 */
public interface PromptTipsService {

    PromptTipsEntity getTipsInfo(String infoType, String typeName, String info, String pageNo, String pageSize) throws Exception;

    void saveTipInfo(BaseInfoEntity tipInfo) throws Exception;

    void deleteTipInfo(List<String> idList) throws Exception;

    BaseInfoEntity getTipInfoById(String id) throws Exception;
}
