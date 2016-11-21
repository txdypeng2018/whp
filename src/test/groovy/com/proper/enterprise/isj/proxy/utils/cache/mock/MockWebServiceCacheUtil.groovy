package com.proper.enterprise.isj.proxy.utils.cache.mock

import com.proper.enterprise.isj.proxy.document.DoctorDocument
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
@CacheConfig(cacheNames = 'pep-temp')
class MockWebServiceCacheUtil extends WebServiceCacheUtil {

    public Map<String, Set<String>> getCacheDoctorInfoLike() throws Exception {
        return ['金冶': ['011193'], '冶金': ['012193'], '炼金': ['013193']]
    }

    public Map<String, DoctorDocument> getCacheDoctorDocument() throws Exception {
        DoctorDocument jy = new DoctorDocument();
        jy.setId("011193");
        jy.setName('金冶');
        DoctorDocument yj = new DoctorDocument();
        yj.setId("012193");
        yj.setName('冶金');
        DoctorDocument lj = new DoctorDocument();
        lj.setId("013193");
        lj.setName('炼金');
        return [
                '011193': jy,
                '012193': yj,
                '013193': lj
        ];
    }

}
