package com.proper.enterprise.platform.auth.common.mock
import com.proper.enterprise.platform.api.auth.model.User
import com.proper.enterprise.platform.auth.common.entity.UserEntity
import com.proper.enterprise.platform.auth.common.service.impl.CommonUserServiceImpl
import com.proper.enterprise.platform.core.utils.ConfCenter
import com.proper.enterprise.platform.core.utils.RequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service("mockUserService")
@Primary
class MockUserServiceImpl extends CommonUserServiceImpl {

    def static final Logger LOGGER = LoggerFactory.getLogger(MockUserServiceImpl.class)

    @Override
    User getCurrentUser() {
        if (ConfCenter.get('test.mockUser.throwEx') == 'true') {
            throw new Exception('Mock to throw exception in getCurrentUser')
        } else {
            def mockUser
            try {
                mockUser = RequestUtil.getCurrentRequest().getAttribute('mockUser')
            } catch (IllegalStateException e) {
                LOGGER.debug("Could not get current request! {}", e.getMessage());
            }
            def user
            if (mockUser != null) {
                user = new UserEntity(mockUser.username, mockUser.password)
                user.id = mockUser.id
                user.superuser = mockUser.isSuper
            } else {
                user = new UserEntity('default-mock-user', 'default-mock-user-pwd')
                user.setId('default-mock-user-id')
            }
            return user

        }
    }

}