package com.github.melodypapa.rhapsody.plugin.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPComment;
import com.telelogic.rhapsody.core.IRPDependency;
import com.telelogic.rhapsody.core.IRPInstance;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPPort;
import com.telelogic.rhapsody.core.IRPTag;
import com.telelogic.rhapsody.core.RhapsodyRuntimeException;

public class ARModelToSysMLController extends AutoSARController {
	
	private static final String PACKAGE_FLOW_NAME = "SignalFlows";

	public ARModelToSysMLController(IRPApplication application) {
		super(application);
	}
	
	private String formatPortName(String name){
		Pattern	pattern = Pattern.compile("(?:rp|pp)_(\\w+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		
		matcher = pattern.matcher(name);
		if (matcher.matches()){
			return matcher.group(1);
		}
		return name;
	}
	
	private void convertARModelSenderReceiverPorts(IRPClass atomicSwComponent, IRPClass blockElement){
		IRPCollection portCollection;
		int			  portTotal;
		
		portCollection = atomicSwComponent.getNestedElementsByMetaClass("Port", 1);
		portTotal      = portCollection.getCount();
		
		for (int next = 1; next <= portTotal; next ++){
			Object item = portCollection.getItem(next);
			if (item instanceof IRPPort){
				IRPPort port = (IRPPort) item;
				
				/* Skip the client port */
				if (checkStereoTypeIsDataSenderPort(port) || checkStereoTypeIsDataReceiverPort(port)){
					
					/* Create the Signal Flow Block */
					IRPClass itemFlowType = createSysMLSignalFlowBlock(atomicSwComponent, port);
					
					createSysMLBlockFlowProperties(atomicSwComponent, blockElement, port, itemFlowType);
					createSysMLBlockFullPorts(atomicSwComponent, blockElement, port, itemFlowType);
					//log(String.format("Set contract %s => %s", itemType.getName(), blockPort.getName()));
				}
			}
		}
	}
	
	/**
	 * Create the SysML block operation which is extracted from the event of AUTOSAR AtomicSwComponentType Runnable Entity.
	 * @param atomicSwComponent	AUTOSAR AtomicSwComponent
	 * @param blockElement	SysML block
	 * @param event	Runnable entity's event
	 */
	private void createBlockOperation(IRPClass atomicSwComponent, IRPClass blockElement, IRPInstance event){
		IRPCollection dependencyCollection;
		int			  dependencyTotal;
		
		dependencyCollection = event.getNestedElementsByMetaClass("Dependency", 1);
		dependencyTotal      = dependencyCollection.getCount();
		
		for (int next = 1; next <= dependencyTotal; next ++){
			IRPModelElement item = (IRPModelElement) dependencyCollection.getItem(next);
			if (item instanceof IRPDependency){
				IRPDependency dependency = (IRPDependency) item;
				
				if (checkStereoType(dependency, "l_startOnEvent") == false){
					//logger.warning("Unsupport stereotype of runnable entity dependency");
					continue;
				}

				String runnableEntityName = dependency.getDependsOn().getName();
				
				IRPInstance runnableEntity = (IRPInstance) atomicSwComponent.findNestedElementRecursive(runnableEntityName, "Object");
			
				String symbol = getARModelOperationSymbol(runnableEntity);
				if (symbol.equals("") == false){
					findOrCreateModelElementRecursive(blockElement, symbol, "Operation");
					//logger.warning("  Operation: " + symbol + "\n");
				}
				else {
					logger.warning(String.format("Symbol is empty <%s>", runnableEntity.getName()));
				}
			}
		}
	}
	
	private void convertARModelEvents(IRPClass atomicSwComponent, IRPClass blockElement){
		IRPCollection eventCollection;
		int			  eventTotal;
		
		eventCollection = atomicSwComponent.getNestedElementsByMetaClass("Object", 1);
		eventTotal      = eventCollection.getCount();
		
		for (int next = 1; next <= eventTotal; next ++){
			Object item = eventCollection.getItem(next);
			if (item instanceof IRPInstance){
				IRPInstance event = (IRPInstance) item;
				/* Skip the client port */
				if (checkStereoTypeIsOperationInvokedEvent(event)){
					createBlockOperation(atomicSwComponent, blockElement, event);
				}
				else if (checkStereoTypeIsTimingEvent(event)){
					createBlockOperation(atomicSwComponent, blockElement, event);
				}
				else if (checkStereoTypeIsInternalTriggerOccurredEvent(event)){
					createBlockOperation(atomicSwComponent, blockElement, event);
				}
				else if (checkStereoTypeIsSwcModeSwitchEvent(event)){
					createBlockOperation(atomicSwComponent, blockElement, event);
				}
				else {
					//logger.warning("Unsupported Event:" + event.getName());
				}
			}
		}
	}
	
	private IRPModelElement findSysMLBlock(String name){
		IRPCollection packageCollection;
		int			  packageTotal;
		
		packageCollection = getActiveProject().getNestedElementsByMetaClass(META_TYPE_PACKAGE, 1);
		packageTotal      = packageCollection.getCount();
		
		for (int next = 1; next <= packageTotal; next ++){
			IRPPackage packageElement = (IRPPackage) packageCollection.getItem(next);
			if (checkStereoTypeIsARPackage(packageElement)){
				continue;
			}
			IRPClass blockElement = (IRPClass) packageElement.findNestedElementRecursive(name, META_TYPE_CLASS);
			if (blockElement != null){
				return blockElement;
			}
		}
		return null;
	}
	
	private String getARModelCommentSymbol(IRPInstance runnableEntity){
		IRPCollection symbolCollection;
		int			  symbolTotal;
		
		symbolCollection = runnableEntity.getNestedElementsByMetaClass("Comment", 1);
		symbolTotal      = symbolCollection.getCount();
		
		for (int next = 1; next <= symbolTotal; next ++){
			Object item = symbolCollection.getItem(next);
			if (item instanceof IRPComment){
				IRPComment symbol = (IRPComment) item;
				//log(String.format("Symbol %s in runnable entity %s", symbol.getName(), runnableEntity.getName()));
				if (checkStereoTypeIsSymbol(symbol)){
					IRPTag tag = symbol.getTag("value");
					if (tag != null){
						return tag.getValue();
					}
					else {
						logger.warning(String.format("Invalid Symbol of <%s>", runnableEntity.getName()));
					}
				}
			}
		}
		return "";
	}
	
	private String getARModelTagSymbol(IRPInstance runnableEntity){
		for (Object item: runnableEntity.getAllTags().toList()){
			if (item instanceof IRPTag){
				IRPTag tag = (IRPTag) item;
				if (tag.getName().equals("symbol")){
					//logger.info(String.format(" <%s> (%s) in <%s>", tag.getName(), tag.getValue(), runnableEntity.getName()));
					return tag.getValue();
				}
			}
		}
		return "";
	}
	
	private String getARModelOperationSymbol(IRPInstance runnableEntity){
		String symbol;
		
		symbol = getARModelCommentSymbol(runnableEntity);
		if (!symbol.equals("")){
			return symbol;
		}
		
		symbol = getARModelTagSymbol(runnableEntity);
		if (!symbol.equals("")){
			return symbol;
		}
		
		return "";
	}
	
	
	
	private void createSysMLFlowBlockValueAttribute(IRPClass element, IRPClass flowBlock){
		IRPCollection attributeCollection;
		int			  attributeTotal;
		
		attributeCollection = element.getAttributes();
		attributeTotal      = attributeCollection.getCount();
		
		for (int next = 1; next <= attributeTotal; next ++){
			Object item = attributeCollection.getItem(next);
			if (item instanceof IRPAttribute){
				IRPAttribute interfaceAttribute = (IRPAttribute) item;
				findOrCreateModelElementRecursive(flowBlock, interfaceAttribute.getName(), 
						"Attribute", new String[] {"ValueProperty"});
			}
		}
	}
	
	private IRPClass getARModelPortInterface(IRPPort port){
		IRPCollection dependencyCollection;
		int			  dependencyTotal;
		IRPClass 	  interfaceType = null;
		
		dependencyCollection = port.getDependencies();
		dependencyTotal      = dependencyCollection.getCount();
		
		for (int next = 1; next <= dependencyTotal; next ++){
			IRPModelElement item = (IRPModelElement) dependencyCollection.getItem(next);
			if (item instanceof IRPDependency){
				IRPDependency dependency = (IRPDependency) item;
				interfaceType = (IRPClass) dependency.getDependsOn();
			}
		}
		return interfaceType;
	}
	
	private String formatFlowBlockName(String name){
		Pattern	pattern = Pattern.compile("(?:if)_(\\w+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		
		matcher = pattern.matcher(name);
		if (matcher.matches()){
			return matcher.group(1);
		}
		return name;
	}
	
	private IRPClass createSysMLSignalFlowBlock(IRPClass atomicSwComponent, IRPPort port){
		IRPPackage    flowPackage;
		IRPClass	  flowBlock = null;
		IRPClass	  interfaceType = null;
		String        flowBlockName;
		
		interfaceType = getARModelPortInterface(port);
		
		if (interfaceType != null){
			flowPackage = (IRPPackage) findOrCreateModelElementRecursive(getActiveProject(), atomicSwComponent.getName() + "_" + PACKAGE_FLOW_NAME, 
					META_TYPE_PACKAGE);
			
			flowBlockName = formatFlowBlockName(interfaceType.getName());
			flowBlock = (IRPClass) findOrCreateModelElementRecursive(flowPackage, flowBlockName, META_TYPE_CLASS, new String[]{"Block"});
		
			createSysMLFlowBlockValueAttribute(interfaceType, flowBlock);
		}
		
		//log("Type: " + dependency.getDependsOn().getName());
		return flowBlock;
	}
	
	private void createSysMLBlockFlowProperties(IRPClass atomicSwComponent, IRPClass blockElement, IRPPort port, IRPClass itemFlowType){
		String 	portName;
		
		portName = formatPortName(port.getName());
		
		/* Create the flow property */
		IRPAttribute flowProperty = (IRPAttribute) findOrCreateModelElementRecursive(blockElement, portName, 
				"Attribute", new String[]{"flowProperty"});
		
		flowProperty.setType(itemFlowType);
		
		if (checkStereoTypeIsDataSenderPort(port)){
			setModelElementTagVaue(flowProperty, "direction", "Out");
		}
		else if (checkStereoTypeIsDataReceiverPort(port)){
			setModelElementTagVaue(flowProperty, "direction", "In");
		}
	}
	
	private void createSysMLBlockFullPorts(IRPClass atomicSwComponent, IRPClass blockElement, IRPPort port, IRPClass itemFlowType){
		
		String portName;
		
		portName = formatPortName(port.getName());
		
		/* Create the full port */
		IRPPort fullPort = (IRPPort) findOrCreateModelElementRecursive(blockElement, portName, "Port", new String[]{"fullPort"});
		fullPort.setContract(itemFlowType);
	}
	
	private IRPClass createSysMLBlock(IRPClass atomicSwComponent){
		IRPClass blockElement;
		Pattern  pattern = Pattern.compile("Swc(\\w+)");
		
		/* Find the SysML Block with the full name */
		blockElement = (IRPClass) findSysMLBlock(atomicSwComponent.getName());
		
		/* Find the SysML Block again with the removing the Swc prefix" */
		if (blockElement == null){
			Matcher matcher = pattern.matcher(atomicSwComponent.getName());
			if (matcher.matches()){
				blockElement = (IRPClass) findSysMLBlock(matcher.group(1));
			}
		}
		
		if (blockElement == null){
			IRPPackage rootPackage = (IRPPackage) findOrCreateModelElementRecursive(getActiveProject(), atomicSwComponent.getName() + "_Package", META_TYPE_PACKAGE);
			blockElement = (IRPClass) findOrCreateModelElementRecursive(rootPackage, atomicSwComponent.getName(), 
					META_TYPE_CLASS);
			addStereotypes(blockElement, new String[]{"Block"});
		}
		
		if (blockElement != null){	
			convertARModelSenderReceiverPorts(atomicSwComponent, blockElement);
			convertARModelEvents(atomicSwComponent, blockElement);
		}
		
		logger.severe(String.format("Block <%s> is updated", atomicSwComponent.getName()));
		
		return blockElement;
	}
	
	@Override
	public void execute(IRPModelElement arPackage){
		IRPCollection atomicSwComponentCollection;
		int		      atomicSwComponentTotal;
		
		if (checkSysMLProfileExist() == true){
		
			atomicSwComponentCollection = arPackage.getNestedElementsByMetaClass(META_TYPE_CLASS, 1);
			atomicSwComponentTotal = atomicSwComponentCollection.getCount();
			
			for (int next = 1; next <= atomicSwComponentTotal; next ++){
				Object item = atomicSwComponentCollection.getItem(next);
				if (item instanceof IRPClass){
					IRPClass element = (IRPClass) item;
					if (checkStereoTypeIsAtomicComponentType(element)){
						IRPClass blockElement = createSysMLBlock(element);
						logger.info(String.format("%s is updated to %s.", element.getFullPathName(), blockElement.getFullPathName()));
					}
				}
			}
		}
		else {
			throw new RhapsodyRuntimeException("SysML has not been added to this project.\n");
		}
	}
}
