/*
 *  (c) 2013 Michael A. Beck, disco | Distributed Computer Systems Lab
 *                                  University of Kaiserslautern, Germany
 *         All Rights Reserved.
 *
 *  This software is work in progress and is released in the hope that it will
 *  be useful to the scientific community. It is provided "as is" without
 *  express or implied warranty, including but not limited to the correctness
 *  of the code or its suitability for any particular purpose.
 *
 *  You are free to use this software for any non-commercial educational or
 *  research purpose, provided that this copyright notice is not removed or
 *  modified. For commercial uses please contact the respective author(s).
 *
 *  If you find our software useful, we would appreciate if you mentioned it
 *  in any publication arising from the use of this software or acknowledge
 *  our work otherwise. We would also like to hear of any fixes or useful
 *  extensions to this software.
 * 
 */
package unikl.disco.calculator.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import unikl.disco.calculator.SNC;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.ArrivalFactory;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.Hoelder;
import unikl.disco.calculator.symbolic_math.Service;
import unikl.disco.calculator.symbolic_math.ServiceFactory;

/**
 * This class provides several methods to construct and change a network
 * consisting of {@link Flow}s and {@link vertex}-objects. It has two static
 * variables, which are used to automatically tag new nodes and flows with
 * distinct IDs.
 *
 * @author Michael Beck
 * @author Sebastian Henningsen
 * @see Flow
 * @see Vertex
 * @see Arrival
 * @see Service
 * @see Hoelder
 *
 */
public class Network implements Serializable {

    //Members
    private int FLOW_ID;
    private int VERTEX_ID;
    private int HOELDER_ID;
    private Map<Integer, Flow> flows;
    private Map<Integer, Vertex> vertices;
    private Map<Integer, Hoelder> hoelders;
    private List<NetworkListener> listeners;

    // Constructor
    /**
     *
     */
    public Network() {
        this(null, null, null);
    }

    /**
     *
     * @param vertices
     * @param flows
     * @param hoelders
     */
    public Network(Map<Integer, Vertex> vertices, Map<Integer, Flow> flows, Map<Integer, Hoelder> hoelders) {
        this.flows = (flows != null) ? flows : new HashMap<Integer, Flow>();
        this.vertices = (vertices != null) ? vertices : new HashMap<Integer, Vertex>();
        this.hoelders = (hoelders != null) ? hoelders : new HashMap<Integer, Hoelder>();
        FLOW_ID = this.flows.size() + 1;
        VERTEX_ID = this.vertices.size() + 1;
        HOELDER_ID = this.hoelders.size() + 1;
        this.listeners = new ArrayList();
    }

    //Methods
    /**
     *
     * @param l
     * @return
     */
    public boolean addListener(NetworkListener l) {
        return listeners.add(l);
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean removeListener(NetworkListener l) {
        return listeners.remove(l);
    }

    /**
     * Creates a new Hoelder-Object and returns its id.
     *
     * @return the newly created Hoelder-Object.
     */
    public Hoelder createHoelder() {
        Hoelder hoelder = new Hoelder(HOELDER_ID);
        hoelders.put(hoelder.getHoelderID(), hoelder);
        incrementHOELDER_ID();
        return hoelder;
    }

    /**
     * Adds a new dummy vertex with alias
     *
     * @param alias
     */
    public void addVertex(String alias) {
        Vertex vertex = new Vertex(VERTEX_ID, alias, this);
        vertices.put(VERTEX_ID, vertex);
        incrementVERTEX_ID();
        for (NetworkListener l : listeners) {
            l.vertexAdded(vertex);

        }
    }

    /**
     * Adds a new vertex with predefined service to the network
     *
     * @param service the service the new vertex possesses
     */
    public void addVertex(Service service) {
        addVertex(service, "");
    }

    /**
     * Adds a new vertex with predefined service and alias
     *
     * @param service
     * @param alias
     * @return
     */
    public Vertex addVertex(Service service, String alias) {
        Vertex vertex = new Vertex(VERTEX_ID, service, alias, this);
        vertices.put(VERTEX_ID, vertex);
        incrementVERTEX_ID();
        for (NetworkListener l : listeners) {
            l.vertexAdded(vertex);
        }
        return vertex;
    }

    // Adds a vertex with a given ID, overwrites any existing vertices
    /**
     *
     * @param vertex
     * @return
     */
    public Vertex addVertex(Vertex vertex) {
        vertices.put(vertex.getID(), vertex);
        for (NetworkListener l : listeners) {
            l.vertexAdded(vertex);
        }
        return vertex;
    }

    /**
     * Computes the leftover service at the vertex with the given ID. This
     * serves the prioritized flow and removes it from the arrivals of the node.
     *
     * @param vertexID
     * @return The output bound of the flow being served
     * @throws ArrivalNotAvailableException
     */
    public Arrival computeLeftoverService(int vertexID) throws ArrivalNotAvailableException {
        int fid = getVertex(vertexID).getPrioritizedFlow();
        Arrival output = getVertex(vertexID).serve();
        // Notify listeners
        for (NetworkListener l : listeners) {
            l.vertexChanged(getVertex(vertexID));
            l.flowChanged(getFlow(fid));
        }
        return output;
    }

    /**
     *
     * @param vertex1ID
     * @param vertex2ID
     * @param flowOfInterestID
     * @return
     */
    public int convolute(int vertex1ID, int vertex2ID, int flowOfInterestID) {
        // Compute convoluted service, add a new vertex with that service
        // Some preconditions have to be met: 
        // (1) The vertices are direct neighbours wrt. to the flow of interest (FoI)
        // (2) There are no other flows on the path of the FoI
        if (areConvolutable(vertex1ID, vertex2ID, flowOfInterestID)) {
            Vertex v1 = getVertex(vertex1ID);
            Vertex v2 = getVertex(vertex2ID);
            Flow foi = getFlow(flowOfInterestID);
            Service convService = v1.getService().concatenate(v1.getService(), v2.getService());
            Vertex convVertex = addVertex(convService, vertex1ID + " conv. " + vertex2ID);
            Arrival arrival = v1.getArrivalOfFlow(flowOfInterestID);
            convVertex.addArrival(v1.getPriorityOfFlow(flowOfInterestID), flowOfInterestID, arrival);
            foi.replaceFirstOccurence(v1.getID(), v2.getID(), convVertex);

            /*Set<Integer> v1Flows = v1.getAllFlowIDs();
             Set<Integer> v2Flows = v2.getAllFlowIDs();

             for (Integer flowID : v1Flows) {
             // TODO: This is not very nice
             Arrival arrival = v1.getArrivalOfFlow(flowID);
             if (arrival != null) {
             convVertex.addArrival(v1.getPriorityOfFlow(flowID), flowID, arrival);
             } else {
             convVertex.addUnknownArrival(v1.getPriorityOfFlow(flowID), flowID);
             }
             Flow f = getFlow(flowID);
             f.replaceFirstOccurence(v1.getID(), v2.getID(), convVertex);
             }

             for (Integer flowID : v2Flows) {
             Arrival arrival = v2.getArrivalOfFlow(flowID);
             if (arrival != null) {
             convVertex.addArrival(v2.getPriorityOfFlow(flowID), flowID, arrival);
             } else {
             convVertex.addUnknownArrival(v2.getPriorityOfFlow(flowID), flowID);
             }
             // TODO: Make priorities unique again
             Flow f = getFlow(flowID);
             f.replaceFirstOccurence(v1.getID(), v2.getID(), convVertex);
             }*/
            removeVertex(v1);
            removeVertex(v2);
            return convVertex.getID();
        } else {
            throw new IllegalArgumentException("Vertices are not convolutable!");
        }
    }

    public boolean areConvolutable(int vertex1ID, int vertex2ID, int flowID) {
        return true;
    }

    /**
     * Sets the service at a specific node in the network
     *
     * @param vertex the vertex to be altered
     * @param service the new service at the specific vertex
     */
    public void setServiceAt(Vertex vertex, Service service) {
        vertex.setMGFService(service);
    }

    /**
     * Sets the service at the vertex with the given id
     *
     * @param vertex_id the id of the vertex to be altered
     * @param service the new service at the specific vertex
     */
    public void setServiceAt(int vertex_id, Service service) {
        vertices.get(vertex_id).setMGFService(service);
    }

    /**
     * Removes a vertex from the network. This is done by removing the vertex
     * from all flows, which include this vertex in its route. I.e. if a flow
     * routes through A-B-C and B is removed the new route will be A-C. After
     * this the vertex is removed from the list of vertices.
     *
     * @param vertex the <code>Vertex</code> to be removed
     * @return if removing the vertex was successful
     */
    public boolean removeVertex(Vertex vertex) {
        return removeVertex(vertex.getID());
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean removeVertex(int id) {
        boolean success = false;
        Vertex vertex = getVertex(id);
        if (vertices.containsKey(id)) {
            for (int i : getVertex(id).getAllFlowPriorities().keySet()) {
                flows.get(i).removeVertex(id);
                for (NetworkListener l : listeners) {
                    l.flowChanged(getFlow(i));
                }
            }
            vertices.remove(id);
            success = true;
        }
        // Notify listeners
        for (NetworkListener l : listeners) {
            l.vertexRemoved(vertex);
        }
        return success;
    }

    /**
     * Creates a new flow with an initial arrival and a complete description of
     * its route through the network (in expression all of its vertices and the
     * corresponding priorities at these vertices of the flow).
     *
     * @param initial_arrival the arrival at the first node
     * @param route
     * @param vertices the vertices the flow traverses
     * @param priorities the priorities of the flow at the corresponding
     * vertices
     * @param alias the alias of the new flow
     * @return
     * @throws ArrivalNotAvailableException
     */
    public int addFlow(Arrival initial_arrival, List<Integer> route, List<Integer> priorities,
            String alias) throws ArrivalNotAvailableException {

        // Creates the dummy arrivals for all vertices after the first
        List<Arrival> arrivals = new ArrayList<>(1);
        arrivals.add(0, initial_arrival);
        for (int i = 1; i < route.size(); i++) {
            arrivals.add(new Arrival(this));
        }

        //Adds the flow into the flow list
        Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias, this);
        flows.put(FLOW_ID, flow);

        //Writes the flow in its corresponding vertices
        for (int i = 0; i < route.size(); i++) {
            Vertex vertex;
            vertex = vertices.get(route.get(i));
            vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
        }

        Vertex first_vertex = vertices.get(route.get(0));

        //Initializes the first arrival at the first vertex
        first_vertex.learnArrival(FLOW_ID, initial_arrival);

        //Increments the flow count
        incrementFLOW_ID();

        // Notify the listeners
        for (NetworkListener l : listeners) {
            l.flowAdded(flow);
        }
        return flow.getID();
    }

    /**
     * Adds a flow with all its arrivals, priorities and vertices to the network
     *
     * @param arrivals the arrivals at the vertices. Normally only te initial
     * arrival is needed and hence all other arrivaly will be overwritten by the
     * analysis.
     * @param route
     * @param priorities the priorities of the flow at the corresponding
     * vertices
     * @param alias the alias of the new flow
     * @throws ArrivalNotAvailableException
     */
    public void addFlow(List<Arrival> arrivals, List<Integer> route,
            List<Integer> priorities, String alias) throws ArrivalNotAvailableException {

        Flow flow = new Flow(FLOW_ID, route, arrivals, priorities, alias, this);

        //Adds the flow into the flow list
        flows.put(FLOW_ID, flow);

        //Writes the flow in its corresponding vertices
        for (int i = 0; i < route.size(); i++) {
            Vertex vertex;
            vertex = vertices.get(route.get(i));
            vertex.addUnknownArrival(priorities.get(i), FLOW_ID);
            vertex.learnArrival(FLOW_ID, arrivals.get(i));
        }

        //Increments the flow count
        incrementFLOW_ID();

        // Notify the listeners
        for (NetworkListener l : listeners) {
            l.flowAdded(flow);

        }
    }

    /**
     * Appends a node to an already existing flow. The arrival at this appended
     * node is non established.
     *
     * @param flow_id the flow ot which the node is appended
     * @param vertex_id the vertex which is appended
     * @param priority the priority the flow has at the appended vertex.
     */
    public void appendNode(int flow_id, int vertex_id, int priority) {

        //Adds the vertex to the path of the flow
        flows.get(flow_id).addNodetoPath(vertex_id, priority);

        //Adds a non-established arrival to the appended vertex
        vertices.get(vertex_id).addUnknownArrival(priority, flow_id);
    }

    /**
     * Sets the initial arrival of some flow
     *
     * @param flow_id the flow-id of the flow, which is initialized
     * @param arrival the initial arrival
     * @throws ArrivalNotAvailableException
     */
    public void setInitialArrival(int flow_id, Arrival arrival) throws ArrivalNotAvailableException {

        //initializes the arrival at the flow
        flows.get(flow_id).setInitialArrival(arrival);

        //the arrival is established at the vertex
        Vertex vertex = vertices.get(flows.get(flow_id).getFirstVertexID());
        vertex.learnArrival(flow_id, arrival);
    }

    /**
     * Removes a flow from the network. This is done by deleting the
     * corresponding entries in the vertices, which lie on the route of the
     * flow. After this the flow itself is removed from <code>flows</code>.
     *
     * @param flow the <code>Flow</code> to be removed
     * @return returns if removing the flow was successful
     */
    public boolean removeFlow(Flow flow) {
        boolean success = false;
        if (flows.containsKey(flow.getID())) {
            for (int i : flow.getVerticeIDs()) {
                vertices.get(i).removeFlow(flow.getID());
            }
            flows.remove(flow.getID());
            success = true;

        }
        // Notify the listeners
        for (NetworkListener l : listeners) {
            l.flowRemoved(flow);

        }
        return success;
    }

    private void incrementFLOW_ID() {
        FLOW_ID++;
    }

    private void incrementVERTEX_ID() {
        VERTEX_ID++;
    }

    private void incrementHOELDER_ID() {
        HOELDER_ID++;
    }

    /**
     *
     * @param reset
     */
    public void resetFLOW_ID(int reset) {
        FLOW_ID = reset;
    }

    /**
     *
     * @param reset
     */
    public void resetVERTEX_ID(int reset) {
        VERTEX_ID = reset;
    }

    /**
     *
     * @param reset
     */
    public void resetHOELDER_ID(int reset) {
        HOELDER_ID = reset;
    }

    /**
     * Returns a string representation of the network. This is done by first
     * listing the vertices aliases or if not existend their IDs.
     *
     * @return
     */
    public String getStringRepresentation() {
        String result_string = "List of vertices:\n";
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            result_string = result_string + "Vertex-ID: " + entry.getValue().getID()
                    + "\t Vertex-Alias: " + entry.getValue().getAlias() + "\n";
        }
        result_string = result_string + "List of flows:\n";
        for (Map.Entry<Integer, Flow> entry : flows.entrySet()) {
            result_string = result_string + "Flow-ID: " + entry.getValue().getID()
                    + "\t Flow-Alias: " + entry.getValue().getAlias() + "\t route:\n" + entry.getValue().getVerticeIDs().toString() + "\n";
        }

        return result_string + "Number of Hoelder parameters: " + (HOELDER_ID - 1);
    }

    //Getter and Setter
    /**
     *
     * @param id
     * @return
     */
    public Vertex getVertex(int id) {
        return vertices.get(id);
    }

    public Vertex getVertexByName(String name) {
        for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
            Integer key = entry.getKey();
            Vertex vertex = entry.getValue();
            if (vertex.getAlias().equals(name)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     *
     * @param id
     * @return
     */
    public Flow getFlow(int id) {
        return flows.get(id);
    }

    /**
     *
     * @return
     */
    public int getVERTEX_ID() {
        return VERTEX_ID;
    }

    /**
     *
     * @return
     */
    public int getFLOW_ID() {
        return FLOW_ID;
    }

    /**
     *
     * @return
     */
    public int getHOELDER_ID() {
        return HOELDER_ID;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    /**
     *
     * @return
     */
    public Network deepCopy() {
        /*Network newNetwork = new Network();
         Map<Integer, Vertex> newVertices = new HashMap(vertices.size());
         for (Map.Entry<Integer, Vertex> entry : newVertices.entrySet()) {
         Vertex newVertex = entry.getValue().copy();
         newNetwork.addVertex(newVertex);
         //newVertices.put(entry.getKey(), newVertex);
         }
            
         Map<Integer, Flow> newFlows = new HashMap(flows.size());
         for (Map.Entry<Integer, Flow> entry : newFlows.entrySet()) {
         Flow newFlow = entry.getValue().copy();
         newFlows.put(entry.getKey(), newFlow);
         }
            
         Map<Integer, Hoelder> newHoelders = new HashMap(hoelders.size());
         for (Map.Entry<Integer, Hoelder> entry : newHoelders.entrySet()) {
         Hoelder newHoelder = entry.getValue().copy();
         newHoelders.put(entry.getKey(), newHoelder);
         }
         return new Network(newVertices, newFlows, newHoelders);*/
        // Copy Network using serialization:
        //Serialization of object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(vertices);
            out.writeObject(flows);
            out.writeObject(hoelders);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //De-serialization of object
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in;

        Map<Integer, Vertex> newVertices = null;
        Map<Integer, Flow> newFlows = null;
        Map<Integer, Hoelder> newHoelders = null;
        try {
            in = new ObjectInputStream(bis);
            newVertices = (Map<Integer, Vertex>) in.readObject();
            newFlows = (Map<Integer, Flow>) in.readObject();
            newHoelders = (Map<Integer, Hoelder>) in.readObject();
        } catch (IOException | ClassNotFoundException exc) {
            System.out.println(exc.getMessage());
        }
        Network nw = new Network(newVertices, newFlows, newHoelders);
        return nw;
    }

    /**
     * Loads a network, which is given in <code>file</code>. Can only read
     * networks, which had been saved by a simple
     * <code>ObjectOutputStream</code>. The order of saved objects (and its
     * corresponding type) is: vertices (HashMap<Integer, Vertex>
     * flows (HashMap<Integer, Flow>) hoelders (HashMap<Integer, Hoelder>)
     *
     * @param profile_path
     * @return
     */
    public static Network load(File profile_path) {
        //will read profile.txt line by line
        BufferedReader br = null;
        Network nw = new Network();
        // Kind of a hack
        List<NetworkListener> oldListeners = SNC.getInstance().getCurrentNetwork().getListeners();
        for (NetworkListener l : oldListeners) {
            l.clear();
            nw.addListener(l);
        }
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(profile_path));

            //reads all lines
            while ((sCurrentLine = br.readLine()) != null) {
                //if a line starts with "I" an interface (i.e. service element) is added to the network in form of a Vertex.
                if (sCurrentLine.startsWith("I")) {
                    try {
                        nw.handleVertexLine(sCurrentLine);
                    } catch (BadInitializationException | NumberFormatException e) {
                        System.out.println("Bad Initialization in line " + sCurrentLine + ". Parameter for Constant Rate server must be a non-negative number.");
                        e.printStackTrace();
                    }
                }

                //if a line starts with "F" a flow is added to the network in form of a Flow-object.
                if (sCurrentLine.startsWith("F")) {
                    try {
                        nw.handleFlowLine(sCurrentLine);
                    } catch (NumberFormatException | BadInitializationException | ArrivalNotAvailableException e) {
                        System.out.println("Error at line " + sCurrentLine + ":");
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("The following vertices had been added (alias, rate):");
            for (Vertex vertex : nw.getVertices().values()) {
                System.out.println(vertex.getAlias() + ", " + vertex.getService().toString());
            }
            System.out.println("The following flows had been added (alias, route, priorities):");
            for (Flow flow : nw.getFlows().values()) {
                System.out.println(flow.getAlias() + ", " + flow.getVerticeIDs() + ", " + flow.getPriorities());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nw;
    }

    private int handleVertexLine(String line) throws BadInitializationException, NumberFormatException {
        // Removes the first character, we do not need that anymore anyway
        line = line.substring(1).trim();
        String[] lineParts = line.split(",");
        String vertex_name = lineParts[0].trim();
        Double service_rate = Double.parseDouble(lineParts[3].trim());
        return this.addVertex(ServiceFactory.buildConstantRate(-service_rate), vertex_name).getID();
    }

    private void handleFlowLine(String line) throws NumberFormatException, BadInitializationException, ArrivalNotAvailableException {
        final int pathOffset = 2; // There are 2 entries before the route
        // Removes the first character, we do not need that anoymore
        line = line.substring(1).trim();
        String[] lineParts = line.split(",");
        String flowName = lineParts[0].trim();
        int pathLength = Integer.parseInt(lineParts[1].trim());
        // We know that path_length entries in lineParts are only relevant for the route
        List<Integer> route = new ArrayList<>();
        List<Integer> priorities = new ArrayList<>();
        for (int i = 0; i < pathLength; i++) {
            String[] entry = lineParts[i + pathOffset].trim().split(":");
            Vertex v = this.getVertexByName(entry[0].trim());
            if (v == null) {
                throw new IllegalArgumentException("Could not find Vertex " + entry[0].trim());
            }
            route.add(this.getVertexByName(entry[0].trim()).getID());
            priorities.add(Integer.parseInt(entry[1].trim()));
        }

        // Find out the Arrival Type now. It's located after the path
        String arrivalType = lineParts[pathOffset + pathLength].trim();
        Arrival arrival = null;
        if (arrivalType.equals("EBB")) {
            double rate;
            double decay;
            double prefactor;
            // TODO: Error Handling.
            rate = Double.parseDouble(lineParts[pathOffset + pathLength + 1].trim());
            decay = Double.parseDouble(lineParts[pathOffset + pathLength + 2].trim());
            prefactor = Double.parseDouble(lineParts[pathOffset + pathLength + 3].trim());
            arrival = ArrivalFactory.buildEBB(rate, decay, prefactor);

        } else if (arrivalType.equals("CONSTANT")) {
            double rate = Double.parseDouble(lineParts[pathOffset + pathLength + 1].trim());
            arrival = ArrivalFactory.buildConstantRate(rate);
        } else if (arrivalType.equals("EXPONENTIAL")) {
            double rate = Double.parseDouble(lineParts[pathOffset + pathLength + 1].trim());
            arrival = ArrivalFactory.buildExponentialRate(rate);
        } else if (arrivalType.equals("STATIONARYTB")) {
            double rate;
            double bucket;
            double maxTheta;
            // Check if there are 2 or 3 parameters given
            if (lineParts.length - pathOffset - pathLength - 1 == 2) {
                rate = Double.parseDouble(lineParts[pathOffset + pathLength + 1].trim());
                bucket = Double.parseDouble(lineParts[pathOffset + pathLength + 2].trim());
                arrival = ArrivalFactory.buildStationaryTB(rate, bucket);
            } else {
                rate = Double.parseDouble(lineParts[pathOffset + pathLength + 1].trim());
                bucket = Double.parseDouble(lineParts[pathOffset + pathLength + 2].trim());
                maxTheta = Double.parseDouble(lineParts[pathOffset + pathLength + 3].trim());
                arrival = ArrivalFactory.buildStationaryTB(rate, bucket, maxTheta);
            }

        } else {
            throw new IllegalArgumentException("No arrival with type " + arrivalType + " known.");
        }
        int flowID = this.addFlow(arrival, route, priorities, flowName);
        this.getFlow(flowID).getInitialArrival().getArrivaldependencies().clear();

    }

    /**
     * Saves the network in the given <code>file</code>. This is done by using a
     * simple ObjectOutputStream. The order of saved objects (and its
     * corresponding type) is: vertices (HashMap<Integer, Vertex>
     * flows (HashMap<Integer, Flow>) hoelders (HashMap<Integer, Hoelder>)
     *
     * @param file
     */
    public void save(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.getVertices());
            oos.writeObject(this.getFlows());
            oos.writeObject(this.getHoelders());
            oos.close();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, Flow> getFlows() {
        return flows;
    }

    /**
     *
     * @return
     */
    public Map<Integer, Hoelder> getHoelders() {
        return hoelders;
    }

    public List<NetworkListener> getListeners() {
        return listeners;
    }

}
