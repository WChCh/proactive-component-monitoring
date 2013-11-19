/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEvent;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventListener;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class MetricsStoreImpl extends AbstractPAComponentController implements MetricsStore, RemmosEventListener, BindingController {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);

	/** bound components **/
	private RecordStore records;
	
	/** Metrics stored in this component */
	private Map<String, Metric<?>> metrics;
	private String[] itfList = { Remmos.RECORD_STORE_ITF };
	
	
	@Override
	public void init() {
		metrics = new HashMap<String, Metric<?>>();
	}
	
	@Override
	public void addMetric(String name, Metric<?> metric) {
		metric.setRecordSource(records);
		metrics.put(name, metric);
	}

	@Override
	public Object calculate(String name) {
		Metric<?> metric = metrics.get(name);
		if(metric != null)
			return metric.calculate();
		return null;
	}
	
	@Override
	public Object calculate(String name, Object[] params) {
		Metric<?> metric = metrics.get(name);
		if(metric != null)
			return metric.calculate(params);
		return null;
	}

	@Override
	public void disableMetric(String name) {
		Metric<?> metric = metrics.get(name);
		if(metric != null) {
			metric.disable();
		}
	}

	@Override
	public void enableMetric(String name) {
		Metric<?> metric = metrics.get(name);
		if(metric != null) {
			metric.enable();
		}
	}

	@Override
	public Object getValue(String name) {
		Metric<?> metric = metrics.get(name);
		if(metric != null) {
			return metric.getValue();
		}
		return null;
	}

	@Override
	public void removeMetric(String name) {
		metrics.remove(name);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(String name, Object v) {
		Metric metric = metrics.get(name);
		if(metric != null) {
			metric.setValue(v);
		}
	}
	
	@Override
	public List<String> getMetricList() {
		Set<String> keys = metrics.keySet();
		List<String> res = new ArrayList<String>(keys.size());
		res.addAll(keys);
		return res;
	}


	@Override
	public void bindFc(String itfName, Object obj) throws NoSuchInterfaceException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			records = (RecordStore) obj;
		}
		else {
			throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
		}
	}

	@Override
	public String[] listFc() {
		return itfList;
	}

	@Override
	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			return records;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
	}

	@Override
	public void unbindFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			records = null;
		}
		else {
			throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
		}
	}

	@Override
	public void onEvent(RemmosEvent re) {
		// check all the metrics stored. If the metric is subscribed for the event, recalculate it.
		// System.out.println("EVENT ON " + hostComponent.getComponentParameters().getControllerDescription().getName() + ": " + re.getType());
		for(Metric<?> metric : metrics.values()) {
			if(metric.isSubscribedTo(re.getType())) {
				metric.calculate(new Object[] {re});
			}
		}
	}

}
