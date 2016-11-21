package com.proper.enterprise.isj.webservices.utils

import spock.lang.Specification

class ReqEncryptedAdapterSpec extends Specification {

    def adapter = new ReqEncryptedAdapter()

    def "Test nested map"() {
        def data = ['a':'b', 'c': [['ca1': '1', 'ca2': '2'], ['cb1': '1', 'cb2': '2']]]
        def result = adapter.iterateCollection(data)

        expect:
        result == '<a><![CDATA[b]]></a><c><ca1><![CDATA[1]]></ca1><ca2><![CDATA[2]]></ca2></c><c><cb1><![CDATA[1]]></cb1><cb2><![CDATA[2]]></cb2></c>'
    }

}
