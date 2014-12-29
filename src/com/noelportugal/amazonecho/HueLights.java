package com.noelportugal.amazonecho;

import com.noelportugal.amazonecho.hue.HueBridgeLink;
import com.noelportugal.amazonecho.hue.HueBridgeLinkCallback;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import java.util.List;
import java.util.Random;

public class HueLights implements HueBridgeLinkCallback {
    private static PHHueSDK phHueSDK;
    private static PHBridge bridge;
    private static PHLightState lightState;
    private static final int MAX_HUE = 65535;
    private static final int maxBrightness = 254;
    private static final int minBrightness = 15;
    
    public HueLights(){
        HueBridgeLink bridgeLink = new HueBridgeLink();
        bridgeLink.connect(HueLights.this);
        phHueSDK = PHHueSDK.getInstance();
    }

    @Override
    public void onBridgeReady() {
        bridge = phHueSDK.getSelectedBridge();
    }
    
    public void allOff(){
        lightState = new PHLightState();
        lightState.setOn(false);
        bridge.setLightStateForDefaultGroup(lightState);
    }
    
    public void allOn(){
        lightState = new PHLightState();
        lightState.setOn(true);
        lightState.setBrightness(maxBrightness);
        //lightState.setHue(MAX_HUE);
        bridge.setLightStateForDefaultGroup(lightState);
    }
    
    public void setState(String state){
        if (state.equals("on")){
            allOn();
        }else if (state.equals("off")){
            allOff();
        }
    }
    
    public void lightShow(){
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();

        int i =0;
        while (i++ < 6) {
            System.out.println("while loop: " + i);
            try {
                PHLightState lightState = new PHLightState();
                lightState.setOn(true);
                int randomColor = rand.nextInt(MAX_HUE);
                lightState.setBrightness(maxBrightness);
                lightState.setHue(randomColor);
                bridge.setLightStateForDefaultGroup(lightState);
                Thread.sleep(1000);
            } catch (Exception ex) {
                System.err.println("error:" + ex.getMessage());
            }
        }
        allOff();
    }
}