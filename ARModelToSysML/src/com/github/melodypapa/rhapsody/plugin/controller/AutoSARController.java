package com.github.melodypapa.rhapsody.plugin.controller;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPort;

public abstract class AutoSARController extends AbstractController {

	public AutoSARController(IRPApplication application) {
		super(application);
	}
	
	/**
	 * The current model element contains OperationInvokedEvent stereotype or not
	 * @param element model element
	 * @return
	 * 		true: model element contains OperationInvokedEvent stereotype
	 * 		false: model element does not contain OperationInvokedEvent stereotype
	 */
	protected boolean checkStereoTypeIsOperationInvokedEvent(IRPModelElement element){
		return this.checkStereoType(element, "OperationInvokedEvent");
	}
	
	protected boolean checkStereoTypeIsTimingEvent(IRPModelElement element){
		return this.checkStereoType(element, "TimingEvent");
	}
	
	protected boolean checkStereoTypeIsInternalTriggerOccurredEvent(IRPModelElement element){
		return this.checkStereoType(element, "InternalTriggerOccurredEvent");
	}
	
	protected boolean checkStereoTypeIsSwcModeSwitchEvent(IRPModelElement element){
		return this.checkStereoType(element, "SwcModeSwitchEvent");
	}
	
	/**
	 * The current model element contains dataReceiverPort stereotype or not
	 * @param element port element
	 * @return
	 * 		true: Port element contains dataReceiverPort stereotype
	 * 		false: Port element does not contain dataReceiverPort stereotype
	 */
	protected boolean checkStereoTypeIsDataSenderPort(IRPPort element){
		return this.checkStereoType(element, "dataSenderPort");
	}
	
	/**
	 * The current model element contains dataReceiverPort stereotype or not
	 * @param element port element
	 * @return 
	 * 		true: Port element contains dataReceiverPort stereotype
	 * 		false: Port element does not contain dataReceiverPort stereotype
	 */
	protected boolean checkStereoTypeIsDataReceiverPort(IRPPort element){
		return this.checkStereoType(element, "dataReceiverPort");
	}
	
	/**
	 * The current model element contains ARPackage stereotype or not
	 * @param element model element
	 * @return
	 * 		true: Port element contains ARPackage stereotype
	 * 		false: Port element does not contain ARPackage stereotype
	 */
	protected boolean checkStereoTypeIsARPackage(IRPModelElement element){
		return this.checkStereoType(element, "ARPackage");
	}
	
	/**
	 * The current model element contains symbol stereotype or not
	 * @param element model element
	 * @return
	 * 		true: Port element contains symbol stereotype
	 * 		false: Port element does not contain symbol stereotype
	 */
	protected boolean checkStereoTypeIsSymbol(IRPModelElement element){
		return this.checkStereoType(element, "symbol");
	}
	
	/**
	 * The current model element contains ApplicationSwComponentType stereotype or not
	 * @param element model element
	 * @return 
	 * 		true: Model element contains ApplicationSwComponentType stereotype
	 * 		false: Model element does not contain ApplicationSwComponentType stereotype
	 */
	protected boolean checkStereoTypeIsAtomicComponentType(IRPClass element){
		boolean result = false;
		
		if (this.checkStereoType(element, "ApplicationSwComponentType")){
			result = true;
		}
		else if (this.checkStereoType(element, "ComplexDeviceDriverSwComponentType")){
			result = true;
		}
		else if (this.checkStereoType(element, "SensorActuatorSwComponentType")){
			result = true;
		}
		
		return result;
	}

}
