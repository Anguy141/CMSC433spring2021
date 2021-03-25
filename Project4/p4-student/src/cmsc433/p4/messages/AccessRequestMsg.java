package cmsc433.p4.messages;

import cmsc433.p4.util.AccessRequest;
import akka.actor.ActorRef;

/**
 * Class of messages for requesting access to a resource.
 * 
 * @author Rance Cleaveland
 *
 */
public class AccessRequestMsg {
	
	private final AccessRequest request;
	private final ActorRef replyTo;
	
	public AccessRequestMsg (AccessRequest request, ActorRef user) {
		this.request = request;
		this.replyTo = user;
	}
	
	public AccessRequest getAccessRequest() {
		return request;
	}

	public ActorRef getReplyTo() {
		return replyTo;
	}
	
	@Override 
	public String toString () {
		return request.getType() + " request for " + request.getResourceName();
	}

}
