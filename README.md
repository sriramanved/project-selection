README for CS 4820 Spring 2023 Programming Assignment

## Maximum Total Score Cut in a Flow Network via Project Selection
This program solves a variation of the minimum cut problem in a flow network where each vertex in the network has an associated score. Given a flow network $G = (V, E)$, where each vertex $v \in V$ is assigned a score $r_v$, the goal is to find a minimum capacity cut $(A, B)$ such that the total score in $A$, or the summation of all nodes in $A$, is maximized.

The input format is as follows:

- The first line contains two positive integers n, m (separated by a space), which represents the number of nodes and edges in G respectively. The nodes are labeled by numbers 1, . . . , $n$. In addition, 1 is the source, and n is the sink.
- The second line contains n integers $r_1$, $r_2$, . . . , $r_n$, where each $r_i$ is the score assigned to node $i$.
- The following m lines are the $m$ edges in G. Each of the m lines contains three integers uj,vj,cj (separated by spaces), which represents an edge from $u_j$ to $v_j$ with capacity $c_j$.
The output of the program is the maximum total score $\sum_{v \in A} r_u$ (instead of the cut $(A, B)$).

## Data Structures and Algorithms
The program uses the Ford-Fulkerson algorithm to find the maximum flow in the given flow network. The algorithm repeatedly finds an augmenting path from the source to the sink and adds its flow to the overall flow of the network. To find the augmenting path, the program uses the breadth-first search (BFS) algorithm, which finds the shortest path from the source to the sink in terms of the number of edges.

To keep track of the flow in the network and to find the minimum cut, the program uses the residual graph. The residual graph is a modified version of the original graph, where each edge has a residual capacity. Initially, the residual capacity of each edge is equal to the capacity of the corresponding edge in the original graph. As the Ford-Fulkerson algorithm progresses, the residual capacity of each edge is updated to reflect the remaining capacity of the edge after the flow has been added.

To find the minimum cut in the residual graph, the program uses the depth-first search (DFS) algorithm to traverse the graph from the source and mark all the reachable vertices. The vertices that are not marked form one part of the minimum cut, and the vertices that are marked form the other part.

To maximize the total score in the minimum cut, the program sorts the vertices in the minimum cut by their scores and adds up the scores of the vertices in the larger part of the cut.

## Takeaways
This assignment explores implementations of network flow algorithms and graph traversal algorithms. These concepts are essential for solving many real-world problems in fields such as operations research, computer networking, and machine learning.
