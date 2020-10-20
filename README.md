# GraphS-system
This repository contains a our work about the "Real-time Constrained Cycle Detection in Large Dynamic Graphs" paper, which present a GraphS system to efficiently detect constrained cycles in a dynamic graph, which is changing constantly, and return the satisfying cycles in real-time.

From the paper:
### Abstract.
As graph data is prevalent for an increasing number of Internet applications, continuously monitoring structural patterns in dynamic graphs in order to generate real-time alerts
and trigger prompt actions becomes critical for many applications. In this paper, we present a new system GraphS
to efficiently detect constrained cycles in a dynamic graph,
which is changing constantly, and return the satisfying cycles
in real-time. A hot point based index is built and efficiently
maintained for each query so as to greatly speed-up query
time and achieve high system throughput. The GraphS system is developed at Alibaba to actively monitor various online fraudulent activities based on cycle detection. For a
dynamic graph with hundreds of millions of edges and vertices, the system is capable to cope with a peak rate of tens
of thousands of edge updates per second and find all the
cycles with predefined constraints with a 99.9% latency of
20 milliseconds.
