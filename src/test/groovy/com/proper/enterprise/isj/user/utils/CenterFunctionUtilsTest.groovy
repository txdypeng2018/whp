package com.proper.enterprise.isj.user.utils

import org.junit.Test

import com.proper.enterprise.platform.test.AbstractTest

class CenterFunctionUtilsTest extends AbstractTest {

    @Test
    void testConstValues(){
        
        def map = CenterFunctionUtils.getRegLevelNameMap()
        assert map.size() > 0
        
    }
}
