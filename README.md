# ECO-MCRA
Implementation of Energy-Aware Combinatorial Auction-Based Multidimensional Resource Allocation Model , heuristic methods and test case generator.

 The test case generator implementation can be found in TestCaseGenerator folder using python language. With specified parameters  , 3456 test cases were generated using run.py and the test cases are
 stored in test_case_bids.json file in the folder.


The MIP solver for the model is implemented and can be found in ECOMCRAGurobiSolver folder using python language. The model is formulated using Gurobi v10. Additionally, the linear relaxed version of the model 
is formulated and solved in order to use in Greedy bid sorting.

The heuristic methods are implemented in ECOMCRAHeuristicSolver folder using java language. The heuristic method implementations are seperated in classes for each method as , GreedySolver.java, GeneticSolver.java ,
AntColonyOptimization.java. The Main class is used to call each solver for all test cases. 
 
