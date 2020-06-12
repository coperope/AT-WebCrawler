package serverCommunications;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AgentType;
import node.AgentCenter;


public interface CommunicationsRest {
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<AgentCenter> newConnection(AgentCenter connection);
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean oneNode(AgentCenter connection);
	
	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean allNodes(List<AgentCenter> connection);
	
	
	
    @GET
    @Path("/agents/classes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public List<AgentType> allAgentTypes();
  
    
    @POST
    @Path("/agents/classes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public List<AgentType> sendAgentTypes();
		/*
		 * @GET
		 * 
		 * @Path("/users/loggedIn")
		 * 
		 * @Produces(MediaType.APPLICATION_JSON) public HashMap<String,User>
		 * getAllUsers();
		 */
	 
	
	@DELETE
	@Path("/node/{alias}")
	public boolean deleteNode(@PathParam("alias") String alias);
	
	@GET
	@Path("/node")
	public AgentCenter getNode();
}
