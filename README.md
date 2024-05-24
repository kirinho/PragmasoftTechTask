### Write REST API wrapper around the GraalJs javascript interpreter, allowing to:

1) Evaluate arbitrary javascript code passed as a plaintext request body for non blocking execution, collecting script's output (stdout) and error (stderr) streams while it's executing.
   1a) Optional extra complexity level, consider implementing this part after the rest is implemented - support extra request parameter for blocking execution and stream back in the response body script's console output and/or console error (in case of error) as a plaintext immediately while script writes its output.
2) review the list of scripts, their ids, execution statuses (successfully completed, executing, queued, etc..), other useful context info like scheduled time, execution time. Allow optional parameters for list filtering by status and list ordering by script id or scheduled time descending.
3) get the detailed script info, including script body and its console output / error till now.
4) forcibly stop any running or scheduled script to prevent hanging scripts from consuming system resources.
5) remove inactive (stopped, completed, failed) scripts from the list by their id.

Keep in mind that we cannot control the script's content - it can be broken, malicious, malformed, or contain infinite loops. We should provide to the script authors sufficiently detailed diagnostic information about script errors, if any.

Authentication is not required - API should be public for simplicity. Persistence is not required. It's also not required to write a client for this API.You can use postman and/or curl for testing. Unit tests are also welcome. Use maven or gradle for build.

Provide adequate code documentation and build / run instructions.

#### link with demonstration example: https://drive.google.com/file/d/1ZUdU6hdNhuR48-cA1F-Pf733LNEAAtYl/view?usp=sharing
![Screen1](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/1.png?raw=true)  
**method post to add script**
---
![Screen2](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/2.png?raw=true)  
**get method to show script by id, script is executing**
---
![Screen3](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/3.png?raw=true)  
**get method to show script by id, script is completed**
---
![Screen4](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/4.png?raw=true)  
**delete method to delete script by id**
---
![Screen5](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/5.png?raw=true)  
**get method to get all scripts**
---
![Screen6](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/6.png?raw=true)  
**post method to stop script by id**
---
![Screen7](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/7.png?raw=true)  
**get method to show stopped script**
---
![Screen8](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/8.png?raw=true)  
**post method with passed js code as a parameter that has an error**
---
![Screen9](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/9.png?raw=true)  
**get method to show script by id, script is completed**
---
![Screen10](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/MainControllerTest.png?raw=true)  
**Tests for MainController**
---
![Screen11](https://github.com/kirinho/PragmasoftTechTask/blob/main/images/ScriptServiceTest.png?raw=true)  
**Tests for ScriptService**
---