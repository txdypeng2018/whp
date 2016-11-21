package com.proper.enterprise.isj.proxy.utils.cache.mock
import com.proper.enterprise.isj.proxy.document.SubjectDocument
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
@CacheConfig(cacheNames = 'pep-temp')
class MockWebServiceDataSecondCacheUtil extends WebServiceDataSecondCacheUtil {

    /**
     *
     * @return Map<String levelCode, Map<String parentId, List>>
     * @throws Exception
     */
    public Map<String, Map<String, List<SubjectDocument>>> getCacheSubjectAndDoctorDocument() throws Exception {
        def d11 = new SubjectDocument('011193', '金冶')
        def d21 = new SubjectDocument('012193', '冶金')
        def d31 = new SubjectDocument('013193', '炼金')

        def s3 = new SubjectDocument('zhmz', '综合门诊')
        s3.subjects = [
                new SubjectDocument('zy3', '中医3'),
                new SubjectDocument('1925', '耳科门诊(3)'),
                new SubjectDocument('1959', '喉科3'),
                new SubjectDocument('1234', '皮肤门诊')
        ]

        return [
            // DeptLevel.CHILD
            '1': [
                '0': [new SubjectDocument('1207', '南湖院区')],
                '1207': [
                    new SubjectDocument('zyk', '中医科1', [new SubjectDocument('0010', '中医科')]),
                    new SubjectDocument('1265', '耳鼻喉', [new SubjectDocument('ebh2', '耳科')]),
                    s3
                ],
                'zyk': [new SubjectDocument('0010', '中医科')],
                '1265': [new SubjectDocument('ebh2', '耳科')],
                'zhmz': s3.subjects
            ],
            // DeptLevel.DOCTOR
            '2': [
                '0010': [d11],
                'ebh2': [d21],
                '1925': [d31]
            ]
        ];
    }

}
