import java.util.*;

class Main {
    static class Edge {
        int from, to, capacity;
        
        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
        }
    }

    public static void main(String[] args) {

        float start = System.nanoTime();
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int[] scores = new int[n+2];
        
        // Read scores for each node
        for (int i = 0; i < n; i++) {
            scores[i] = sc.nextInt();
        }

        // Add source and sink scores to the graph
        scores[n]=0;
        scores[n+1]=0;
        
        // Build graph G = (V, E)
        @SuppressWarnings("unchecked")
        HashMap<Integer, List<Edge>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<Edge>());
        }
        
        // Define a source and sink to the graph
        int source = 0;
        int sink = n-1;
        
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt() - 1;
            int v = sc.nextInt() - 1;
            int capacity = sc.nextInt();
            addEdge(graph, u, v, capacity);
        }

        // Run Ford-Fulkerson algorithm to find max flow from source to sink. Calculate the residual graph.
        HashMap<Integer, List<Edge>> rGraph = fordFulkerson(graph, source, sink);

        // Find all nodes reachable from the source
        List<Integer> reachableNodesFromSource = findReachableNodesFromSource(rGraph, source);

        // Find all nodes reachable from the sink
        List<Integer> reachableNodesToSink = findReachableNodesToSink(rGraph, sink);

        // Define a source and sink to the project selection graph
        int projectSource = n;
        int projectSink = n+1;

        // create the project selection graph
        HashMap<Integer, List<Edge>> projectSelectionGraph = createProjectSelectionGraph(reachableNodesFromSource, reachableNodesToSink, projectSource, projectSink, scores, rGraph);

        // Reduce to max flow problem. Calculate the maxscore from project selection. Run the FF algorithm on the project selection graph to find the max flow from the source to the sink. Calculate the residual graph.
        HashMap<Integer, List<Edge>> rProjectSelectionGraph = fordFulkerson(projectSelectionGraph, projectSource, projectSink);

        // Find all nodes reachable from the source
        List<Integer> reachableNodesFromSourceInProjectSelection = findReachableNodesFromSource(rProjectSelectionGraph, projectSource);

        // Sum up the scores of all nodes reachable from the source in the project selection graph
        int maxScoreFromProjectSelection = 0;
        for (int i = 0; i < reachableNodesFromSourceInProjectSelection.size(); i++) {
            maxScoreFromProjectSelection += scores[reachableNodesFromSourceInProjectSelection.get(i)];
        }

        // calculate the max total score
        int maxScore = 0;
        for (int i = 0; i < n; i++) {
            if (reachableNodesFromSource.contains(i) && !reachableNodesToSink.contains(i)) {
                maxScore += scores[i];
            }
        }

        //print the max score from project selection
        System.out.println(maxScoreFromProjectSelection+maxScore);

        float end = System.nanoTime();
        float time = end - start;
        float timeToSec = time/1000000000;

        //print the time
        // System.out.println(timeToSec);

        sc.close();
    }

    private static void addEdge(HashMap<Integer, List<Edge>> graph, int from, int to, int capacity) {
        Edge forwardEdge = new Edge(from, to, capacity);
        graph.get(from).add(forwardEdge);
    }

    // Returns the maximum flow from s to t in the given
    // graph using the Ford-Fulkerson algorithm. Created a residual graph rGraph.
    private static HashMap<Integer, List<Edge>> fordFulkerson(HashMap<Integer, List<Edge>> graph, int source, int sink) {
        // Create a residual graph and fill the residual
        // graph with given capacities in the original graph
        // as residual capacities in residual graph
    
        // Construct residual graph as a HashMap
        HashMap<Integer, List<Edge>> rGraph = new HashMap<>();
        for (int u : graph.keySet()) {
            rGraph.put(u, new ArrayList<>());
            for (Edge e : graph.get(u)) {
                Edge forwardEdge = new Edge(u, e.to, e.capacity);
                rGraph.get(u).add(forwardEdge);
            }
        }
    
        // This array is filled by BFS to store path
        int[] parent = new int[graph.size()];
    
        int max_flow = 0; // There is no flow initially. We can keep track of the flow.
    
        // Augment the flow while there is an s-t path
        while (bfs(rGraph, source, sink, parent)) {
    
            // Find minimum residual capacity of the edges
            // along the path filled by BFS. Or we can say
            // find the maximum flow through the path found.
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, findEdge(rGraph, u, v).capacity);
            }
    
            // Update residual capacities of the edges and
            // reverse edges along the path
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                findEdge(rGraph, u, v).capacity -= pathFlow;
                if (findEdge(rGraph, u, v).capacity == 0) {
                    rGraph.get(u).remove(findEdge(rGraph, u, v));
                }
                Edge reverseEdge = findEdge(rGraph, v, u);
                if (reverseEdge == null) {
                    // Add reverse edge if it doesn't exist
                    reverseEdge = new Edge(v, u, pathFlow);
                    rGraph.get(v).add(reverseEdge);
                }
                else {
                    // Update reverse edge if it exists
                    reverseEdge.capacity += pathFlow;
                }
            }
    
            // Add path flow to overall flow
            max_flow += pathFlow;
        }
    
        // Return the overall residual graph
        return rGraph;
    }
    
    // Helper method to find an edge between nodes u and v in the graph
    private static Edge findEdge(HashMap<Integer, List<Edge>> graph, int u, int v) {
        for (Edge edge : graph.get(u)) {
            if (edge.to == v) {
                return edge;
            }
        }
        return null; // This should not happen if the edge exists
    }
    
    // Returns true if there is a path from source 's' to
    // sink 't' in residual graph. Also fills parent[] to
    // store the path
    private static boolean bfs(HashMap<Integer, List<Edge>> rGraph, int s, int t, int[] parent) {
        int n = rGraph.size();
        boolean[] visited = new boolean[n];
    
        // Create a queue, enqueue source vertex
        // and mark source vertex as visited
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;
    
        // BFS loop
        while (!queue.isEmpty()) {
            int u = queue.poll();
    
            for (Edge edge : rGraph.get(u)) {
                int v = edge.to;
                if (!visited[v] && edge.capacity > 0) {
    
                    // Mark the neighbor node as visited
                    // and enqueue it
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                    
                }
            }
        }
        return (visited[t] == true);
    }    

    // Returns all nodes reachable from the source
    private static List<Integer> findReachableNodesFromSource(HashMap<Integer, List<Edge>> rGraph, int source) {
        int n = rGraph.size();
        int[] parent = new int[n];
        List<Integer> reachableNodesFromSource = new ArrayList<>();
    
        // Loop over all nodes in the graph and find the reachable nodes from the source
        // using the bfs over the residual graph rGraph
        for (int i = 0; i < n; i++) {
            if (bfs(rGraph, source, i, parent)) {
                reachableNodesFromSource.add(i);
            }
        }
    
        return reachableNodesFromSource;
    }
    
    // Returns the nodes that can reach the sink
    private static List<Integer> findReachableNodesToSink(HashMap<Integer, List<Edge>> rGraph, int sink) {
        int n = rGraph.size();
        int[] parent = new int[n];
        List<Integer> reachableNodesFromSink = new ArrayList<>();
    
        // Loop over all nodes in the graph and find the reachable nodes from the source
        // using the bfs over the residual graph rGraph
        for (int i = 0; i < n; i++) {
            if (bfs(rGraph, i, sink, parent)) {
                reachableNodesFromSink.add(i);
            }
        }
    
        return reachableNodesFromSink;
    }

    //First, create the project selection graph
    private static HashMap<Integer, List<Edge>> createProjectSelectionGraph(List<Integer> reachableNodesFromSource, List<Integer> reachableNodesToSink, int projectSource, int projectSink, int[] scores, HashMap<Integer, List<Edge>> rGraph) {

        HashMap<Integer, List<Edge>> projectSelectionGraph = new HashMap<>();

        for (Integer key : rGraph.keySet()) {
            projectSelectionGraph.put(key, new ArrayList<Edge>());
        }

        projectSelectionGraph.put(projectSource, new ArrayList<Edge>());
        projectSelectionGraph.put(projectSink, new ArrayList<Edge>()); // add new source and sink

        // add prequisite edges: if there is an edge from u to v in the residual graph, add an edge from u to v in the project selection graph
        for (Integer fromNode : rGraph.keySet()) {
            for (Edge edge : rGraph.get(fromNode)) {
                int toNode = edge.to;
                if (!reachableNodesFromSource.contains(fromNode) && !reachableNodesToSink.contains(toNode) && !reachableNodesToSink.contains(fromNode) && !reachableNodesFromSource.contains(toNode)) {
                    if (edge.capacity > 0) {
                        projectSelectionGraph.get(fromNode).add(new Edge(fromNode, toNode, Integer.MAX_VALUE));
                    }
                }
            }
        }

        // add positive edges from source and negative edges to sink
        for (Integer fromNode : rGraph.keySet()) {
            if (!reachableNodesFromSource.contains(fromNode) && !reachableNodesToSink.contains(fromNode)) {
                if (scores[fromNode] > 0) {
                    projectSelectionGraph.get(projectSource).add(new Edge(projectSource, fromNode, scores[fromNode]));
                }
                if (scores[fromNode] < 0) {
                    projectSelectionGraph.get(fromNode).add(new Edge(fromNode, projectSink, -scores[fromNode]));
                }
            }
        }
        // change to only passing in the size of the graph
        return projectSelectionGraph;

    }
    
}