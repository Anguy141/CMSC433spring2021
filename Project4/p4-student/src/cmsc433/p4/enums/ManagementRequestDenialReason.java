package cmsc433.p4.enums;

/**
 * Enum type of reasons why management requests may be denied.
 * 
 * @author Rance Cleaveland
 *
 */
public enum ManagementRequestDenialReason {
	
	RESOURCE_NOT_FOUND, 	// Returned if the resource does not exist in the system
	ACCESS_HELD_BY_USER		// Returned if a user is attempting to disable a resource they currently hold access to.
}
