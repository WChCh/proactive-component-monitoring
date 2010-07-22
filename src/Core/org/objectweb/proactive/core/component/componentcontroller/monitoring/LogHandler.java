package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Interface for storing monitoring records in the Log Store
 * 
 * Very basic management of log entries.
 * 
 * @author cruz
 *
 */
public interface LogHandler {

	// init the logs store
	void init();
	
	// inserts new record in the store
	void insert(AbstractRecord record);
	
	// fetches an existing record in the store
	AbstractRecord fetch(Object key, RecordType rt);
	RequestRecord fetchRequestRecord(Object key);
	CallRecord fetchCallRecord(Object key);
	
	// queries the existence of a record in the store
	BooleanWrapper exists(Object key, RecordType rt);

	// updates an existing record
	void update(Object key, AbstractRecord record);
	
	// test: obtain logs
	Map<ComponentRequestID, RequestRecord> getRequestLog();
	Map<ComponentRequestID, CallRecord> getCallLog();
	
	// obtain subset of entries
	Map<ComponentRequestID, CallRecord> getCallRecordsFromParent(ComponentRequestID id);
	Map<ComponentRequestID, RequestRecord> getRequestRecordsFromRoot(ComponentRequestID rootID);
	
	// clean the logs
	void reset();
	
	public List<ComponentRequestID> getListOfRequestIDs();
	public List<ComponentRequestID> getListOfCallIDs();
	
}