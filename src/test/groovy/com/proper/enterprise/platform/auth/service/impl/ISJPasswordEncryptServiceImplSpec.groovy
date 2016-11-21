package com.proper.enterprise.platform.auth.service.impl

import spock.lang.Specification

class ISJPasswordEncryptServiceImplSpec extends Specification {

    def service = new ISJPasswordEncryptServiceImpl()

    def "#pwd after encrypt is #encrypted"() {
        expect:
        service.encrypt(pwd) == encrypted

        where:
        pwd         | encrypted
        '123456'    | '453098036b3e8d9cf37ebed71362d465302fe632a1795b82efb6b587166c098b'
        ''          | 'ef15b3c76001cf25685eb33b7e809434d3a238882887fa401ed89e62efeba5d0'
    }

    def "Should throw exception when password is null"() {
        when:
        service.encrypt(null)

        then:
        thrown(IllegalArgumentException)
    }

}
