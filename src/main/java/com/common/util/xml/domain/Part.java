package com.common.util.xml.domain;

import lombok.Data;

import java.util.List;

@Data
public class Part {
    public String partId;
    public String version;
    public String parentUuid;
    private Attributes atts;
    private BomUses bomUses;
    private List<Part> subPart;
}