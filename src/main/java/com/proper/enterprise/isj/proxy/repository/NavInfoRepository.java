package com.proper.enterprise.isj.proxy.repository;

import com.proper.enterprise.isj.proxy.entity.NavInfoEntity;
import com.proper.enterprise.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 医院导航
 */
public interface NavInfoRepository extends BaseRepository<NavInfoEntity, String> {

    List<NavInfoEntity> findByNavIdOrderByNavIdAsc(String id);

    List<NavInfoEntity> findByParentTypeAndParentIdLikeAndNavNameLike(String parentType, String parentId, String navName);

    Page<NavInfoEntity> findByParentTypeAndParentIdLikeAndNavNameLike(String parentType, String parentId, String navName, Pageable pageReq);

    List<NavInfoEntity> findByParentIdLikeAndParentTypeAndNavNameLike(String parentId, String parentType, String deptName);

    Page<NavInfoEntity> findByParentIdLikeAndParentTypeAndNavNameLike(String parentId, String parentType, String deptName, Pageable pageReq);

    NavInfoEntity findTopByParentIdOrderByNavIdDesc(String parentId);

    NavInfoEntity findByNavId(String navId);

    List<NavInfoEntity> findByParentId(String parentId);

    List<NavInfoEntity> findByParentType(String parentType);
}
