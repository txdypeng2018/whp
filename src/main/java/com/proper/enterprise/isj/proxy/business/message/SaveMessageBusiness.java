package com.proper.enterprise.isj.proxy.business.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.FunctionToolkit;
import com.proper.enterprise.isj.context.MessagesDocumentContext;
import com.proper.enterprise.isj.function.message.SaveSingleMessageFunction;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class SaveMessageBusiness<M extends MessagesDocumentContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    FunctionToolkit toolkit;

    @Override
    public void process(M ctx) throws Exception {
        MessagesDocument msg = ctx.getMessagesDocument();
        toolkit.executeFunction(SaveSingleMessageFunction.class, msg);
    }

}