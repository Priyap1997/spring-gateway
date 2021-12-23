package com.vmware.user.apigateway.model;

import java.io.Serializable;

/**
 * <code>StatusVO</code> is model class to give status of Gateway
 *
 * @author Kartik
 */

public class StatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;

    private String startTime;

    private String activeTime;

    /**
     * Gets the status of the application as UP or DOWN.
     *
     * @return Returns status of the Gateway Application.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the application as UP or DOWN.
     *
     * @param status Status of the Gateway Application as UP or DOWN.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the start time for the application.
     *
     * @return Returns start time for the gateway application.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time for the application.
     *
     * @param startTime Start time for the gateway application.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the active time in milliseconds for the gateway application.
     *
     * @return Returns active time for the gateway application in milliseconds.
     */
    public String getActiveTime() {
        return activeTime;
    }

    /**
     * Sets the active time in milliseconds for the gateway application.
     *
     * @param activeTime Active time for the gateway application in milliseconds.
     */
    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

}
