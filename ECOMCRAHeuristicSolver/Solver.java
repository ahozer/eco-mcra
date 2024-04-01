import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class Solver {
  List<Object> bids;
  int sortingMethod;
  int dimension;
  int numberOfDataCenters;
  double objectiveValue;
  double current_obj_value;
  List<List<Integer>> physicalMachines;
  HashMap<Integer, Integer> dataCentersPhysicalMachines;
  HashMap<Integer, Double>  profitDictionary;
  List<List<Double>> physicalMachineIdleFullEnergyCosts;
  List<Integer> phyMachineAllCapacities;
  ArrayList<List<Integer>> phyMachineCapacitiesByFeatures;
  ArrayList<List<Integer>> utilizedMachineCapacities;
  double pue;
  double allocationHours;
  LinkedHashMap<Integer, Double> profitOrderedDictonary ;
  HashMap<Integer, Boolean>  allocationOfBidsResults;
  List<Double> dataCenterEnergyCosts;
  List<List<Integer>> phyResourceCapacities;
  int index;
  

    public Solver(List<Object> bids,int sortingMethod,int dimension,int numberOfDataCenters, HashMap<Integer, Integer>  dataCentersPhysicalMachines,List<List<Integer>> physicalMachines,List<List<Double>> physicalMachineIdleFullEnergyCosts ,List<Double> dataCenterEnergyCosts,List<List<Integer>> phyResourceCapacities,int index) {
      this.bids=bids; 
      this.sortingMethod=sortingMethod;
       this.dimension=dimension;
       this.numberOfDataCenters=numberOfDataCenters;
       this.objectiveValue=0;
       this.current_obj_value=0;
       this.profitDictionary= new HashMap<Integer, Double>();
       this.dataCentersPhysicalMachines=dataCentersPhysicalMachines;
       this.physicalMachines=physicalMachines;
       this.physicalMachineIdleFullEnergyCosts=physicalMachineIdleFullEnergyCosts;
       this.phyMachineAllCapacities= new ArrayList<Integer>();
       this.phyMachineCapacitiesByFeatures =new ArrayList<List<Integer>>();
       this.pue=new Double(1.57);
       this.allocationHours=new Double(720);
       this.profitOrderedDictonary = new LinkedHashMap<>();
       this.allocationOfBidsResults= new HashMap<Integer, Boolean>();
       this.utilizedMachineCapacities =new ArrayList<List<Integer>>();
       this.dataCenterEnergyCosts=dataCenterEnergyCosts;
       this.phyResourceCapacities=phyResourceCapacities;
       this.index=index;
       for (int b=0;b<this.physicalMachines.size();b++)
       {
            ArrayList<Integer> all_zeros= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              all_zeros.add(0);
            }
              this.utilizedMachineCapacities.add(all_zeros);
       }

       
      }

    public Solver(List<Object> bids2, int sortingMethod2, int dimension2, int numberOfDataCenters2,
    HashMap<Integer, Integer> dataCentersPhysicalMachines2, List<List<Integer>> physicalMachines2,
            List<List<Double>> physicalMachineIdleFullEnergyCosts2) {
    }
}
