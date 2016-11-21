package com.proper.enterprise.isj.rule.repository;

import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.platform.core.annotation.CacheQuery;
import com.proper.enterprise.platform.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface RuleRepository extends BaseRepository<RuleEntity, String> {

    @CacheQuery
    Collection<RuleEntity> findByCatalogue(String catalogue);

    List<RuleEntity> findByCatalogueLikeAndNameLikeAndRuleLike(String catalogue, String name, String rule);

    Page<RuleEntity> findByCatalogueLikeAndNameLikeAndRuleLike(String catalogue, String name, String rule, Pageable pageable);

    RuleEntity findById(String id);

}
