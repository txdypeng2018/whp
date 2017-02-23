package com.proper.enterprise.isj.payment.logger.repository;

import com.proper.enterprise.isj.payment.logger.entity.DefaultPayLogRecordEntity;
import com.proper.enterprise.platform.core.repository.BaseRepository;

import java.util.List;

/**
 * 支付日志记录仓库.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayLogRecordRepository extends BaseRepository<DefaultPayLogRecordEntity, String> {

    List<DefaultPayLogRecordEntity> findByStepAndStepStatus(int step, int stepStatus);
}
