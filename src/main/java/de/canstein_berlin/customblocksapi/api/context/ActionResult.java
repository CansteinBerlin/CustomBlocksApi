package de.canstein_berlin.customblocksapi.api.context;

public enum ActionResult {

    SUCCESS, // The OnUse Event successfully is used. The Interaction will be cancelled;
    FAIL; //The Interaction event will not be cancelled


    ActionResult() {
    }

    public boolean isAccepted() {
        return this == SUCCESS;
    }

}
