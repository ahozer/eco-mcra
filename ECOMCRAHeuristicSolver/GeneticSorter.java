import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.util.Random;

import java.util.HashMap;
import java.util.LinkedHashMap;
public class GeneticSorter extends Solver{
    public ArrayList<List<Integer>> population;
    public int population_size;
    public ArrayList<Double> objective_values_of_population;
    public double current_obj_value;
    public Boolean stopping_criteria_reached;
    public double crossover_probability;
    public double mutation_probability;
    public int no_improvement_count;
    public List<Integer> current_optimal_individual;
    public long start_time;
    public int mutation_group_number;
    public int crossover_group_number;
    public GeneticSorter(List<Object> bids,int sortingMethod, int dimension, int numberOfDataCenters,
    HashMap<Integer, Integer> dataCentersPhysicalMachines, List<List<Integer>> physicalMachines,
            List<List<Double>> physicalMachineIdleFullEnergyCosts,List<Double> dataCenterEnergyCosts,List<List<Integer>> phyResourceCapacities,int index,int population_size
            ,int crossover_group_number,int mutation_group_number) {
        super(bids,sortingMethod, dimension, numberOfDataCenters, dataCentersPhysicalMachines, physicalMachines,
                physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,index);
                
                this.population=new ArrayList<List<Integer>>();
                this.population_size=population_size;
                this.objective_values_of_population=new ArrayList<Double>();
                this.current_obj_value=0;
                this.stopping_criteria_reached= false;
                this.crossover_probability=0.5;
                this.mutation_probability=0.05;
            
                this.no_improvement_count =0 ;

                this.current_optimal_individual = new  ArrayList<Integer>();
                
                this.mutation_group_number=mutation_group_number;
                this.crossover_group_number=crossover_group_number;
                 this.start_time = System.currentTimeMillis();
            }
    public  ArrayList<List<Integer>> initializePopulation() throws DeserializationException, FileNotFoundException
    {
        ArrayList<List<Integer>> initial_population=new ArrayList<List<Integer>>();
        JsonObject  linRelaxJson = (JsonObject) Jsoner.deserialize(new FileReader("linrelax_results\\linearrelax_"+Integer.toString(this.index)+".json"));
        List linRelaxList=(List) linRelaxJson.get("linear-relax-results");
        JsonObject dict =(JsonObject) linRelaxList.get(0);
        Map<String, BigDecimal>  linrelaxprodic= (Map<String, BigDecimal>)  dict.get("profit-dictionary");

        ArrayList<Integer> linrelaxprolist_accepted=new ArrayList<Integer>();
        for(String i : linrelaxprodic.keySet())
        {
            if (linrelaxprodic.get(i).intValue() ==1 )
            {
                linrelaxprolist_accepted.add(Integer.valueOf(i));
            }
        }

        ArrayList<Integer> linrelaxprolist_rejected=new ArrayList<Integer>();
        for(String i : linrelaxprodic.keySet())
        {
            if (linrelaxprodic.get(i).intValue() ==0 )
            {
                linrelaxprolist_rejected.add(Integer.valueOf(i));
            }
        }

        ArrayList<Integer> linrelaxprolist_inbetween=new ArrayList<Integer>();
        for(String i : linrelaxprodic.keySet())
        {
            if (linrelaxprodic.get(i).intValue() !=0  && linrelaxprodic.get(i).intValue() !=1)
            {
                linrelaxprolist_inbetween.add(Integer.valueOf(i));
            }
        }

        List<Integer> linrelaxprolist = Stream.of(linrelaxprolist_accepted, linrelaxprolist_inbetween, linrelaxprolist_rejected).flatMap(Collection::stream).collect(Collectors.toList()); 
        
        int length_of_accepted_bids=linrelaxprolist_accepted.size();


        for (int pop=0; pop<this.population_size-2; pop++)
        {
            List<Integer> linrelaxprolist_shuffled =this.shuffle_slice(linrelaxprolist,0,length_of_accepted_bids);

            linrelaxprolist_shuffled = this.shuffle_slice(linrelaxprolist_shuffled,length_of_accepted_bids,length_of_accepted_bids+linrelaxprolist_inbetween.size());
            
            linrelaxprolist_shuffled = this.shuffle_slice(linrelaxprolist_shuffled,length_of_accepted_bids+linrelaxprolist_inbetween.size(),linrelaxprolist.size());
            
            initial_population.add(linrelaxprolist_shuffled);
        }
        
        GreedySolver greedy_solver_create_pop_1 =new  GreedySolver(this.bids,1,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
        
        HashMap<Integer,Double> ind_sort1=   greedy_solver_create_pop_1.sortBids();

        HashMap<Integer,Double> sortedMap1 =this.sortHashMap(ind_sort1);
        
        List<Integer> keys1 = new ArrayList<>(sortedMap1.keySet());
        
        GreedySolver greedy_solver_create_pop_2 =new  GreedySolver(this.bids,2,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);

        HashMap<Integer,Double> ind_sort2=   greedy_solver_create_pop_2.sortBids();

        HashMap<Integer,Double> sortedMap2 =this.sortHashMap(ind_sort2);

        List<Integer> keys2 = new ArrayList<>(sortedMap2.keySet());
        
        initial_population.add(keys1);
        initial_population.add(keys2);

        return initial_population;
    }
    public List<Integer> shuffle_slice(List<Integer> order_list, int start, int stop) {
        if (start ==stop )
            return order_list;
        
            List<Integer> order_list_to_shuffle = new ArrayList<Integer>(order_list);

        int i = start;
        while (i < stop-1)
        { 
            Random rand = new Random();
            int randomNum = rand.nextInt(stop-i) + i;

             Collections.swap(order_list_to_shuffle, i, randomNum);
             i=i+1;
        }   

        return order_list_to_shuffle   ;
    }      


    public List<Integer> shuffle_two_bids_from_slice(List<Integer> order_list, int start, int stop) {
       
        
            List<Integer> order_list_to_shuffle = new ArrayList<Integer>(order_list);

      
            Random rand = new Random();
            int first = rand.nextInt(stop-start ) + start;
            int second = rand.nextInt(stop-start ) + start; 
            Collections.swap(order_list_to_shuffle, first, second);
           
          

        return order_list_to_shuffle   ;
    }    
    
    public double determine_objective_value_for_individual(List<Integer> individual)

    {
        GreedySolver greedy_solver = new GreedySolver(this.bids,0,this.dimension,this.numberOfDataCenters,this.dataCentersPhysicalMachines,this.physicalMachines,this.physicalMachineIdleFullEnergyCosts,this.dataCenterEnergyCosts,this.phyResourceCapacities,this.index);
        greedy_solver.allocateBidsWithGivenOrder(individual) ;
        return greedy_solver.objectiveValue;

    }
    
    public void calculate_objective_values()
    {
        this.objective_values_of_population=new ArrayList<Double>();
        for (List<Integer> individual : this.population)
        {
            double obj_value=this.determine_objective_value_for_individual(individual);
    
            this.objective_values_of_population.add(obj_value);
        }
           
    }
    
    public int determine_optimal_individual()
    {
        double max_value = Collections.max(this.objective_values_of_population) ;
        int max_index = this.objective_values_of_population.indexOf(max_value)    ;
    
        return max_index;
    }

    public int determine_worst_individual()
    {
        double min_value = Collections.min(this.objective_values_of_population) ;
        int min_index = this.objective_values_of_population.indexOf(min_value)    ;
    
        return min_index;
    } 

    public List<Integer> binary_tournament_selection(List<Double> objective_values)
    {
        //select two random index from population. Candidates are solution at those indexes.
        Random rand = new Random();
        int first_candidate = rand.nextInt(this.population.size());
        int second_candidate = rand.nextInt(this.population.size());  
       
        //return the candidate with better solution. The returned candidate will be added to the mating pool.
        if  (objective_values.get(first_candidate) >= objective_values.get(second_candidate) )
        {
            return this.population.get(first_candidate);
        }
          
        return this.population.get(second_candidate);
    
    }
    
    public List<Integer> crossover(List<Integer> parent1, List<Integer> parent2)
    {  
        if (this.crossover_group_number== parent1.size()-1)
        {
          return this.crossover_with_one_group(parent1,parent2);
        }
        else
        {
            return this.crossover_with_several_groups(parent1,parent2);
        }
       
    }   
    
    public List<Integer> crossover_with_several_groups(List<Integer> parent1, List<Integer> parent2) {
        List<Integer> child=new  ArrayList<Integer>();
        List<Integer> first_candidate=new ArrayList<Integer>(); 
        List<Integer> second_candidate=new ArrayList<Integer>(); 
        for(int i=0;i<parent1.size();i=i+this.crossover_group_number)
        {
            Random rand =	new Random();
            double probability =	rand.nextDouble();
            if (i+this.crossover_group_number <= parent1.size())
            {
                if (probability < this.crossover_probability)
                {
                    first_candidate= parent1.subList(i, i+this.crossover_group_number);
                    second_candidate= parent2.subList(i, i+this.crossover_group_number);
                }
                else
                {
                    first_candidate= parent2.subList(i, i+this.crossover_group_number);
                    second_candidate= parent1.subList(i, i+this.crossover_group_number);
                }
            }
            else
            {
                if (probability < this.crossover_probability)
                {
                    first_candidate= parent1.subList(i, parent1.size());
                    second_candidate= parent2.subList(i,  parent2.size());
                }
                else
                {
                    first_candidate= parent2.subList(i,  parent2.size());
                    second_candidate= parent1.subList(i, parent1.size());
                } 
            }
            

            for (int x : first_candidate)
            {
                if(!child.contains(x) )
                {
                     child.add(x);
                }
            }

            for (int y : second_candidate)
            {
                if(!child.contains(y) )
                {
                     child.add(y);
                }
            }
        }
        return child;
    }
    public List<Integer> crossover_with_one_group(List<Integer> parent1, List<Integer> parent2) {
        List<Integer> child=new  ArrayList<Integer>();
        int first_candidate=0;
        int second_candidate=0;
        for(int i=0;i<parent1.size();i++)
        {   
            Random rand =	new Random();
            double probability =	rand.nextDouble();
            if (probability < this.crossover_probability)
            {
                first_candidate=parent1.get(i);
                second_candidate=parent2.get(i);
            }                
            else
            {
                first_candidate=parent2.get(i);
                second_candidate=parent1.get(i);
            }
                

            if (!child.contains(first_candidate) )  
            {
                child.add(first_candidate);
            }             
            if (!child.contains(second_candidate) ) 
            {
                child.add(second_candidate);
            }
                                 
        }
        return child ;
    }
    public void mutation()
    {   
        Random rand = new Random();
        int individual_index = rand.nextInt(this.population.size());

        List<Integer> individual  = this.population.get(individual_index);
        
        for(int i=0;i<individual.size();i=i+this.mutation_group_number)
        {
            Random rand2 =	new Random();
            double probability =	rand2.nextDouble(); // for each bit , a random num. is generated.
            if ( probability < this.mutation_probability)
            { 
                if (i+this.mutation_group_number <= individual.size())
                {
                    individual=this.shuffle_two_bids_from_slice(individual,i,i+this.mutation_group_number);
                }                  
                else
                {
                    individual=this.shuffle_two_bids_from_slice(individual,i,individual.size())  ; 
                }
                    
            }
                 
        }
        
        this.population.set(individual_index, individual);     

        this.objective_values_of_population.set(individual_index,this.determine_objective_value_for_individual(individual));    
    }

   
        
    public boolean check_stopping_criteria_reached()
    {   long time_now = System.currentTimeMillis();   
        float sec = (time_now - this.start_time) / 1000F;
        if (this.no_improvement_count >= 20*this.population_size || sec>=1800 )
        return true;
        else
        return false; 
    }
    
    public void sort_main() throws FileNotFoundException, DeserializationException
    {
        int x=0;
        this.population=this.initializePopulation() ;   
        this.calculate_objective_values();

        int current_optimal_index = this.determine_optimal_individual();
        this.current_obj_value=this.objective_values_of_population.get(current_optimal_index);

        while (this.check_stopping_criteria_reached()    == false )
        {
            List<Integer> parent1=this.binary_tournament_selection(this.objective_values_of_population);
            List<Integer> parent2=this.binary_tournament_selection(this.objective_values_of_population);

            List<Integer> child=this.crossover(parent1,parent2);
            int current_worst_index =this.determine_worst_individual();
            this.population.set(current_worst_index, child);
            this.mutation() ;

            this.objective_values_of_population.set(current_worst_index,this.determine_objective_value_for_individual(child));

            int best_solution_index=this.determine_optimal_individual();

            if (this.objective_values_of_population.get(best_solution_index)> this.current_obj_value )
            {
                this.current_optimal_individual = this.population.get(best_solution_index);
                this.current_obj_value = this.objective_values_of_population.get(best_solution_index);
                this.no_improvement_count = 0;
            }
            else
            {
                this.no_improvement_count = this.no_improvement_count +1    ;
            }
          //  System.out.println(this.current_obj_value);
            x=x+1;
        }
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
