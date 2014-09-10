package unikl.disco.calculator;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import unikl.disco.calculator.network.Network;
import unikl.disco.calculator.network.NetworkListener;
import unikl.disco.calculator.network.Vertex;
import unikl.disco.calculator.optimization.AbstractOptimizer;
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
import unikl.disco.misc.UndoRedoStack;

/**
 * This class contains the main method, which starts and prepares the GUI. It
 * also serves as interface relaying the commands from the user to the
 * corresponding classes {@link Network}, {@SimpleAnalysis} etc. Alternatively
 * the implemented methods can be used directly in the main-method to construct
 * a network and perform calculations on it.
 *
 * @author Michael Beck
 *
 */
public class SNC {

    // TODO
    private static Network nw;
    private static UndoRedoStack undoRedoStack;
    private static SNC singletonInstance;

    private SNC() {
    }

    public static SNC getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SNC();
        }
        return singletonInstance;
    }

    //Main-Method
    public static void main(String[] args) throws InvocationTargetException, InterruptedException,
            ArrivalNotAvailableException, BadInitializationException, DeadlockException, ThetaOutOfBoundException,
            ParameterMismatchException, ServerOverloadException {

        // TODO
        SNC snc = SNC.getInstance();
        nw = new Network();
        undoRedoStack = new UndoRedoStack();
        final MainWindow main = new MainWindow();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                main.createGUI();
            }
        };
        //snc.ConvolutionTest();
        //System.exit(0);
        EventQueue.invokeLater(runnable);

    }

    public void registerNetworkListener(NetworkListener listener) {
        SNC.getInstance().getCurrentNetwork().addListener(listener);
    }

    public void undo() {
        undoRedoStack.undo();
    }

    public void redo() {
        undoRedoStack.redo();
    }

    public void invokeCommand(Command c) {
        undoRedoStack.insertIntoStack(c);
        c.execute();
    }

    public Network getCurrentNetwork() {
        return nw;
    }

    public void saveNetwork(File file) {
        getCurrentNetwork().save(file);
    }

    public void loadNetwork(File file) {
        nw = Network.load(file);
    }

    public Network getNetwork(int id) {
        return nw;
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
     * @return the result of the analysis in arrival-representation.
     */
    public Arrival analyzeNetwork(Flow flow, Vertex vertex, AnalysisType anaType, AbstractAnalysis.Boundtype boundtype, Network nw) {

        //Preparations
        Arrival bound = null;

        Map<Integer, Vertex> givenVertices = new HashMap<Integer, Vertex>();
        for (Entry<Integer, Vertex> entry : nw.getVertices().entrySet()) {
            givenVertices.put(entry.getKey(), entry.getValue().copy());
        }

        Map<Integer, Flow> givenFlows = new HashMap<Integer, Flow>();
        for (Entry<Integer, Flow> entry : nw.getFlows().entrySet()) {
            givenFlows.put(entry.getKey(), entry.getValue().copy());
        }

        int resetFlowID = nw.getFLOW_ID();
        int resetHoelderID = nw.getHOELDER_ID();
        int resetVertexID = nw.getVERTEX_ID();

        Analyzer analyzer = AnalysisFactory.getAnalyzer(anaType, nw, givenVertices, givenFlows, flow.getID(), vertex.getID(), boundtype);
        try {
            bound = analyzer.analyze();
        } catch (ArrivalNotAvailableException | DeadlockException | BadInitializationException e) {
            e.printStackTrace();
        }

        //Resets the network
        nw.resetFLOW_ID(resetFlowID);
        nw.resetHOELDER_ID(resetHoelderID);
        nw.resetVERTEX_ID(resetVertexID);

        return bound;
    }

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
        Optimizer optimizer = OptimizationFactory.getOptimizer(nw, bound, analysisBound, optAlgorithm);

        try {
            result = optimizer.minimize(thetaGran, hoelderGran);
            // Temporary Debug Test
            if (boundType == BoundType.BACKLOG || boundType == BoundType.DELAY) {
                debugVal = optimizer.Bound(symbolicBound, analysisBound, value, thetaGran, hoelderGran);
            } else {
                debugVal = optimizer.ReverseBound(symbolicBound, analysisBound, value, thetaGran, hoelderGran);
            }

        } catch (ThetaOutOfBoundException | ParameterMismatchException | ServerOverloadException e) {
            e.printStackTrace();
        }
        // For debugging purposes
        if (result != debugVal) {
            throw new RuntimeException("[DEBUG] Optimization results do not match!");
        }
        return result;
    }

    public AbstractAnalysis.Boundtype convertBoundTypes(BoundType boundType) {
        AbstractAnalysis.Boundtype targetBoundType = null;
        if (boundType == BoundType.BACKLOG || boundType == BoundType.INVERSE_BACKLOG) {
            targetBoundType = AbstractAnalysis.Boundtype.BACKLOG;
        } else if (boundType == BoundType.DELAY || boundType == BoundType.INVERSE_DELAY) {
            targetBoundType = AbstractAnalysis.Boundtype.DELAY;
        } else {
            throw new IllegalArgumentException("No such boundtype");
        }
        return targetBoundType;
    }

    // Temp:
    private void ConvolutionTest() {
        System.out.println("Convolution Test:");
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
        Command convV1V2 = new ConvoluteVerticesCommand(1, 2, -1, SNC.getInstance());
        invokeCommand(addV1);
        invokeCommand(addV2);
        invokeCommand(addF1);
        invokeCommand(convV1V2);

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
