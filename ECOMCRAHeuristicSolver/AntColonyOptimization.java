import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;

public class AntColonyOptimization extends Solver{

    public double initial_pheromone;
    public double epsilon;
    public double elitist_comp_selection_probability;
    public int no_improvement_count;
    public long start_time;
    public ArrayList<List<Integer>> population;
    public int population_size;
    public ArrayList<Double> pheromones_of_bids;
    public ArrayList<Double> objective_values_of_bids;
    public JsonArray bidss;
    public int bid_count;
    public ArrayList<Double> desirability_of_bids;
    public ArrayList<Double> objective_values_of_population;
    public double current_obj_value;
    public List<Integer> current_optimal_individual;
    public AntColonyOptimization(List<Object> bids,int sortingMethod, int dimension, int numberOfDataCenters,
    HashMap<Integer, Integer> dataCentersPhysicalMachines, List<List<Integer>> physicalMachines,
            List<List<Double>> physicalMachineIdleFullEnergyCosts,List<Double> dataCenterEnergyCosts,List<List<Integer>> phyResourceCapacities,int index,int population_size,double initial_pheromone
            ,double elitist_comp_selection_probability,double epsilon
            ) {
        super(bids,sortingMethod, dimension, numberOfDataCenters, dataCentersPhysicalMachines, physicalMachines,
                physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,index);
                this.initial_pheromone = initial_pheromone;
                this.epsilon=epsilon;
                this.elitist_comp_selection_probability=elitist_comp_selection_probability;
                this.no_improvement_count=0;
                this.start_time = System.currentTimeMillis();
                this.population=new ArrayList<List<Integer>>();
                this.population_size=population_size;
                this.pheromones_of_bids=new ArrayList<Double>();
                this.objective_values_of_bids=new ArrayList<Double>();
                this.objective_values_of_population=new ArrayList<Double>();
                this.bidss=(JsonArray)this.bids.get(0);
                this.bid_count=bidss.size();
                this.desirability_of_bids= new ArrayList<Double>();
                
                this.current_obj_value=0;
                this.current_optimal_individual = new  ArrayList<Integer>();
        for (int p=0;p<bid_count;p++)
        {
            this.pheromones_of_bids.add(new Double(this.initial_pheromone));
        }

        for (int p=0;p<bid_count;p++)
        {
            this.objective_values_of_bids.add(new Double(0));
        }
        
        for (int p=0;p<bid_count;p++)
        {
            this.desirability_of_bids.add(new Double(0));
        }
    }
    
    public int binary_tournament_selection(List<Integer> bids_left)
    {
        //select two random index from population. Candidates are solution at those indexes.
        Random rand = new Random();
        int first_candidate_index = rand.nextInt(bids_left.size());
        int second_candidate_index = rand.nextInt(bids_left.size());
        
        int first_candidate = bids_left.get(first_candidate_index);
        int second_candidate = bids_left.get(second_candidate_index);
        //return the candidate with better solution. The returned candidate will be added to the mating pool.
        if  (this.desirability_of_bids.get(first_candidate) >= this.desirability_of_bids.get(second_candidate) )
        {
            return first_candidate;
        }
          
        return second_candidate;
    
    }
    public int get_most_desirable_bid_from_all_bids(List<Integer> bids_left )
    { 
        ArrayList<Double> desirability_of_bids_left= new ArrayList<Double>();

        for (int p=0;p<this.bid_count;p++)
        {
            desirability_of_bids_left.add(new Double(-90000000));
        }

        for (int bid: bids_left)
        { 
            desirability_of_bids_left.set(bid, this.desirability_of_bids.get(bid));
        }

        double max_value = Collections.max(desirability_of_bids_left) ;
        int max_index = desirability_of_bids_left.indexOf(max_value)    ;

        return max_index;
    }
    public int elitist_component_selection(List<Integer> bids_left)
    {
        Random rand =	new Random();
        double probability =	rand.nextDouble();

        if (probability < this.elitist_comp_selection_probability)
        {
            return this.get_most_desirable_bid_from_all_bids(bids_left);
        }
        else
        {
            return this.binary_tournament_selection(bids_left);
        }
    }
    public List<Integer> create_bid_order() {
        List<Integer> bid_order=new ArrayList<Integer>();
        List<Integer> bids_left= new ArrayList<Integer>() ;
        for (int p=0;p<bid_count;p++)
        {
            bids_left.add(p);
        }
        
        int selected_bid=0;
        for (int x=0;x<bid_count;x++)
        {
           selected_bid=this.elitist_component_selection(bids_left);
           bid_order.add(selected_bid);
           bids_left.remove(bids_left.indexOf(selected_bid));
        }

        return bid_order;
    }
    
    public double set_desirability_of_bid(int bid)
    {
       double desirability_of_bid= this.pheromones_of_bids.get(bid)*Math.pow(this.objective_values_of_bids.get(bid), this.epsilon);
       this.desirability_of_bids.set(bid, desirability_of_bid);

       return desirability_of_bid;
    }
    
    public void set_all_desirabilities_of_bids()
    {
        for (int p=0;p<this.bid_count;p++)
        {
            this.desirability_of_bids.set(p,this.set_desirability_of_bid(p));
        }
    }

    public double set_objective_value_of_bid(int bid)
    {
        GreedySolver greedy_solver = new GreedySolver(this.bids,0,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
       // greedy_solver.allocateSingleBid(bidss.get(bid), bid);
      return  greedy_solver.determineBidProfitByProfitperunitAndEnergyCostForAntColony(this.bidss.get(bid));
    //    return greedy_solver.calculateObjectiveValue();
      
    }
    public void set_all_objective_value_of_all_bids()
    {
        for (int p=0;p<this.bid_count;p++)
        {
            this.objective_values_of_bids.set(p,this.set_objective_value_of_bid(p));
        }
    }

    public double determine_objective_value_for_individual(List<Integer> individual)

    {
        GreedySolver greedy_solver = new GreedySolver(this.bids,0,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
        greedy_solver.allocateBidsWithGivenOrder(individual) ;
        return greedy_solver.objectiveValue;

    }
    public int find_bid_order_with_maximum_totalvalue_from_population()
    {
        this.objective_values_of_population=new ArrayList<Double>();
        for (List<Integer> individual : this.population)
        {
            double obj_value=this.determine_objective_value_for_individual(individual);
    
           this.objective_values_of_population.add(obj_value);
        }
        
        double max_value = Collections.max(this.objective_values_of_population) ;
        int max_index = this.objective_values_of_population.indexOf(max_value)    ;
    
        return max_index;
    }

    public void decrease_pheromone_of_bid(int bid_index)
    {
        double new_pheromone=0.5*this.pheromones_of_bids.get(bid_index)+0.5*initial_pheromone;
        this.pheromones_of_bids.set(bid_index, new_pheromone);
    }

    public void increase_pheromone_of_bid(int bid_index,double total_obj_value)
    {
        double new_pheromone=0.5*this.pheromones_of_bids.get(bid_index)+0.5*total_obj_value;
        this.pheromones_of_bids.set(bid_index, new_pheromone);
    }
    public boolean check_stopping_criteria_reached()
    {   long time_now = System.currentTimeMillis();   
        float sec = (time_now - this.start_time) / 1000F;
        if (this.no_improvement_count >= 20 || sec>=1800 )
        return true;
        else
        return false; 
    }
    public void solve_main() throws FileNotFoundException, DeserializationException
    {   
        this.set_all_objective_value_of_all_bids();
        this.set_all_desirabilities_of_bids();
        int iterCount=0;
        while (this.check_stopping_criteria_reached()    == false )
        {   
            if(iterCount==0)
            {
            for (int pop=0; pop<this.population_size-1; pop++)
            {
                List<Integer> bid_order= this.create_bid_order();
                this.population.add(bid_order);
            }
            GreedySolver greedy_solver = new GreedySolver(this.bids,2,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
            HashMap<Integer,Double> ind_sort=   greedy_solver.sortBids();

            HashMap<Integer,Double> sortedMap =this.sortHashMap(ind_sort);
    
            List<Integer> keysLinRelax = new ArrayList<>(sortedMap.keySet());
            this.population.add(keysLinRelax);
            }
            else
            {
                for (int pop=0; pop<this.population_size; pop++)
                {
                    List<Integer> bid_order= this.create_bid_order();
                    this.population.add(bid_order);
                }
            }

            
           

            int best_solution_index =this.find_bid_order_with_maximum_totalvalue_from_population();
            double best_obj_value=this.objective_values_of_population.get(best_solution_index);
            List<Integer> allocated_bids =this.determine_allocated_bids_from_best_order(this.population.get(best_solution_index));
            
            if (best_obj_value> this.current_obj_value )
            {
                this.current_optimal_individual = this.population.get(best_solution_index);
                this.current_obj_value = this.objective_values_of_population.get(best_solution_index);
                this.no_improvement_count = 0;
            }
            else
            {
                this.no_improvement_count = this.no_improvement_count +1    ;
            }
            
            for (int p=0;p<bid_count;p++)
            {
                this.decrease_pheromone_of_bid(p);
            }

            for(int b:allocated_bids)
            {
                this.increase_pheromone_of_bid(b, best_obj_value);
            }

            this.population= new  ArrayList<List<Integer>>();
            iterCount=iterCount+1;

            
        }
    }

    private List<Integer> determine_allocated_bids_from_best_order(List<Integer> best_solution)
    {  
        List<Integer> allocated_bids=new ArrayList<Integer>();
        GreedySolver greedy_solver = new GreedySolver(this.bids,0,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
        greedy_solver.allocateBidsWithGivenOrder(best_solution) ;
        
        for (int p=0; p<greedy_solver.allocationOfBidsResults.size();p++)
        {
            if(greedy_solver.allocationOfBidsResults.get(p) == true)
            {
                allocated_bids.add(p);
            }
        }

        return allocated_bids;
        
    }
     

    private  LinkedHashMap<Integer, Double> sortHashMap(Map<Integer, Double> unsortedMap) {
        // Create a sorted SET
        SortedSet<Map.Entry<Integer, Double>> sortedSet = new TreeSet<>((e1, e2) -> {
          double res = e1.getValue().compareTo(e2.getValue());
            if (res == 0)
                return e2.getKey().compareTo(e1.getKey());
            return (int)res * -1;
        });
        sortedSet.addAll(unsortedMap.entrySet());
    
        
        LinkedHashMap<Integer, Double> sortedLinkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : sortedSet) sortedLinkedHashMap.put(entry.getKey(), entry.getValue());
    
        return sortedLinkedHashMap;
      }
    


    
}
