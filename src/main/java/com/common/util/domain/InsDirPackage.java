package com.common.util.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * 文件描述
 **/
@XStreamAlias(value = "InsDirPackage")
public class InsDirPackage {

    @XStreamAlias(value = "l_dept_id")
    private Integer lDeptId;
    @XStreamAlias(value = "l_org_id")
    private String lOrgId;
    @XStreamAlias(value = "l_instr_count")
    private String lInstrCount;
    @XStreamAlias(value = "vc_session_id")
    private String vcSessionId;
    @XStreamAlias(value = "Instruction")
    private List<Instruction> instructions;
    @XStreamAlias(value = "User")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getlInstrCount() {
        return lInstrCount;
    }

    public void setlInstrCount(String lInstrCount) {
        this.lInstrCount = lInstrCount;
    }

    public Integer getlDeptId() {
        return lDeptId;
    }

    public void setlDeptId(Integer lDeptId) {
        this.lDeptId = lDeptId;
    }

    public String getlOrgId() {
        return lOrgId;
    }

    public void setlOrgId(String lOrgId) {
        this.lOrgId = lOrgId;
    }

    public String getVcSessionId() {
        return vcSessionId;
    }

    public void setVcSessionId(String vcSessionId) {
        this.vcSessionId = vcSessionId;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }
}
