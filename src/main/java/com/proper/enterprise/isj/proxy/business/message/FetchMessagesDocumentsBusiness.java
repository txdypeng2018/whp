package com.proper.enterprise.isj.proxy.business.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.UserIdContext;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.repository.MessagesRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchMessagesDocumentsBusiness<M extends UserIdContext<List<MessagesDocument>> & ModifiedResultBusinessContext<List<MessagesDocument>>>
        implements IBusiness<List<MessagesDocument>, M> {

    @Autowired
    MessagesRepository messagesRepo;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(messagesRepo.findByUserIdOrderByCreateTimeDesc(ctx.getUserId()));
    }

}