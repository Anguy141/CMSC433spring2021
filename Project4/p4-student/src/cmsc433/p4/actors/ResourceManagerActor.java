package cmsc433.p4.actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import cmsc433.p4.enums.*;
import cmsc433.p4.messages.*;
import cmsc433.p4.util.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.AbstractActor;

public class ResourceManagerActor extends AbstractActor {

	private ActorRef logger; // Actor to send logging messages to

	/**
	 * Props structure-generator for this class.
	 * 
	 * @return Props structure
	 */
	static Props props(ActorRef logger) {
		return Props.create(ResourceManagerActor.class, logger);
	}

	/**
	 * Factory method for creating resource managers
	 * 
	 * @param logger Actor to send logging messages to
	 * @param system Actor system in which manager will execute
	 * @return Reference to new manager
	 */
	public static ActorRef makeResourceManager(ActorRef logger, ActorSystem system) {
		ActorRef newManager = system.actorOf(props(logger));
		return newManager;
	}

	/**
	 * Sends a message to the Logger Actor
	 * 
	 * @param msg The message to be sent to the logger
	 */
	public void log(LogMsg msg) {
		logger.tell(msg, getSelf());
	}

	/**
	 * Constructor
	 * 
	 * @param logger Actor to send logging messages to
	 */
	private ResourceManagerActor(ActorRef logger) {
		super();
		this.logger = logger;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Object.class, this::onReceive).build();
	}

	// You may want to add data structures for managing local resources and users,
	// storing
	// remote managers, etc.
	//
	// REMEMBER: YOU ARE NOT ALLOWED TO CREATE MUTABLE DATA STRUCTURES THAT ARE
	// SHARED BY
	// MULTIPLE ACTORS!
	private Map<String, Resource> localResources = new HashMap<String, Resource>();
	private Set<ActorRef> localUsers = new HashSet<ActorRef>();
	private Set<ActorRef> remoteManagers = new HashSet<ActorRef>();
	private Map<String, List<ManagementRequestMsg>> resourcesPendingDisablement = new HashMap<String, List<ManagementRequestMsg>>();
	private Map<String, List<ActorRef>> readResourceOwner = new HashMap<String, List<ActorRef>>();
	private Map<String, List<ActorRef>> writeResourceOwner = new HashMap<String, List<ActorRef>>();
	private Map<String, ActorRef> nonLocalResource = new HashMap<String, ActorRef>();
	private Map<String, Queue<AccessRequestMsg>> resourcesPendingRequests = new HashMap<String, Queue<AccessRequestMsg>>();
	private Set<ActorRef> checkedRemoteManagers = new HashSet<ActorRef>();
	/*
	 * (non-Javadoc)
	 * 
	 * You must provide an implementation of the onReceive() method below.
	 * 
	 * @see akka.actor.AbstractActor#createReceive
	 */

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof AddInitialLocalResourcesRequestMsg) {
			AddInitialLocalResourcesRequestMsg payload = (AddInitialLocalResourcesRequestMsg) msg;
			for (Resource r : payload.getLocalResources()) {

				log(LogMsg.makeLocalResourceCreatedLogMsg(getSelf(), r.getName()));
				log(LogMsg.makeResourceStatusChangedLogMsg(getSelf(), r.getName(), ResourceStatus.ENABLED));

				r.enable();
				localResources.put(r.getName(), r);
			}
			AddInitialLocalResourcesResponseMsg response = new AddInitialLocalResourcesResponseMsg(payload);
			getSender().tell(response, getSelf());
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof WhoHasResourceRequestMsg) {
			WhoHasResourceRequestMsg payload = (WhoHasResourceRequestMsg) msg;
			AccessRequestMsg reqMsg = payload.getAccessRequestMsg();
			AccessReleaseMsg relMsg = payload.getAccessReleaseMsg();
			ManagementRequestMsg manMsg = payload.getManagementRequestMsg();
			String resourceName = payload.getResourceName();
			boolean result = false;

			if (localResources.containsKey(resourceName)) {
				result = true;
			}
			WhoHasResourceResponseMsg response = new WhoHasResourceResponseMsg(payload, result, getSelf(), reqMsg,
					relMsg, manMsg);
			getSender().tell(response, getSelf());
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof WhoHasResourceResponseMsg) {
			WhoHasResourceResponseMsg payload = (WhoHasResourceResponseMsg) msg;
			AccessRequestMsg reqMsg = payload.getAccessRequestMsg();
			AccessReleaseMsg relMsg = payload.getAccessReleaseMsg();
			ManagementRequestMsg manMsg = payload.getManagementRequestMsg();
			String resourceName = payload.getResourceName();
			boolean result = payload.getResult();
			checkedRemoteManagers.add(payload.getSender());
			//System.out.println(remoteManagers.size());

			if (result == true) {
				if (nonLocalResource.get(resourceName) == null) {
					log(LogMsg.makeRemoteResourceDiscoveredLogMsg(getSelf(), payload.getSender(), resourceName));
				}
				nonLocalResource.put(resourceName, payload.getSender());
			}

			if (checkedRemoteManagers.size() >= remoteManagers.size()) {
				ActorRef owner = nonLocalResource.get(resourceName);
				if (owner == null) {
					if (reqMsg != null) {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(reqMsg,
								AccessRequestDenialReason.RESOURCE_NOT_FOUND);
						log(LogMsg.makeAccessRequestDeniedLogMsg(reqMsg.getReplyTo(), getSelf(), reqMsg.getAccessRequest(), AccessRequestDenialReason.RESOURCE_NOT_FOUND));
						reqMsg.getReplyTo().tell(response, getSelf());
					} else if (manMsg != null) {
						ManagementRequestDeniedMsg response = new ManagementRequestDeniedMsg(manMsg,
								ManagementRequestDenialReason.RESOURCE_NOT_FOUND);
						log(LogMsg.makeManagementRequestDeniedLogMsg(manMsg.getReplyTo(), getSelf(), manMsg.getRequest(), ManagementRequestDenialReason.RESOURCE_NOT_FOUND));
						manMsg.getReplyTo().tell(response, getSelf());
					}else if (relMsg != null) {
						log(LogMsg.makeAccessReleaseIgnoredLogMsg(relMsg.getSender(), getSelf(), relMsg.getAccessRelease()));
					}
				} else {
					if (reqMsg != null) {
						log(LogMsg.makeAccessRequestForwardedLogMsg(getSelf(), owner, reqMsg.getAccessRequest()));
						owner.forward(reqMsg, getContext());
					} else if (relMsg != null) {
						log(LogMsg.makeAccessReleaseForwardedLogMsg(getSelf(), owner, relMsg.getAccessRelease()));
						owner.forward(relMsg, getContext());
					} else if (manMsg != null) {
						log(LogMsg.makeManagementRequestForwardedLogMsg(getSelf(), owner, manMsg.getRequest()));
						owner.forward(manMsg, getContext());
					}
				}
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof AddLocalUsersRequestMsg) {
			AddLocalUsersRequestMsg payload = (AddLocalUsersRequestMsg) msg;
			for (ActorRef a : payload.getLocalUsers()) {
				localUsers.add(a);
			}
			AddLocalUsersResponseMsg response = new AddLocalUsersResponseMsg(payload);
			getSender().tell(response, getSelf());
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof AddRemoteManagersRequestMsg) {
			AddRemoteManagersRequestMsg payload = (AddRemoteManagersRequestMsg) msg;
			for (ActorRef a : payload.getManagerList()) {
				if (!a.equals(getSelf())) {
					remoteManagers.add(a);
				}
			}
			AddRemoteManagersResponseMsg response = new AddRemoteManagersResponseMsg(payload);
			getSender().tell(response, getSelf());
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof ManagementRequestMsg) {
			ManagementRequestMsg payload = (ManagementRequestMsg) msg;
			ManagementRequest request = payload.getRequest();
			ActorRef replyTo = payload.getReplyTo();
			String resourceName = request.getResourceName();
			ManagementRequestType mrType = request.getType();

			log(LogMsg.makeManagementRequestReceivedLogMsg(replyTo, getSelf(), request));

			if (localResources.containsKey(resourceName)) {
				Resource localResource = localResources.get(resourceName);
				if (mrType == ManagementRequestType.ENABLE) {

					localResource.enable();

					log(LogMsg.makeResourceStatusChangedLogMsg(getSelf(), localResource.getName(),
							ResourceStatus.ENABLED));

					log(LogMsg.makeManagementRequestGrantedLogMsg(replyTo, getSelf(), request));

					ManagementRequestGrantedMsg response = new ManagementRequestGrantedMsg(request);
					replyTo.tell(response, getSelf());


				} else if (mrType == ManagementRequestType.DISABLE) {
					if (isUserAReadOwner(resourceName, replyTo) || isUserWriteOwner(resourceName, replyTo)) {
						ManagementRequestDeniedMsg response = new ManagementRequestDeniedMsg(request,
								ManagementRequestDenialReason.ACCESS_HELD_BY_USER);

						log(LogMsg.makeManagementRequestDeniedLogMsg(replyTo, getSelf(), request,
								ManagementRequestDenialReason.ACCESS_HELD_BY_USER));

						replyTo.tell(response, getSelf());

					} else if ((readResourceOwner.get(resourceName) == null
							|| readResourceOwner.get(resourceName).isEmpty())
							&& (writeResourceOwner.get(resourceName) == null) || writeResourceOwner.isEmpty()) {

						localResource.disable();

						log(LogMsg.makeResourceStatusChangedLogMsg(getSelf(), localResource.getName(),
								ResourceStatus.DISABLED));

						log(LogMsg.makeManagementRequestGrantedLogMsg(replyTo, getSelf(), request));

						ManagementRequestGrantedMsg response = new ManagementRequestGrantedMsg(request);
						replyTo.tell(response, getSelf());

						processresourcesPendingRequests(resourceName);
					} else {
						List<ManagementRequestMsg> allPayloads = resourcesPendingDisablement.get(resourceName);
						if (allPayloads == null) {
							allPayloads = new ArrayList<ManagementRequestMsg>();
						}
						allPayloads.add(payload);
						resourcesPendingDisablement.put(resourceName, allPayloads);
						processresourcesPendingRequests(resourceName);
					}
				}
			} else {
				if (nonLocalResource.containsKey(resourceName)) {
					ActorRef owner = nonLocalResource.get(resourceName);
					log(LogMsg.makeManagementRequestForwardedLogMsg(getSelf(), owner, payload.getRequest()));

					owner.forward(payload, getContext());

				} else {
					WhoHasResourceRequestMsg whoHasMsg = new WhoHasResourceRequestMsg(resourceName, null, null,
							payload);
					for (ActorRef curr : remoteManagers) {
						curr.tell(whoHasMsg, getSelf());
					}
				}
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof AccessReleaseMsg) {
			AccessReleaseMsg payload = (AccessReleaseMsg) msg;
			AccessRelease accessRelease = payload.getAccessRelease();
			ActorRef sender = payload.getSender();
			String resourceName = accessRelease.getResourceName();
			AccessType accessType = accessRelease.getType();

			log(LogMsg.makeAccessReleaseReceivedLogMsg(sender, getSelf(), accessRelease));

			if (localResources.containsKey(resourceName)) {
				if (accessType == AccessType.CONCURRENT_READ) {
					if (readResourceOwner.containsKey(resourceName)) {
						int size = readResourceOwner.get(resourceName).size();
						List<ActorRef> lst = readResourceOwner.get(resourceName);
						for (int i = 0; i < size; i++) {
							if(lst.get(i).equals(sender)) {
								lst.remove(i);
								log(LogMsg.makeAccessReleasedLogMsg(sender, getSelf(), accessRelease));
								break;
							}
						}

					} else {
						log(LogMsg.makeAccessReleaseIgnoredLogMsg(sender, getSelf(), accessRelease));
					}
				} else if (accessType == AccessType.EXCLUSIVE_WRITE) {
					if (writeResourceOwner.containsKey(resourceName)) {
						int size = writeResourceOwner.get(resourceName).size();
						List<ActorRef> lst = writeResourceOwner.get(resourceName);
						for (int i = 0; i < size; i++) {
							if(lst.get(i).equals(sender)) {
								lst.remove(i);
								log(LogMsg.makeAccessReleasedLogMsg(sender, getSelf(), accessRelease));
								break;
							}
						}
					} else {
						log(LogMsg.makeAccessReleaseIgnoredLogMsg(sender, getSelf(), accessRelease));
					}
				}

				if (!readResourceOwner.containsKey(resourceName) && !writeResourceOwner.containsKey(resourceName)
						&& resourcesPendingDisablement.containsKey(resourceName)) {

					localResources.get(resourceName).disable();

					log(LogMsg.makeResourceStatusChangedLogMsg(getSelf(), resourceName, ResourceStatus.DISABLED));

					List<ManagementRequestMsg> allPayloads = resourcesPendingDisablement.get(resourceName);

					for (ManagementRequestMsg curr : allPayloads) {
						ManagementRequestGrantedMsg response = new ManagementRequestGrantedMsg(curr.getRequest());
						ActorRef replyTo = curr.getReplyTo();
						replyTo.tell(response, getSelf());
					}

					resourcesPendingDisablement.remove(resourceName);
				}
				processresourcesPendingRequests(resourceName);

			} else {

				if (nonLocalResource.containsKey(resourceName)) {
					ActorRef owner = nonLocalResource.get(resourceName);
					log(LogMsg.makeAccessReleaseForwardedLogMsg(getSelf(), owner, payload.getAccessRelease()));

					owner.forward(payload, getContext());
				} else {
					WhoHasResourceRequestMsg whoHasMsg = new WhoHasResourceRequestMsg(resourceName, null, payload,
							null);
					for (ActorRef curr : remoteManagers) {
						curr.tell(whoHasMsg, getSelf());
					}
				}
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if (msg instanceof AccessRequestMsg) {
			AccessRequestMsg payload = (AccessRequestMsg) msg;
			AccessRequest request = payload.getAccessRequest();
			AccessRequestType accessType = request.getType();
			ActorRef replyTo = payload.getReplyTo();
			String resourceName = request.getResourceName();

			log(LogMsg.makeAccessRequestReceivedLogMsg(replyTo, getSelf(), request));

			if (localResources.containsKey(resourceName)) {

				///////////////////////////////////////////////////////////////////////////////////////////////
				if (accessType == AccessRequestType.EXCLUSIVE_WRITE_BLOCKING) {
					if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
							&& ((isUserSoleReadOwner(resourceName, replyTo)
									|| readResourceOwner.get(resourceName) == null)
									&& (isUserWriteOwner(resourceName, replyTo)
											|| writeResourceOwner.get(resourceName) == null))
							&& !resourcesPendingDisablement.containsKey(resourceName)) {

						List<ActorRef> allUsers = writeResourceOwner.get(resourceName);

						if (allUsers == null) {
							allUsers = new ArrayList<ActorRef>();
						}

						allUsers.add(replyTo);
						writeResourceOwner.put(resourceName, allUsers);

						AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);

						log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

						replyTo.tell(response, getSelf());
					} else if (localResources.get(resourceName).getStatus() == ResourceStatus.DISABLED
							|| (resourcesPendingDisablement.containsKey(resourceName))) {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_DISABLED);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_DISABLED));

						replyTo.tell(response, getSelf());
					} else {
						Queue<AccessRequestMsg> pr = resourcesPendingRequests.get(resourceName);
						if (pr == null) {
							pr = new LinkedList<AccessRequestMsg>();
						}
						pr.add(payload);
						resourcesPendingRequests.put(resourceName, pr);
					}
					////////////////////////////////////////////////////////////////////////////////////////////////////////
				} else if (accessType == AccessRequestType.CONCURRENT_READ_BLOCKING) {
					if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
							&& (isUserWriteOwner(resourceName, replyTo) || writeResourceOwner.get(resourceName) == null)
							&& !resourcesPendingDisablement.containsKey(resourceName)) {

						List<ActorRef> allUsers = readResourceOwner.get(resourceName);

						if (allUsers == null) {
							allUsers = new ArrayList<ActorRef>();
						}
						allUsers.add(replyTo);
						readResourceOwner.put(resourceName, allUsers);

						AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);

						log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

						replyTo.tell(response, getSelf());
					} else if (localResources.get(resourceName).getStatus() == ResourceStatus.DISABLED
							|| (resourcesPendingDisablement.containsKey(resourceName))) {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_DISABLED);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_DISABLED));

						replyTo.tell(response, getSelf());
					} else {
						Queue<AccessRequestMsg> pr = resourcesPendingRequests.get(resourceName);
						if (pr == null) {
							pr = new LinkedList<AccessRequestMsg>();
						}
						pr.add(payload);
						resourcesPendingRequests.put(resourceName, pr);
					}
					////////////////////////////////////////////////////////////////////////////////////////////////////////
				} else if (accessType == AccessRequestType.CONCURRENT_READ_NONBLOCKING) {

					if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
							&& (isUserWriteOwner(resourceName, replyTo) || writeResourceOwner.get(resourceName) == null)
							&& !resourcesPendingDisablement.containsKey(resourceName)) {
						List<ActorRef> allUsers = readResourceOwner.get(resourceName);

						if (allUsers == null) {
							allUsers = new ArrayList<ActorRef>();
						}
						allUsers.add(replyTo);
						readResourceOwner.put(resourceName, allUsers);

						AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);

						log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

						replyTo.tell(response, getSelf());
					} else if (localResources.get(resourceName).getStatus() == ResourceStatus.DISABLED
							|| (resourcesPendingDisablement.containsKey(resourceName))) {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_DISABLED);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_DISABLED));

						replyTo.tell(response, getSelf());
					} else {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_BUSY);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_BUSY));

						replyTo.tell(response, getSelf());
					}

					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
				} else if (accessType == AccessRequestType.EXCLUSIVE_WRITE_NONBLOCKING) {
					if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
							&& ((isUserSoleReadOwner(resourceName, replyTo)
									|| readResourceOwner.get(resourceName) == null)
									&& (isUserWriteOwner(resourceName, replyTo)
											|| writeResourceOwner.get(resourceName) == null))
							&& !resourcesPendingDisablement.containsKey(resourceName)) {

						List<ActorRef> allUsers = writeResourceOwner.get(resourceName);

						if (allUsers == null) {
							allUsers = new ArrayList<ActorRef>();
						}

						allUsers.add(replyTo);
						writeResourceOwner.put(resourceName, allUsers);

						AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);

						log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

						replyTo.tell(response, getSelf());
					} else if (localResources.get(resourceName).getStatus() == ResourceStatus.DISABLED
							|| (resourcesPendingDisablement.containsKey(resourceName))) {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_DISABLED);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_DISABLED));

						replyTo.tell(response, getSelf());
					} else {
						AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(payload,
								AccessRequestDenialReason.RESOURCE_BUSY);

						log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
								AccessRequestDenialReason.RESOURCE_BUSY));

						replyTo.tell(response, getSelf());
					}
				}
			} else {
				if (nonLocalResource.containsKey(resourceName)) {
					ActorRef owner = nonLocalResource.get(resourceName);
					log(LogMsg.makeAccessRequestForwardedLogMsg(getSelf(), owner, payload.getAccessRequest()));

					owner.forward(payload, getContext());

				} else {
					WhoHasResourceRequestMsg whoHasMsg = new WhoHasResourceRequestMsg(resourceName, payload, null,
							null);
					for (ActorRef curr : remoteManagers) {
						curr.tell(whoHasMsg, getSelf());
					}
				}
			}
		}
	}

	private boolean isUserSoleReadOwner(String resourceName, ActorRef user) {
		if (readResourceOwner.containsKey(resourceName)) {
			int size = readResourceOwner.get(resourceName).size();
			int cnt = 0;
			for (ActorRef curr : readResourceOwner.get(resourceName)) {
				if (curr.equals(user)) {
					cnt++;
				}
			}
			return (cnt == size) ? true : false;
		}
		return false;
	}

	private boolean isUserAReadOwner(String resourceName, ActorRef user) {
		if (readResourceOwner.containsKey(resourceName)) {
			if (readResourceOwner.get(resourceName).contains(user)) {
				return true;
			}
		}
		return false;
	}

	private boolean isUserWriteOwner(String resourceName, ActorRef user) {
		if (writeResourceOwner.containsKey(resourceName)) {
			int size = writeResourceOwner.get(resourceName).size();
			int cnt = 0;
			for (ActorRef curr : writeResourceOwner.get(resourceName)) {
				if (curr.equals(user)) {
					cnt++;
				}
			}
			return (cnt == size) ? true : false;
		}
		return false;

	}

	private void processresourcesPendingRequests(String resourceName) {
		Queue<AccessRequestMsg> pr = resourcesPendingRequests.get(resourceName);
		if (pr == null) {
			return;
		}
		while (!pr.isEmpty()) {
			AccessRequestMsg payload = pr.peek();
			AccessRequest request = payload.getAccessRequest();
			ActorRef replyTo = payload.getReplyTo();
			AccessRequestType accessType = request.getType();

			if (localResources.containsKey(resourceName)) {
				if ((accessType == AccessRequestType.EXCLUSIVE_WRITE_BLOCKING
						|| accessType == AccessRequestType.CONCURRENT_READ_BLOCKING)
						&& localResources.get(resourceName).getStatus() == ResourceStatus.DISABLED
						|| (resourcesPendingDisablement.containsKey(resourceName))) {
					AccessRequestDeniedMsg response = new AccessRequestDeniedMsg(request,
							AccessRequestDenialReason.RESOURCE_DISABLED);

					log(LogMsg.makeAccessRequestDeniedLogMsg(replyTo, getSelf(), request,
							AccessRequestDenialReason.RESOURCE_DISABLED));

					replyTo.tell(response, getSelf());
				} else {
					if (accessType == AccessRequestType.CONCURRENT_READ_BLOCKING) {
						if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
								&& (isUserWriteOwner(resourceName, replyTo) || writeResourceOwner.get(resourceName) == null)
								&& !resourcesPendingDisablement.containsKey(resourceName)) {

							List<ActorRef> allUsers = readResourceOwner.get(resourceName);
							if (allUsers == null) {
								allUsers = new ArrayList<ActorRef>();
							}
							allUsers.add(replyTo);
							readResourceOwner.put(resourceName, allUsers);

							AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);
							log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

							pr.remove();

							replyTo.tell(response, getSelf());
						} else {
							return;
						}
					} else if (accessType == AccessRequestType.EXCLUSIVE_WRITE_BLOCKING) {
						if (localResources.get(resourceName).getStatus() == ResourceStatus.ENABLED
								&& ((isUserSoleReadOwner(resourceName, replyTo)
										|| readResourceOwner.get(resourceName) == null)
										&& (isUserWriteOwner(resourceName, replyTo)
												|| writeResourceOwner.get(resourceName) == null))
								&& !resourcesPendingDisablement.containsKey(resourceName)) {

							List<ActorRef> allUsers = writeResourceOwner.get(resourceName);

							if (allUsers == null) {
								allUsers = new ArrayList<ActorRef>();
							}

							allUsers.add(replyTo);
							writeResourceOwner.put(resourceName, allUsers);

							AccessRequestGrantedMsg response = new AccessRequestGrantedMsg(request);

							log(LogMsg.makeAccessRequestGrantedLogMsg(replyTo, getSelf(), request));

							pr.remove();

							replyTo.tell(response, getSelf());
						} else {
							return;
						}
					}
				}
			}
		}
	}
}
