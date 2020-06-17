# AT-WebCrawler
Agent technologies course project

#### Authors
- @DamjanPantic
- @coperope
- @mihajlo-perendija

#### About 

##### Short description

This project has been developed as part of the course "Agent technologies" organized at Faculty of technical sciences.
The implementation consists of main backend application and frontend, angular-based client application.
The main emphasis is on exploiting the numerous specifications of JAVA EE, implemented in WildFly server, in order to achieve behaviour of cluster of agents which communicate with both client and with each other.
The application (as a cluster), on request from user, creates multiple agents which perform extensive search of 3 real estate web sites.
Results are kept on different locations and, after being processed, are sent to user for review. 
The user as an end result can see: 

- Statistics for multiple property characteristic (like location, price, area, type etc.)
- Sorted listing of most popular properties, based on location,
- Sorted listing of properties by number of views, size, land or price.

##### Main functionalities

Implemented agent environments functionalities consist of: 
- acquiring types of agents and running agents
- initiatiation and startup of new agents of desired type and name
- communication between clients and agents with other running agents via ACL messages (the communication is carried out through JMS subsystem via message driven bean)
- communication between multiple running instances of the application on different phisical locations (one of which serves as a master node) -> all the running instances can communicate and redundantly maintain data about running agents, which can, consequently, communicate by forwarding messages between locations.


##### Workflow:

In order to aquire and produce final data for user on the client side there have to be 3 types of agents created: master, browser and collector.
- Server side application recieves request for desired data (either a property list or a statistic).
- Master agent is created. It then orchestrates other agents. First it creates a browser on it's node for each of the desired sites. 
- Browser agent searches the predefined locations and either returns data on a successfull hit, or informs the master about an unsuccessfull retrieval of data. 
- If data is not found the master agent triggeres startup of browser agents on all the other active nodes, one by one. 
- If the information is not available on any of nodes, master agent initiates the process of collecting the data. It does this by creating agents of collector type.
- Collector agents now perform an extensive search of desired websites and collect data. 
- After the collector agents return the results, the browser agents are invoked once again to collect the data from where they were saved and forward them back to master agent for further processing.
- The final treatment of data is done by master agent and can consist of: sorting (based on various criteria), finding the properties at the most popular locations or derive conclusions about statistical representation of real estate characteristics.

The data on which the processing happens consists of:
- Wide area location
- Narow area location
- Price
- Type
- Size
- Land area
- State
- Number of views

Data is collected from following websites:
- http://www.021-nekretnine.rs/
- http://www.info-nekretnine.rs/
- http://www.city-nekretnine.rs/

For further or more detailed explanation of any component of the system please contact one of the authors.

 
