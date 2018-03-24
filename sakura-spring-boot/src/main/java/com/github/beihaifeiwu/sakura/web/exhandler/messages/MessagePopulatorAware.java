package com.github.beihaifeiwu.sakura.web.exhandler.messages;

import org.springframework.beans.factory.Aware;

/**
 * Created by liupin on 2017/6/29.
 */
public interface MessagePopulatorAware extends Aware {

    void setMessagePopulator(MessagePopulator messagePopulator);

}
