package cmsc433.p4.messages;

public class WhoHasResourceRequestMsg {	
	private final String resource_name;
	private final AccessRequestMsg reqMsg;
	private final AccessReleaseMsg relMsg;
	private final ManagementRequestMsg manMsg;
	
	public WhoHasResourceRequestMsg (String resource, AccessRequestMsg reqMsg, AccessReleaseMsg relMsg, ManagementRequestMsg manMsg) {
		this.resource_name = resource;
		this.reqMsg = reqMsg;
		this.relMsg = relMsg;
		this.manMsg = manMsg;
	}
	
	public String getResourceName () {
		return resource_name;
	}
	
	public AccessRequestMsg getAccessRequestMsg() {
		return reqMsg;
	}
	
	public ManagementRequestMsg getManagementRequestMsg() {
		return manMsg;
	}
	
	public AccessReleaseMsg getAccessReleaseMsg() {
		return relMsg;
	}
	
	@Override 
	public String toString () {
		return "Who has " + resource_name + "?";
	}
}
