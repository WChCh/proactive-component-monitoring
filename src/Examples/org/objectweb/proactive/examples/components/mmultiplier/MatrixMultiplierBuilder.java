package org.objectweb.proactive.examples.components.mmultiplier;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;
import org.objectweb.proactive.core.node.Node;

public class MatrixMultiplierBuilder {

	public static Component build(PAGCMTypeFactory patf, PAGenericFactory pagf,
			Node node, boolean managed) throws InstantiationException
	{
		List<PAGCMInterfaceType> array = new ArrayList<PAGCMInterfaceType>();
		InterfaceType itfType;
	
		itfType = patf.createGCMItfType(
				MMConstants.MATRIX_MULTIPLIER_ITF,
				MatrixMultiplier.class.getName(),
				PAGCMTypeFactory.SERVER,
				PAGCMTypeFactory.MANDATORY,
				PAGCMTypeFactory.SINGLETON_CARDINALITY);
		array.add((PAGCMInterfaceType) itfType); 
		
		itfType = patf.createGCMItfType(
				MMConstants.RESULT_RECEIVER_ITF,
				ResultReceiver.class.getName(),
				PAGCMTypeFactory.CLIENT,
				PAGCMTypeFactory.OPTIONAL,
				PAGCMTypeFactory.SINGLETON_CARDINALITY);
		array.add((PAGCMInterfaceType) itfType); 

		PAGCMInterfaceType[] fItfType = new PAGCMInterfaceType[array.size()];
		fItfType = array.toArray(fItfType);
	
		PAGCMInterfaceType[] nfItfType = new PAGCMInterfaceType[] {};
		if (managed) {
			nfItfType = Remmos.createMonitorableNFType(patf, fItfType, Constants.COMPOSITE);
		}
		ComponentType compType = patf.createFcType(fItfType, nfItfType);
		ControllerDescription controllerDesc = new ControllerDescription(
				MMConstants.MATRIX_MULTIPLIER_COMP, Constants.COMPOSITE);
		
		return pagf.newFcInstance(compType, controllerDesc, null, node);
	}
}
