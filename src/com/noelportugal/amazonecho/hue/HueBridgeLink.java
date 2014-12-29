package com.noelportugal.amazonecho.hue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import java.util.List;

/**
 * Deals with the Hue Bridge discovery, authentication, link, and connection
 * 
 * @author Raymond Xie
 * 
 */
public class HueBridgeLink 
{
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    private HueBridgeLinkCallback caller;
    
    public static void main(String args[]) {
        HueBridgeLink link = new HueBridgeLink();
        link.connect(null); // supply the "caller" here
    }

    public HueBridgeLink() {
        phHueSDK = PHHueSDK.create();
        // listener for HueBridge events
        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }
    
    public void connect( HueBridgeLinkCallback callback ) {
        caller = callback;
        
        phHueSDK=PHHueSDK.getInstance();
        
        // Load in HueProperties, if first time use a properties file is created.
        HueProperties.loadProperties();  
        String lastIpAddress = HueProperties.getLastConnectedIP();
        String lastUsername = HueProperties.getUsername();
        
        if( lastIpAddress == null || "".equals(lastIpAddress) ) {
            // first time, call setup
            setup();
        }
        else {
            // try to connect to last saved access point
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            // try to connect now, waiting for "onBridgeConnected()" callback
            phHueSDK.connect(lastAccessPoint);
        }
    }
    
    public void setup() {
        phHueSDK=PHHueSDK.getInstance();

        // Load in HueProperties, if first time use a properties file is created.
        HueProperties.loadProperties();  
        HueProperties.storeUsername("ExchangeHueLights");

        // go and find bridges
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
        System.out.println("Trying to find a Hue bridge on the network");
    }

    
    // listener of PhHue SDK internals 
    private final PHSDKListener listener = new PHSDKListener() {

        // Event in response to the "sm.search()"
        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            //RXIE: got back a list of HueBridge AccessPoint, just pick the first one
              String username = HueProperties.getUsername();
              HueProperties.storeUsername(username);

              if( accessPointsList == null || accessPointsList.size() == 0 ) {
                  System.err.println("Error: No Hue Bridge Access Point is found.");
                  return;
              }
              
              // just pick the first Hue bridge found
              PHAccessPoint accessPoint = accessPointsList.get(0);
              
              phHueSDK = PHHueSDK.getInstance();
              accessPoint.setUsername(username);
              
              // try to connect to the access point, and listener for event
              phHueSDK.connect(accessPoint);            
              System.out.println("Connecting to bridge: " + accessPoint.getMacAddress());
        }

        // Event in response to "connect" if not previously linked
        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            // Start the Pushlink Authentication.
            phHueSDK.startPushlinkAuthentication(accessPoint);
            System.out.println("Push Hue Bridge button for authentication now... Timeout in 30 seconds!!!");
        }

        // Event in response to "connect" if was preiousely linked
        @Override
        public void onBridgeConnected(PHBridge bridge) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);

            String username = HueProperties.getUsername();
            String lastIpAddress =  bridge.getResourceCache().getBridgeConfiguration().getIpAddress();   
            
            System.out.println("On connected: IP=" + lastIpAddress);
            
            HueProperties.storeUsername(username);
            HueProperties.storeLastIPAddress(lastIpAddress);
            HueProperties.saveProperties();

            //RXIE: ready for light control from now on....
            System.out.println("All set - start to accept lighting control now...");
            
            if( caller != null ) {
                caller.onBridgeReady();
            }
        }

        @Override
        public void onCacheUpdated(int arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {

            if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                System.err.println("Error: BRIDGE_NOT_RESPONDING - " + message);
                
                //RXIE: clean out saved bridge ip address to force searching and authenticating again.
                HueProperties.storeLastIPAddress("");
                HueProperties.saveProperties();
                
                System.out.println("Cleaned out last saved bridge. Retry to establish new connection");
                
            }
            else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                // pushLinkDialog.incrementProgress();
                System.err.println("Warning: PUSHLINK_BUTTON_NOT_PRESSED - press the bridge button to authenticate" );
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                System.err.println("Error: PUSHLINK_AUTHENTICATION_FAILED" );
            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                System.err.println("Error: BRIDGE_NOT_FOUND - " + message);
            }
        } 
    };
    
}