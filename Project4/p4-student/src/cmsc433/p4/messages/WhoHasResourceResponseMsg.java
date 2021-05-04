package cmsc433.p4.messages;

import akka.actor.ActorRef;

public class WhoHasResourceResponseMsg {
	private final String resource_name;
	private final boolean result;
	private final AccessRequestMsg reqMsg;
	private final AccessReleaseMsg relMsg;
	private final ManagementRequestMsg manMsg;
	private final ActorRef sender; // The actor who sends this response message.
	
	public WhoHasResourceResponseMsg (String resource_name, boolean result, ActorRef sender, AccessRequestMsg reqMsg, AccessReleaseMsg relMsg, ManagementRequestMsg manMsg) {
		this.resource_name = resource_name;
		this.result = result;
		this.sender = sender;
		this.reqMsg = reqMsg;
		this.relMsg = relMsg;
		this.manMsg = manMsg;
	}
	
	public WhoHasResourceResponseMsg (WhoHasResourceRequestMsg request, boolean result, ActorRef sender, AccessRequestMsg reqMsg, AccessReleaseMsg relMsg, ManagementRequestMsg manMsg) {
		this.resource_name = request.getResourceName();
		this.result = result;
		this.sender = sender;
		this.reqMsg = reqMsg;
		this.relMsg = relMsg;
		this.manMsg = manMsg;
	}
	
	public String getResourceName () {
		return resource_name;
	}
	
	public boolean getResult () {
		return result;
	}
	
	public ActorRef getSender () {
		return sender;
	}
	
	public AccessRequestMsg getAccessRequestMsg() {
		return reqMsg;
	}
	
	public AccessReleaseMsg getAccessReleaseMsg() {
		return relMsg;
	}
	
	public ManagementRequestMsg getManagementRequestMsg() {
		return manMsg;
	}
	
	@Override public String toString () {
		return "I" + (result ? " have " : " do not have ") + resource_name;
	}
}
