package com.common.util.xml.domain;

import lombok.Data;

import java.util.List;

@Data
public class Attributes {
    private String partName;
    private String partDesc;
    private String partType;
    private String partUuid;
    private String updatedBy;
    private String effectivity;
    private String lifeCycleState;
    private String dpMaturat;
    private String createdBy;
    private String createdTime;
    private String lastUpdated;
    private List<IbaAttr> lstIbaAttr;
}