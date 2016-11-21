package com.proper.enterprise.isj.proxy.repository;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 基本信息
 */
public interface BaseInfoRepository extends BaseRepository<BaseInfoEntity, String> {

    List<BaseInfoEntity> findByInfoType(String infoType);

    List<BaseInfoEntity> findByInfoTypeLikeAndTypeNameLikeAndInfoLike(String infoType, String typeName, String info);

    Page<BaseInfoEntity> findByInfoTypeLikeAndTypeNameLikeAndInfoLike(String infoType, String typeName, String info, Pageable pageable);

    BaseInfoEntity findById(String id);

}
