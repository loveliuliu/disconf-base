package com.baidu.disconf.web.service.config.condition;

import com.baidu.disconf.web.service.config.bo.ConfigDraft;

import java.util.ArrayList;
import java.util.List;

public class ConfigDraftCondition extends ConfigDraft {

    public ConfigDraftCondition() {
    }



    private List<String> draftTypeList = new ArrayList<String>();

    public List<String> getDraftTypeList() {
        return draftTypeList;
    }

    public void setDraftTypeList(List<String> draftTypeList) {
        this.draftTypeList = draftTypeList;
    }
}

