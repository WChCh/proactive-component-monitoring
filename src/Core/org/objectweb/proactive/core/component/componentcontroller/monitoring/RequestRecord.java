package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/**
 * Stores the data and timestamps for a request.
 * 
 * @author cruz
 *
 */
public class RequestRecord extends AbstractRecord implements Serializable {

	private String callerComponent;
	private String calledComponent;
	private String interfaceName;
	private String methodName;

	private long arrivalTime;
	private long servingStartTime;
	private long replyTime;
	
	private boolean finished;
	
	public RequestRecord() {
	}
	
	public RequestRecord(ComponentRequestID requestID, String callerComponent, String calledComponent, String interfaceName, String methodName, long arrivalTime) {
		super(RecordType.RequestRecord, requestID);
		this.callerComponent = callerComponent;
		this.calledComponent = calledComponent;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.arrivalTime = arrivalTime;
		this.finished = false;
	}
	
	public long getServingStartTime() {
		return servingStartTime;
	}

	public void setServingStartTime(long servingStartTime) {
		this.servingStartTime = servingStartTime;
	}

	public long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(long replyTime) {
		this.replyTime = replyTime;
	}

	public String getCallerComponent() {
		return callerComponent;
	}
	
	public String getCalledComponent() {
		return calledComponent;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}
	
	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public boolean isFinished() {
		return finished;
	}
	
	
	
	
	
}