/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noelportugal.amazonecho.hue;

/**
 * Caller simply calls HueBridgeLink.connect(), and wait for this callbacks
 * 
 * @author raymond
 */
public interface HueBridgeLinkCallback {
    // bridge is ready for controlling lights
    public void onBridgeReady();
    
    // failed to prepare Bridge
    // public void onBridgeFail();
}
