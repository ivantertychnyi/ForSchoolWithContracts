package tap.execounting.components;

import java.util.Date;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.services.DateService;

public class Freezer {
	// Bits
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private ContractMed contractMed;
	@Inject
	private ComponentResources resources;
	
	// Screen fields
	@Component
	private Zone zona;
	@Property
	private Date dateFreeze;
	@Property
	private Date dateUnfreeze;
	
	// Behavior fields
	@Property
	private boolean active;
	@Parameter(required=true)
	@Property
	private int contractId;
	
	void onPrepare(){
		dateFreeze = new Date();
		dateUnfreeze = DateService.datePlusMonths(dateFreeze, 6);
	}
	
	public void activate(){
		active = true;
	}
	
	public Zone getZone(){
		return zona;
	}
	
	void onAction(int contractId){
		System.out.println("\n\nsubmitn\n\n");
		CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>(); 
		resources.triggerEvent("SuccessfullFreeze", new Object[]{new Integer(contractId)}, callback);
	}
	void onSuccess(int contractId){
		contractMed.doFreeze(contractId, dateFreeze, dateUnfreeze);
		active = false;
		renderer.addRender(getZoneID(), zona);
		CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>(); 
		resources.triggerEvent("SuccessfullFreeze", new Object[]{contractId}, callback);
	}
	
	public String getZoneID(){
		return "freezr"+contractId;
	}
}
