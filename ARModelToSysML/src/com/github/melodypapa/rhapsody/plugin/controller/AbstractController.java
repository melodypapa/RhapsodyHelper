package com.github.melodypapa.rhapsody.plugin.controller;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProfile;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStereotype;
import com.telelogic.rhapsody.core.IRPTag;

public abstract class AbstractController {
	
	public static final String META_TYPE_CLASS = "Class";
	public static final String META_TYPE_PACKAGE = "Package";
	public static final String META_TYPE_OPERATION = "Operation";
	public static final String META_TYPE_ARGUMENT = "Argument";
	public static final String META_TYPE_OBJECT = "Object";
	public static final String META_TYPE_SD = "SequenceDiagram";
	public static final String META_TYPE_CONTROLLED_FILE = "ControlledFile";
	public static final String META_TYPE_DEPENDENCY = "Dependency";
	public static final String META_TYPE_COMMENT = "Comment";
	public static final String META_TYPE_ATTRIBUTE = "Attribute";
	public static final String META_TYPE_TYPE = "Type";
	public static final String META_TYPE_PORT = "Port";

	protected static final Logger logger = LogManager.getLogManager().getLogger("");
	
	private IRPApplication application;
	
	public AbstractController(IRPApplication application) {
		this.application = application;
	}
	
	protected IRPApplication getRhapsodyApplication(){
		return this.application;
	}
	
	/**
	 * Get the current active project 
	 * @return the Rhapsody IRPProject object
	 */
	protected IRPProject getActiveProject(){
		return this.application.activeProject();
	}
	
	protected IRPProfile getProfile(String name){
		return (IRPProfile) getActiveProject().findNestedElementRecursive(name, "Profile");
	}
	
	/**
	 * Check current Model Element contains the specific stereotype or not
	 * @param element the Model Element
	 * @return
	 * 		true   : Model element contains the specific stereotype
	 * 		false: : Model element does not contain the specific stereotype
	 */
	protected boolean checkStereoType(IRPModelElement element, String stereoType){
		IRPCollection 	stereoTypeCollection;
		int				stereoTypeTotal;
		
		stereoTypeCollection = element.getStereotypes();
		stereoTypeTotal      = stereoTypeCollection.getCount();
		
		for (int next = 1; next <= stereoTypeTotal; next ++){
			Object item = stereoTypeCollection.getItem(next);
			if (item instanceof IRPStereotype){
				IRPStereotype stereotype = (IRPStereotype) item;
				if (stereotype.getName().equals(stereoType)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the stereotype by name
	 * @param name the name of stereotype
	 * @return The Rhapsody stereotype object
	 * 		
	 */
	protected IRPStereotype getStereotype(String name){
		return (IRPStereotype) getActiveProject().findNestedElementRecursive(name, "Stereotype");
	}
	
	protected void addStereotypes(IRPModelElement element, String[] typeNames){
		for (String stereotypeName: typeNames){
			IRPStereotype stereotype = getStereotype(stereotypeName);
			if (stereotype != null){
				element.addSpecificStereotype(stereotype);
			}
		}
	}
	
	protected IRPModelElement findOrCreateModelElementRecursive(IRPModelElement parent, String name, String metaType, String [] stereotypes){
		IRPModelElement element = parent.findNestedElementRecursive(name, metaType);
		
		if (element == null){
			element = parent.addNewAggr(metaType, name);
			addStereotypes(element, stereotypes);
		}
		return element;
	}
	
	protected IRPModelElement findOrCreateModelElementRecursive(IRPModelElement parent, String name, String metaType){
		return findOrCreateModelElementRecursive(parent, name, metaType, new String[0]);
	}
	
	protected void setModelElementTagVaue(IRPModelElement parent, String tagName, String tagValue){
		IRPTag tag = parent.getTag(tagName);
		if (tag != null){
			parent.setTagValue(tag, tagValue);
		}
	}
	
	/**
	 * Check SysML profile has been added or not in the active project. 
	 * @return
	 * 	true:  SysML profile has been added.
	 *  false: SysML profile has not been added.
	 */
	protected boolean checkSysMLProfileExist(){
		IRPCollection profileCollection;
		int		      profileTotal;
		
		profileCollection = getActiveProject().getProfiles();
		profileTotal      = profileCollection.getCount();
		
		for (int next = 1; next <= profileTotal; next ++){
			Object item = profileCollection.getItem(next);
			if (item instanceof IRPProfile){
				IRPProfile profile = (IRPProfile) item;
				if (profile.getName().equals("SysML")){
					return true;
				}
			}
		}
		
		return false;
	}
	
	abstract public void execute(IRPModelElement rootElement);

}
