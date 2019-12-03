package com.siemens.dasheng.web.generalmodel.dataconnector;

/**
 * comments
 *
 * @author yupeng.zhou@siemens.com
 * @date 2019/10/30 13:18
 */
public class OpcUaEventsQueryRequest {

    private Long connectorId;

    private Long start;

    private Long end;

    private String nodeId;

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
