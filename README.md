AmazonEchoHomeAutomation
========================

Implementation of the Unnoficial Amazon Echo API
https://github.com/noelportugal/AmazonEchoApi

```
    public static void main(String[] args) throws IOException, InterruptedException {
        
        amazonEchoApi = new AmazonEchoApi("https://pitangui.amazon.com","USERNAME", "PASSWORD");
        amazonEchoApi.httpLogin();
        hueLights = new HueLights();
        String smartThingsUrl = "https://graph.api.smartthings.com//api/smartapps/installations/XXXXXXXXX/switches/XXXXXXXXXX";
        smartThings = new SmartThings("CLIENT_ID","API_KEY","BEARER");
        witAi = new WitAi("BEARER");


        
        while(true){
        
            String command = amazonEchoApi.getLatestTodo();;
            if (command != null){
                System.out.println(command);
                WitEntity witEntity = witAi.getEntity(command);
                String subject = witEntity.getSubject();
                String state = witEntity.getState(); 
                System.out.println("Subject: " + subject + ". State: " + state);
                
                if (subject != null && state != null){
                    if (subject.contains("tree")){
                        String body = "{command: " + state + "}";
                        smartThings.setApp(smartThingsUrl, body);
                    }else if (subject.contains("lights")){
                        hueLights.setState(state);
                    }  
                }
                
            }else{
                System.out.println("No new commands");
            }
                           
            Thread.sleep(5000);
        }  
        
    }    
    
```
