/*
 *  (c) 2017 Michael A. Beck, Sebastian Henningsen
 *  		disco | Distributed Computer Systems Lab
 *  		University of Kaiserslautern, Germany
 *  All Rights Reserved.
 *
 * This software is work in progress and is released in the hope that it will
 * be useful to the scientific community. It is provided "as is" without
 * express or implied warranty, including but not limited to the correctness
 * of the code or its suitability for any particular purpose.
 *
 * This software is provided under the MIT License, however, we would 
 * appreciate it if you contacted the respective authors prior to commercial use.
 *
 * If you find our software useful, we would appreciate if you mentioned it
 * in any publication arising from the use of this software or acknowledge
 * our work otherwise. We would also like to hear of any fixes or useful
 */
package unikl.disco.calculator;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import unikl.disco.calculator.commands.AddFlowCommand;
import unikl.disco.calculator.commands.AddVertexCommand;
import unikl.disco.calculator.commands.Command;
import unikl.disco.calculator.commands.ConvoluteVerticesCommand;
import unikl.disco.calculator.gui.MainWindow;
import unikl.disco.calculator.network.AbstractAnalysis;
import unikl.disco.calculator.network.AnalysisFactory;
import unikl.disco.calculator.network.AnalysisType;
import unikl.disco.calculator.network.Analyzer;
import unikl.disco.calculator.network.ArrivalNotAvailableException;
import unikl.disco.calculator.network.DeadlockException;
import unikl.disco.calculator.network.Flow;
import unikl.disco.calculator.network.LadderAnalysis;
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.NetworkListener;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.calculator.optimization.BoundFactory;
import unikl.disco.calculator.optimization.BoundType;
import unikl.disco.calculator.optimization.Optimizable;
import unikl.disco.calculator.optimization.OptimizationFactory;
import unikl.disco.calculator.optimization.OptimizationType;
import unikl.disco.calculator.optimization.Optimizer;
import unikl.disco.calculator.symbolic_math.Arrival;
import unikl.disco.calculator.symbolic_math.BadInitializationException;
import unikl.disco.calculator.symbolic_math.ParameterMismatchException;
import unikl.disco.calculator.symbolic_math.ServerOverloadException;
import unikl.disco.calculator.symbolic_math.ThetaOutOfBoundException;
import unikl.disco.calculator.symbolic_math.functions.ConstantFunction;
import unikl.disco.misc.AnalysisException;
import unikl.disco.misc.FileOperationException;
import unikl.disco.misc.UndoRedoStack;

/**
 * This class contains the main method, which starts and prepares the GUI. It
 * also serves as interface relaying the commands from the user to the
 * corresponding classes {@link Network}, {@link SimpleAnalysis} etc.
 * Additionally, methods for analyzing networks as well as a simple Undo/Redo
 * functionality is provided. Note that this class follows the Singleton
 * Pattern, which may, however, be changed in the near future.
 *
 * @author Michael Beck
 * @author Sebastian Henningsen
 *
 */
public class SNC {

    private final UndoRedoStack undoRedoStack;
    private static SNC singletonInstance;
    private final List<Network> networks;
    private final int currentNetworkPosition;

    private SNC() {
        networks = new ArrayList<>();
        undoRedoStack = new UndoRedoStack();
        networks.add(new Network());    // Create an initially empty Network
        currentNetworkPosition = 0;
    }

    /**
     * Returns the singleton instance, creates a new one if none exists
     *
     * @return
     */
    public static SNC getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SNC();
        }
        return singletonInstance;
    }

    /**
     * The main method of the program, used to start the GUI and initialize
     * everything
     *
     * @param args Command line arguments - not used at the moment
     * @throws InvocationTargetException
     * @throws InterruptedException
     * @throws ArrivalNotAvailableException
     * @throws BadInitializationException
     * @throws DeadlockException
     * @throws ThetaOutOfBoundException
     * @throws ParameterMismatchException
     * @throws ServerOverloadException
     */
    public static void main(String[] args) throws InvocationTargetException, InterruptedException,
            ArrivalNotAvailableException, BadInitializationException, DeadlockException, ThetaOutOfBoundException,
            ParameterMismatchException, ServerOverloadException {

        SNC snc = SNC.getInstance();
        final MainWindow main = new MainWindow();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                main.createGUI();
            }
        };
        EventQueue.invokeLater(runnable);

    }

    /**
     * Registers a new {@link NetworkListener} on the current network
     *
     * @param listener The listener that should be added
     */
    public void registerNetworkListener(NetworkListener listener) {
        SNC.getInstance().getCurrentNetwork().addListener(listener);
    }

    /**
     * Reverts the effects of the last {@link Command}
     */
    public void undo() {
        undoRedoStack.undo();
    }

    /**
     * Redos the last {@link Command} that was un-done
     */
    public void redo() {
        undoRedoStack.redo();
    }

    /**
     * Executes the given {@link Command} and adds it to the
     * {@link UndoRedoStack} Throws NetworkActionException upon error.
     *
     * @param c The command to be invoked.
     */
    public void invokeCommand(Command c) {
        undoRedoStack.insertIntoStack(c);
        c.execute();
    }

    /**
     * Returns the currently accessed {@link Network}. Note that only one
     * network can be opened at a time, at the moment.
     *
     * @return The current network
     */
    public Network getCurrentNetwork() {
        return networks.get(currentNetworkPosition);
    }

    /**
     * Saves the currently accessed {@link Network} to the file specified by the
     * parameter.
     *
     * @param file The file to which the network should be saved.
     */
    public void saveNetwork(File file) {
        getCurrentNetwork().save(file);

    }

    /**
     * Load a {@link Network} from the file specified by the parameter. Since,
     * at the moment, only one network can be opened at a time, the currently
     * accessed network is overwritten. Throws a FileOperationException when an
     * error upon load occurs
     *
     * @param file The file in which the network is saved.
     */
    public void loadNetwork(File file) {
        Network nw = Network.load(file, true);
        networks.set(currentNetworkPosition, nw);
    }

    /**
     * Returns the {@link Network} with the corresponding ID, if it exists. If
     * not, an exception is thrown. At the moment the current network position
     * will be returned.
     *
     * @param id The ID of the desired network.
     * @return The current network
     */
    public Network getNetwork(int id) {
        return networks.get(currentNetworkPosition);
    }

    /**
     * This relays the command of calculating a symbolic (not optimized) bound
     * to the corresponding {@link AbstractAnalysis}-subclass. The result is
     * returned in arrival-representation.
     *
     * @param flow the <code>Flow</code> of interest.
     * @param vertex the <code>Vertex</code> of interest.
     * @param anaType the type of analysis used
     * @param boundtype the type of bound, which needs to be computed.
     * @param nw the <code>Network</code> to which the other parameters belong
     * @return the result of the analysis in arrival-representation.
     */
    public Arrival analyzeNetwork(Flow flow, Vertex vertex, AnalysisType anaType, AbstractAnalysis.Boundtype boundtype, Network nw) {

        //Preparations
        Arrival bound = null;
        File file = null;
        try {
            file = File.createTempFile("SNC", "txt");
        } catch (IOException e) {
            throw new FileOperationException("Error while analyzing Network: " + e.getMessage());
        }
        this.saveNetwork(file);
        Network nwCopy = Network.load(file, false);
        Analyzer analyzer = AnalysisFactory.getAnalyzer(anaType, nwCopy, nwCopy.getVertices(), nwCopy.getFlows(), flow.getID(), vertex.getID(), boundtype);
        try {
            bound = analyzer.analyze();
        } catch (ArrivalNotAvailableException | DeadlockException | BadInitializationException e) {
            throw new AnalysisException(e);
        }
        return bound;
    }

    /**
     * Computes an optimized bound for the desired {@link Flow} and
     * {@link Vertex}.
     *
     * @param flow The {@link Flow} of interest
     * @param vertex The {@link Vertex} of interest
     * @param thetaGran Specifies the optimization granularity of the
     * theta-parameter
     * @param hoelderGran Specifies the optimization granularity of the
     * hoelder-parameter
     * @param analysisType The desired analysis algorithm (see
     * {@link AnalysisType})
     * @param optAlgorithm The desired optimization algorithm (see
     * {@link OptimizationType})
     * @param boundType The desired {@link BoundType}
     * @param value Depending on the boundType parameter this is either: A
     * violation probability (in case of an inverse bound) or a bound value
     * (otherwise)
     * @param nw The network to which the <code>flow</code> and
     * <code>vertex</code> belong to
     * @return An optimal bound
     */
    public double optimizeSymbolicFunction(Flow flow, Vertex vertex, double thetaGran, double hoelderGran,
            AnalysisType analysisType, OptimizationType optAlgorithm, BoundType boundType, double value, Network nw) {

        double result = Double.NaN;
        double debugVal = Double.NaN;
        AbstractAnalysis.Boundtype analysisBound = convertBoundTypes(boundType);
        Arrival symbolicBound = analyzeNetwork(flow, vertex, analysisType, analysisBound, nw);

        //Backlog values are represented by negative values in the arrival representation
        if (boundType == BoundType.BACKLOG && value > 0) {
            value = -value;
        }

        Optimizable bound = BoundFactory.createBound(symbolicBound, boundType, value);
        Optimizer optimizer = OptimizationFactory.getOptimizer(bound, analysisBound, optAlgorithm);

        try {
            result = optimizer.minimize(thetaGran, hoelderGran);
            // Temporary Debug Test
            if (boundType == BoundType.BACKLOG || boundType == BoundType.DELAY) {
                debugVal = optimizer.Bound(symbolicBound, analysisBound, value, thetaGran, hoelderGran);
            } else {
                debugVal = optimizer.ReverseBound(symbolicBound, analysisBound, value, thetaGran, hoelderGran);
            }

        } catch (ThetaOutOfBoundException | ParameterMismatchException | ServerOverloadException e) {
            throw new AnalysisException(e);
        }
        // For debugging purposes
        if (result != debugVal) {
            throw new RuntimeException("[DEBUG] Optimization results do not match!");
        }
        return result;
    }

    /**
     * Helper function to convert between AbstractAnalysis.BoundType and
     * BoundType
     *
     * @param boundType The BoundType that should be converted
     * @return An appropriate AbstractAnalysis.BoundType
     */
    AbstractAnalysis.Boundtype convertBoundTypes(BoundType boundType) {
        AbstractAnalysis.Boundtype targetBoundType = null;
        if (boundType == BoundType.BACKLOG || boundType == BoundType.INVERSE_BACKLOG) {
            targetBoundType = AbstractAnalysis.Boundtype.BACKLOG;
        } else if (boundType == BoundType.DELAY || boundType == BoundType.INVERSE_DELAY) {
            targetBoundType = AbstractAnalysis.Boundtype.DELAY;
        } else {
            throw new AnalysisException("No such boundtype");
        }
        return targetBoundType;
    }

    // Temp:
    private void ConvolutionTest() {
        System.out.println("Convolution Test:");
        Network nw = getCurrentNetwork();
        Command addV1 = new AddVertexCommand("V1", -2.0, -1, SNC.getInstance());
        Command addV2 = new AddVertexCommand("V2", -1.0, -1, SNC.getInstance());
        List<Integer> f1Route = new ArrayList<>();
        List<Integer> f1Prio = new ArrayList<>();
        f1Route.add(1);
        f1Route.add(2);
        f1Prio.add(1);
        f1Prio.add(1);
        Arrival arrival = new Arrival(new ConstantFunction(0), new ConstantFunction(0.5), nw);
        Command addF1 = new AddFlowCommand("F1", arrival, f1Route, f1Prio, -1, SNC.getInstance());
        //Command convV1V2 = new ConvoluteVerticesCommand(1, 2, -1, SNC.getInstance());
        invokeCommand(addV1);
        invokeCommand(addV2);
        invokeCommand(addF1);
        //invokeCommand(convV1V2);

        Map<Integer, Vertex> vertices = nw.getVertices();
        Map<Integer, Flow> flows = nw.getFlows();
        System.out.println("Flows");
        for (Entry<Integer, Flow> entry : flows.entrySet()) {
            System.out.print(entry.getValue().getAlias() + ": " + entry.getValue().getVerticeIDs());
        }
        System.out.println("\nVertices");
        for (Entry<Integer, Vertex> entry : vertices.entrySet()) {
            System.out.print(entry.getKey() + " " + entry.getValue().getAlias() + " ");
        }

    }
}
