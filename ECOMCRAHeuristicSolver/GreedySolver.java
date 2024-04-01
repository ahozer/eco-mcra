import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
public class GreedySolver extends Solver{

    private List<Double> offered_prices_of_bids;
    private HashMap<String, Double> amount_of_requested_resources_mapped_to_phymachines;
    public double total_price;
    public double total_idle_cost;
    public double total_utilization_cost;
    public GreedySolver(List<Object> bids,int sortingMethod, int dimension, int numberOfDataCenters,
    HashMap<Integer, Integer> dataCentersPhysicalMachines, List<List<Integer>> physicalMachines,
            List<List<Double>> physicalMachineIdleFullEnergyCosts,List<Double> dataCenterEnergyCosts,List<List<Integer>> phyResourceCapacities,int index) {
        super(bids,sortingMethod, dimension, numberOfDataCenters, dataCentersPhysicalMachines, physicalMachines,
                physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,index);

        this.offered_prices_of_bids=this.calculate_bids_prices(); 
        this.amount_of_requested_resources_mapped_to_phymachines=new HashMap<String,Double>()   ;   
        this.total_price=new Double(0);
        this.total_idle_cost=new Double(0);
        this.total_utilization_cost=new Double(0);
        JsonArray bidss=(JsonArray)this.bids.get(0);   
        for(int k=0;k<bidss.size();k++)
        {
          ArrayList<Object> bidArray=(ArrayList<Object>) bidss.get(k); 
          int amountOfSubbids=bidArray.size()-1;
          for(int l=0;l<amountOfSubbids;l++)
          {
              ArrayList<Object> subbidArray=(ArrayList<Object>)   bidArray.get(l);
              int amount_of_substituble_resources = subbidArray.size()-1;
                for(int m=0;m<amount_of_substituble_resources;m++)
                {
                    for(int i=0;i<physicalMachines.size();i++)
                    {
                      
                      String str_key=Integer.toString(k) +"-"+Integer.toString(l) +"-"+Integer.toString(m) +"-"+Integer.toString(i) ;
                      this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, new Double(0));
                    } 
                }  
          }
      
     
        }

       
    }

   
    public double determineBidProfitByProfitperunitAndEnergyCostForAntColony(Object bid)
    {
      for(int a=0;a<this.dimension;a++)
      {
            for (List<Integer> machine : this.physicalMachines) {
              this.phyMachineAllCapacities.add(machine.get(a));
          }
      }
         
     for (int i=0;i<this.phyMachineAllCapacities.size()-this.physicalMachines.size()+1;i+=this.physicalMachines.size())
     {
      this.phyMachineCapacitiesByFeatures.add(this.phyMachineAllCapacities.subList(i, i+this.physicalMachines.size()));
     }  

      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      double pricePerUnit=this.determineBidProfitByProfitperunit(bid);
      double sumOfReplacableCpus=0;
      double totalCpu =0;
      
      for (int i=0; i<lengthOfBid-1; i++ )
      {
          ArrayList<Object> subbidArray=(ArrayList<Object>)   bidArray.get(i);
          int lengthOfSubbid= subbidArray.size();
          sumOfReplacableCpus=0;
          for (int j=0; j<lengthOfSubbid-1;j++)
          {
            ArrayList<Object> partsOfSubbid=(ArrayList<Object>)   subbidArray.get(j) ;
            BigDecimal valueBD = (BigDecimal) partsOfSubbid.get(0);
            BigDecimal value2= (BigDecimal) subbidArray.get(lengthOfSubbid-1);
            double normalizedCapValue=this.normalizeCapacityValue((valueBD.intValue())*(value2.intValue()), 0);
            sumOfReplacableCpus=sumOfReplacableCpus+normalizedCapValue;
          }
          
          double averageOfSubbid= sumOfReplacableCpus/(lengthOfSubbid-1);
          totalCpu=totalCpu+averageOfSubbid;

      }

      List<Double> energyCosts=new ArrayList<Double>();

          for(int x=0;x<this.physicalMachines.size();x++)
          {
            double energyCostForPhysicalMachine=this.determineEnergyCostForSinglePhysicalMachine(totalCpu,x)  ;
            energyCosts.add(energyCostForPhysicalMachine);
          }
      
          double sumOfEnergyCosts=0;
      for (double sum:energyCosts)
      {
        sumOfEnergyCosts=sumOfEnergyCosts+sum;
      }

      double averageOfEnergyCosts=sumOfEnergyCosts/energyCosts.size()  ;    
      
      return pricePerUnit-averageOfEnergyCosts;

    
    }

    public HashMap<Integer, Double> sortBids() throws FileNotFoundException, DeserializationException
    {
       
        for(int a=0;a<this.dimension;a++)
        {
              for (List<Integer> machine : this.physicalMachines) {
                this.phyMachineAllCapacities.add(machine.get(a));
            }
        }
           
       for (int i=0;i<this.phyMachineAllCapacities.size()-this.physicalMachines.size()+1;i+=this.physicalMachines.size())
       {
        this.phyMachineCapacitiesByFeatures.add(this.phyMachineAllCapacities.subList(i, i+this.physicalMachines.size()));
       }  


      if(this.sortingMethod ==0 )
            return this.sortBidsWithProfitByUnit();

      if( this.sortingMethod ==1)
           return this.sortBidsWithProfitByUnitAndEnergyCost()    ;

      if( this.sortingMethod ==2)
           return this.sortBidsWithProfitByLinearRelaxation()    ;   
       return null;     
    }
    
    public HashMap<Integer, Double> shuffleBids() 
    {
       
        for(int a=0;a<this.dimension;a++)
        {
              for (List<Integer> machine : this.physicalMachines) {
                this.phyMachineAllCapacities.add(machine.get(a));
            }
        }
           
       for (int i=0;i<this.phyMachineAllCapacities.size()-this.physicalMachines.size()+1;i+=this.physicalMachines.size())
       {
        this.phyMachineCapacitiesByFeatures.add(this.phyMachineAllCapacities.subList(i, i+this.physicalMachines.size()));
       }  


       int bidIndex=0;
       JsonArray bidss= (JsonArray)this.bids.get(0);
       for (Object bid : bidss)
       {   
           Random rand = new Random();
           double randomValue = 0 + (1000) * rand.nextDouble();
           this.profitDictionary.put(bidIndex,randomValue);
           bidIndex=bidIndex+1;
       }

       return this.profitDictionary;
    }
    
    public HashMap<Integer, Double> sortBidsWithProfitByLinearRelaxation  () throws DeserializationException, FileNotFoundException
    {
      JsonObject  linRelaxJson = (JsonObject) Jsoner.deserialize(new FileReader("linrelax_results\\linearrelax_"+Integer.toString(this.index)+".json"));
      List linRelaxList=(List) linRelaxJson.get("linear-relax-results");
      JsonObject dict =(JsonObject) linRelaxList.get(0);
      Map<String, BigDecimal>  pro_dic= (Map<String, BigDecimal>)  dict.get("profit-dictionary");
      HashMap<Integer, Double> pro2= new HashMap<Integer, Double>();
      for (int p=0;p<pro_dic.size();p++)
        {
          BigDecimal prBD=(BigDecimal) pro_dic.get(String.valueOf(p));
          pro2.put(p, prBD.doubleValue());
        }

      this.profitDictionary = pro2;
      if(this.profitDictionary != null)
      {
        JsonArray bidss=(JsonArray)this.bids.get(0);
        for(int k=0;k<bidss.size();k++)
        {
          this.profitDictionary.put(k,this.profitDictionary.get(k)*this.determineBidProfitByProfitperunit(bidss.get(k)));

        }
      }
      return this.profitDictionary;
    }
    
    public HashMap<Integer, Double> sortBidsWithProfitByUnit()
    {
        int bidIndex=0;
        JsonArray bidss= (JsonArray)this.bids.get(0);
        for (Object bid : bidss)
        {
            double profitOfTheBid=this.determineBidProfitByProfitperunit(bid);
            this.profitDictionary.put(bidIndex,profitOfTheBid);
            bidIndex=bidIndex+1;
        }

        return this.profitDictionary;
    }
    
   

    public double determineBidProfitByProfitperunit(Object bid)
    {
        ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
        int lengthOfBid=bidArray.size();
        
        BigDecimal prBidDec= (BigDecimal) bidArray.get(lengthOfBid-1);
        
        int priceOffered=prBidDec.intValue();
        ArrayList<Double> sumOfDimensions= new ArrayList<Double>();
  
        for (int p=0;p<this.dimension;p++)
        {
          sumOfDimensions.add(new Double(0));
        }
  
        List<List<Double>> averageOfOrs=new ArrayList<List<Double>>();
      
        for (int i=0; i<lengthOfBid-1; i++ )
        {
          ArrayList<Object> subbidArray=(ArrayList<Object>)   bidArray.get(i);
          int lengthOfSubbid= subbidArray.size();
            

          for (int j=0; j<lengthOfSubbid-1;j++)
          {
              ArrayList<Object> partsOfSubbid=(ArrayList<Object>)   subbidArray.get(j) ;
              for(int k=0;k<this.dimension;k++)
              {
                
                BigDecimal valueBD = (BigDecimal) partsOfSubbid.get(k);
                BigDecimal value2= (BigDecimal) subbidArray.get(lengthOfSubbid-1);
                 double normalizedCapValue=this.normalizeCapacityValue((valueBD.intValue())*(value2.intValue()), k);
                  sumOfDimensions.set(k, sumOfDimensions.get(k)+normalizedCapValue);
              }
          }
          
          List<Double> doublelist=new ArrayList<Double>();
          for (int b=0;b<this.dimension;b++)
          {
            doublelist.add(sumOfDimensions.get(b)/(double)(lengthOfSubbid-1));
          }
          averageOfOrs.add(doublelist);
  
          for (int a=0;a<this.dimension;a++)
          {
              sumOfDimensions.set(a,new Double(0));
          }
        }
  
        ArrayList<Double> sumOfAllcapacities= new ArrayList<Double>();
  
        for (int p=0;p<this.dimension;p++)
        {
          sumOfAllcapacities.add(new Double(0));
        }
  
        for (int x=0;x<this.dimension;x++)
        {
         for(int y=0;y<averageOfOrs.size();y++)
         {
          sumOfAllcapacities.set(x, sumOfAllcapacities.get(x)+averageOfOrs.get(y).get(x));
         }
        }
        
        double sumOfAll=0;
        for (double sum:sumOfAllcapacities)
        {
          sumOfAll=sumOfAll+sum;
        }
  
        double averageOfAll=sumOfAll/(double)this.dimension  ;
  
        return priceOffered/averageOfAll;
    }


    public double normalizeCapacityValue(int value,int index)
    {
      List<Integer> maxCapacityValues=new ArrayList<Integer>();
      for (List<Integer> cap: this.phyMachineCapacitiesByFeatures)
      {
        maxCapacityValues.add(Collections.max(cap));
      }
      
      double normalizedValue= (double)value/(double) maxCapacityValues.get(index);
      return normalizedValue;
    }

    public HashMap<Integer, Double> sortBidsWithProfitByUnitAndEnergyCost()
    {
        int bidIndex=0;
        JsonArray bidss=(JsonArray)this.bids.get(0);
        for (Object bid : bidss)
        {
          double profitOfTheBid=this.determineBidProfitByProfitperunitAndEnergyCost(bid);
            this.profitDictionary.put(bidIndex,profitOfTheBid);
            bidIndex=bidIndex+1;
        }

        return this.profitDictionary;
    }
    
    public double determineBidProfitByProfitperunitAndEnergyCost(Object bid)
    {
      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      double pricePerUnit=this.determineBidProfitByProfitperunit(bid);
      double sumOfReplacableCpus=0;
      double totalCpu =0;
      
      for (int i=0; i<lengthOfBid-1; i++ )
      {
          ArrayList<Object> subbidArray=(ArrayList<Object>)   bidArray.get(i);
          int lengthOfSubbid= subbidArray.size();
          sumOfReplacableCpus=0;
          for (int j=0; j<lengthOfSubbid-1;j++)
          {
            ArrayList<Object> partsOfSubbid=(ArrayList<Object>)   subbidArray.get(j) ;
            BigDecimal valueBD = (BigDecimal) partsOfSubbid.get(0);
            BigDecimal value2= (BigDecimal) subbidArray.get(lengthOfSubbid-1);
            double normalizedCapValue=this.normalizeCapacityValue((valueBD.intValue())*(value2.intValue()), 0);
            sumOfReplacableCpus=sumOfReplacableCpus+normalizedCapValue;
          }
          
          double averageOfSubbid= sumOfReplacableCpus/(lengthOfSubbid-1);
          totalCpu=totalCpu+averageOfSubbid;

      }

      List<Double> energyCosts=new ArrayList<Double>();

          for(int x=0;x<this.physicalMachines.size();x++)
          {
            double energyCostForPhysicalMachine=this.determineEnergyCostForSinglePhysicalMachine(totalCpu,x)  ;
            energyCosts.add(energyCostForPhysicalMachine);
          }
      
          double sumOfEnergyCosts=0;
      for (double sum:energyCosts)
      {
        sumOfEnergyCosts=sumOfEnergyCosts+sum;
      }

      double averageOfEnergyCosts=sumOfEnergyCosts/energyCosts.size()  ;    
      
      return pricePerUnit-averageOfEnergyCosts;

    
    }

    public double determineEnergyCostForSinglePhysicalMachine(double totalCpu, int phyMacIndex)
    {
      double utilizationCost=(this.physicalMachineIdleFullEnergyCosts.get(phyMacIndex).get(1)/1000)-(this.physicalMachineIdleFullEnergyCosts.get(phyMacIndex).get(0)/1000);
      return utilizationCost*totalCpu/this.phyMachineCapacitiesByFeatures.get(0).get(phyMacIndex);
    }

    public void allocateBids()
    {
   //   this.profitDictionary.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .forEachOrdered(x -> this.profitOrderedDictonary.put(x.getKey(), x.getValue()));
   this.profitOrderedDictonary=  this.sortHashMap(this.profitDictionary);
   JsonArray bidss=(JsonArray)this.bids.get(0);
     
      for (int bid_index:this.profitOrderedDictonary.keySet())
      {
    
        boolean isAccepted=this.allocateSingleBid(bidss.get(bid_index),bid_index);
        this.allocationOfBidsResults.put(bid_index, isAccepted);
        this.current_obj_value=this.calculateObjectiveValue();
        
        
      }
      this.objectiveValue=this.current_obj_value;
    }
    
    public void simulateAllocationRandom()
    {
   //   this.profitDictionary.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) .forEachOrdered(x -> this.profitOrderedDictonary.put(x.getKey(), x.getValue()));
     this.profitOrderedDictonary=  this.sortHashMap(this.profitDictionary);
    JsonArray bidss=(JsonArray)this.bids.get(0);
     
      for (int bid_index:this.profitOrderedDictonary.keySet())
      {
    
        boolean isAccepted=this.allocateSimulationSingleBidRandom(bidss.get(bid_index),bid_index);
        this.allocationOfBidsResults.put(bid_index, isAccepted);
        this.current_obj_value=this.calculateObjectiveValue();
        
        
      }
      this.objectiveValue=this.current_obj_value;
    }

    public void allocateBidsWithGivenOrder(List<Integer> bidOrder)
    {
      JsonArray bidss=(JsonArray)this.bids.get(0);
      for (int bid_index:bidOrder)
      {
        boolean isAccepted=this.allocateSingleBid(bidss.get(bid_index),bid_index);
        this.allocationOfBidsResults.put(bid_index, isAccepted);
        this.current_obj_value=this.calculateObjectiveValue();
        
      }
      this.objectiveValue=this.current_obj_value;
    }

    public double calculateObjectiveValue() {
      return this.calculateTotalPriceOfAcceptedBids()-this.calculateTotalEnergyCost(this.utilizedMachineCapacities);
    }

    public boolean allocateSingleBid(Object bid,int bid_index)
    {
      if (this.numberOfDataCenters== 1)
        return this.allocateSingleBidInSingleDataCenter(bid,bid_index);
      else
        return this.allocateSingleBidInMultipleDataCenters(bid,bid_index) ;   
    }

    public boolean allocateSimulationSingleBidRandom(Object bid,int bid_index)
    {
      if (this.numberOfDataCenters== 1)
        return this.allocateSimulationSingleBidInSingleDataCenterRandom(bid,bid_index);
      else
        return this.allocateSimulationSingleBidInMultipleDataCentersRandom(bid,bid_index) ;   
    }

    public boolean allocateSimulationSingleBidInSingleDataCenterRandom(Object bid, int bid_index) 
    {
      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

       for(int i=0; i<lengthOfBid-1 ; i++)
       {
           ArrayList<Object> subbidArray=(ArrayList<Object>) bidArray.get(i);  
           int lengthOfSubbid=subbidArray.size();
           BigDecimal remaining_quantityDB=(BigDecimal)subbidArray.get(lengthOfSubbid-1);
           int remaining_quantity =remaining_quantityDB.intValue();
          
          int z=0;
            while (remaining_quantity>0 && z<lengthOfSubbid-1  )
            {
              int result=this.trySimulateAllocateFeatureMatrixSingleDataCenterRandomPart2(subbidArray.get(z));                    
              if( result !=-1)
              {
                remaining_quantity=remaining_quantity-1;
              //  self.allocations[bid_index].append(result)
              String str_key=Integer.toString(bid_index)+"-"+Integer.toString(i)+"-"+Integer.toString(z)+"-"+Integer.toString(result);
              this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, this.amount_of_requested_resources_mapped_to_phymachines.get(str_key)+1);
            }                 
              else
              {
                z=z+1 ;
              }
                 
            }
             if( remaining_quantity!= 0)
             {
           //   self.allocations[bid_index].append("rejected")     
              this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;      
              this.emptyAllocationForBid(bid_index);  
              return false;   
             }
                            
       }
       
            return true;        
      
    }

    public boolean allocateSingleBidInSingleDataCenter(Object bid, int bid_index) 
    {
      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

       for(int i=0; i<lengthOfBid-1 ; i++)
       {
           ArrayList<Object> subbidArray=(ArrayList<Object>) bidArray.get(i);  
           int lengthOfSubbid=subbidArray.size();
           BigDecimal remaining_quantityDB=(BigDecimal)subbidArray.get(lengthOfSubbid-1);
           int remaining_quantity =remaining_quantityDB.intValue();
           HashMap<Integer, Double> energiesMap= new HashMap<Integer, Double>();
          for (int j=0; j<lengthOfSubbid-1 ; j++)
          {     
          
            Double minEnergy = new Double(Optional.ofNullable(this.tryAllocateFeatureMatrixSingleDataCenterPart1(subbidArray.get(j)))
                .orElse(new Double(-1)) ); 
                if( minEnergy != -1 )
                energiesMap.put(j, minEnergy);
          }

          Map<Integer, Double> sortByValueMapEnergies = energiesMap.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
                       (entry1, entry2) -> entry2, LinkedHashMap::new));
          
          
          Set<Integer> keysList = sortByValueMapEnergies.keySet();

          List<Integer> keysListArray = new ArrayList<>(keysList);
          int z=0;
            while (remaining_quantity>0 && z<keysListArray.size() )
            {
              int result=this.tryAllocateFeatureMatrixSingleDataCenterPart2(subbidArray.get(keysListArray.get(z)));                    
              if( result !=-1)
              {
                remaining_quantity=remaining_quantity-1;
              //  self.allocations[bid_index].append(result)
              String str_key=Integer.toString(bid_index)+"-"+Integer.toString(i)+"-"+Integer.toString(keysListArray.get(z))+"-"+Integer.toString(result);
              this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, this.amount_of_requested_resources_mapped_to_phymachines.get(str_key)+1);
            }                 
              else
              {
                z=z+1 ;
              }
                 
            }
             if( remaining_quantity!= 0)
             {
           //   self.allocations[bid_index].append("rejected")     
              this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;      
              this.emptyAllocationForBid(bid_index);  
              return false;   
             }
                            
       }
       
      if (this.increaseProfit(bid_index,lengthOfBid))
            return true;        
      
      else
      {
        
        this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;    
        this.emptyAllocationForBid(bid_index);  
        return false;  
      }
       


    }

    public void emptyAllocationForBid(int bid_index)
    {
      JsonArray bidss =(JsonArray) this.bids.get(0);
            ArrayList<Object> bidArray=(ArrayList<Object>) bidss.get(bid_index); 
      int amountOfSubbids=bidArray.size()-1;
      for (int l=0;l<amountOfSubbids;l++)
       {
              ArrayList<Object> subbidArray=(ArrayList<Object>)   bidArray.get(l);
              int amount_of_substituble_resources = subbidArray.size()-1;

              for(int m=0;m<amount_of_substituble_resources;m++)
                {
                    for(int i=0;i<physicalMachines.size();i++)
                    {
                      
                      String str_key=Integer.toString(bid_index) +"-"+Integer.toString(l) +"-"+Integer.toString(m) +"-"+Integer.toString(i) ;
                      this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, new Double(0));
                    } 
                }  
       }
    }

    private int tryAllocateFeatureMatrixSingleDataCenterPart2(Object featureMatrix) {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  

      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
       
        ArrayList<Integer> possibleAllocations=new ArrayList<Integer>();

        for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
        {
          for (int i=0; i<this.dimension ; i++)
          {
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(machine_index).get(i)+ item <= this.physicalMachines.get(machine_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              possibleAllocations.add(machine_index);
        }
            
                
        if (possibleAllocations.size() == 0 )
            return -1;
        else
        {
          int allocated_pm=0;
          double minEnergyCost=100000000;
          HashMap<Integer, Double> energy_costs=new HashMap<Integer, Double>(); 
          for (int possible_pm : possibleAllocations)
          {
            ArrayList<Integer> util=new ArrayList<Integer>();
            for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma2 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma2.intValue();
              util.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i)+item);
            }
            
            utilizedMachineCapacities_tmp.set(possible_pm, util);

            double total_energy_Cost= this.calculateTotalEnergyCost(utilizedMachineCapacities_tmp) ;
            energy_costs.put(possible_pm,total_energy_Cost);
            
           
            if (total_energy_Cost< minEnergyCost)
            {
              allocated_pm=possible_pm;
              minEnergyCost= total_energy_Cost;
            }
            
            ArrayList<Integer> util2=new ArrayList<Integer>();
            for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma3 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma3.intValue();
              util2.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i) - item);
            }
            utilizedMachineCapacities_tmp.set(possible_pm, util2);
          }
          
          ArrayList<Integer> util3=new ArrayList<Integer>();

          for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma4 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma4.intValue();
              util3.add(this.utilizedMachineCapacities.get(allocated_pm).get(i)+item);
            }

            /*Map.Entry<Integer, Double> min = Collections.min(energy_costs.entrySet(),
            Map.Entry.comparingByValue());
            allocated_pm=min.getKey();  */
          this.utilizedMachineCapacities.set(allocated_pm,util3);
          
          

          return allocated_pm;

        }
    }
    private int trySimulateAllocateFeatureMatrixSingleDataCenterRandomPart2(Object featureMatrix) {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  

      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
        int allocated_pm=-1;
       

        for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
        {
          for (int i=0; i<this.dimension ; i++)
          {
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(machine_index).get(i)+ item <= this.physicalMachines.get(machine_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              allocated_pm =machine_index;
              break;
        }
            
                
        if (allocated_pm == -1)
            return -1;
        else
        {
        
          ArrayList<Integer> util3=new ArrayList<Integer>();

          for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma4 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma4.intValue();
              util3.add(this.utilizedMachineCapacities.get(allocated_pm).get(i)+item);
            }

            /*Map.Entry<Integer, Double> min = Collections.min(energy_costs.entrySet(),
            Map.Entry.comparingByValue());
            allocated_pm=min.getKey();  */
          this.utilizedMachineCapacities.set(allocated_pm,util3);
          
          

          return allocated_pm;

        }
    }
    public Double tryAllocateFeatureMatrixSingleDataCenterPart1(Object featureMatrix)
    {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  

      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
       
        ArrayList<Integer> possibleAllocations=new ArrayList<Integer>();

        for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
        {
          for (int i=0; i<this.dimension ; i++)
          { 
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(machine_index).get(i)+ item <= this.physicalMachines.get(machine_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              possibleAllocations.add(machine_index);
        }
            
                
        if (possibleAllocations.size() == 0 )
            return null;
        else
        {
          HashMap<Integer, Double> energy_costs=new HashMap<Integer, Double>(); 
          for (int possible_pm : possibleAllocations)
          {
            ArrayList<Integer> util=new ArrayList<Integer>();
            for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma2 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma2.intValue();
              util.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i)+item);
            }
            
            utilizedMachineCapacities_tmp.set(possible_pm, util);

            energy_costs.put(possible_pm,this.calculateTotalEnergyCost(utilizedMachineCapacities_tmp));

            ArrayList<Integer> util2=new ArrayList<Integer>();
            for (int i = 0 ; i<this.dimension ; i++)
            {
              BigDecimal fma3 = (BigDecimal) featureMatrixArray.get(i);
              int item= fma3.intValue();
              util2.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i) - item);
            }
            utilizedMachineCapacities_tmp.set(possible_pm, util2);
          }
             

          
          return Collections.min(energy_costs.values());
        }
           
    }
    public Double calculateTotalEnergyCost(ArrayList<List<Integer>> capacitiesInUse)
    {
      ArrayList<Double> physicalMachineEnergyCosts=new ArrayList<Double>();
      for (int i=0; i<capacitiesInUse.size();i++)
      {
        physicalMachineEnergyCosts.add(new Double(0));
      }
      
      this.total_idle_cost =0;
      this.total_utilization_cost=0;
      for (int physical_machine_used=0 ; physical_machine_used <capacitiesInUse.size() ; physical_machine_used++)
      {
        if (capacitiesInUse.get(physical_machine_used).get(0) != 0) 
        {
          Double idle_cost=this.determineIdleCostForPhysical_machine(physical_machine_used);
          Double utilization_cost=this.determineUtilizationCostForPhysicalMachine(physical_machine_used,capacitiesInUse);
          Double physical_machine_energy_cost=idle_cost+utilization_cost;
          this.total_idle_cost=this.total_idle_cost+idle_cost;
          this.total_utilization_cost=this.total_utilization_cost+utilization_cost;
          physicalMachineEnergyCosts.set(physical_machine_used,physical_machine_energy_cost);  
        }
      }

      return this.getSumDouble(physicalMachineEnergyCosts);
           
    }

    public Double determineUtilizationCostForPhysicalMachine(int machine_index,ArrayList<List<Integer>> capacitiesInUse)
    {
      int data_center_pm_in=this.dataCentersPhysicalMachines.get(machine_index);  
      double energy_cost_in_data_center=this.dataCenterEnergyCosts.get(data_center_pm_in);
      double util_energy=(this.physicalMachineIdleFullEnergyCosts.get(machine_index).get(1)/1000)-(this.physicalMachineIdleFullEnergyCosts.get(machine_index).get(0)/1000) ;  
      double cpu_used_ratio=((double)capacitiesInUse.get(machine_index).get(0))/((double)this.physicalMachines.get(machine_index).get(0));
      
      return util_energy*cpu_used_ratio*this.pue*this.allocationHours*energy_cost_in_data_center;
    }

    public Double determineIdleCostForPhysical_machine(int machine_index) {
      int data_center_pm_in=this.dataCentersPhysicalMachines.get(machine_index);  
      double energy_cost_in_data_center=this.dataCenterEnergyCosts.get(data_center_pm_in);

      return (this.physicalMachineIdleFullEnergyCosts.get(machine_index).get(0)/1000)*this.pue*this.allocationHours*energy_cost_in_data_center ;

    }
    
    public Boolean increaseProfit(int bidIndex,int lengthOfBid)
    {
      JsonArray bidss=(JsonArray)this.bids.get(0);
      BigDecimal priceOfBidBigDecimal=(BigDecimal)((List<Object>)bidss.get(bidIndex)).get(lengthOfBid-1);
      double objValifAccepted=(double)priceOfBidBigDecimal.intValue() +this.calculateTotalPriceOfAcceptedBids()- this.calculateTotalEnergyCost(this.utilizedMachineCapacities);
        if (this.current_obj_value<=objValifAccepted)
           return true  ;  
        else
            return false ;  
    }

    public double calculateTotalPriceOfAcceptedBids() {
      this.total_price=0;
      JsonArray bidss=(JsonArray)this.bids.get(0);
    
      for (int bid_index : this.allocationOfBidsResults.keySet())
      {
        if (this.allocationOfBidsResults.get(bid_index) == true)
        {
          ArrayList<Object> bidArray=(ArrayList<Object>) bidss.get(bid_index); 
          int index_of_price=bidArray.size()-1;
          BigDecimal priceBD = (BigDecimal) bidArray.get(index_of_price);
          double price= priceBD.doubleValue();
          this.total_price=this.total_price + price;
        }
            
      }

          

      return this.total_price ;
    }

    public boolean allocateSingleBidInMultipleDataCenters(Object bid, int bid_index)
     {
      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

       for(int i=0; i<lengthOfBid-1 ; i++)
       {
           ArrayList<Object> subbidArray=(ArrayList<Object>) bidArray.get(i);  
           int lengthOfSubbid=subbidArray.size();
           BigDecimal remaining_quantityBD=(BigDecimal)subbidArray.get(lengthOfSubbid-1);
           int remaining_quantity =remaining_quantityBD.intValue();
           HashMap<Integer, Double> energiesMap= new HashMap<Integer, Double>();
          for (int j=0; j<lengthOfSubbid-1 ; j++)
          {     
            
                
            Double minEnergy = new Double(Optional.ofNullable(this.tryAllocateFeatureMatrixMultipleDataCentersPart1(subbidArray.get(j)))
            .orElse(new Double(-1)) ); 
            if( minEnergy != -1 )
                energiesMap.put(j, minEnergy);
          }

          Map<Integer, Double> sortByValueMapEnergies = energiesMap.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),
                        (entry1, entry2) -> entry2, LinkedHashMap::new));
          
          Set<Integer> keysList = sortByValueMapEnergies.keySet();

          List<Integer> keysListArray = new ArrayList<>(keysList);
          int z=0;
            while (remaining_quantity>0 && z<keysListArray.size() )
            {
              int result=this.tryAllocateFeatureMatrixMultipleDataCentersPart2(subbidArray.get(keysListArray.get(z)));                    
              if( result !=-1)
              {
                remaining_quantity=remaining_quantity-1;
              //  self.allocations[bid_index].append(result)
                String str_key=Integer.toString(bid_index)+"-"+Integer.toString(i)+"-"+Integer.toString(keysListArray.get(z))+"-"+Integer.toString(result);
                this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, this.amount_of_requested_resources_mapped_to_phymachines.get(str_key)+1);
        
              }                 
              else
              {
                z=z+1 ;
              }
                 
            }
             if( remaining_quantity!= 0)
             {
           //   self.allocations[bid_index].append("rejected")     
                this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;      
                this.emptyAllocationForBid(bid_index);         
              return false;   
             }
                            
       }
       
      if (this.increaseProfit(bid_index,lengthOfBid))
            return true;        
      
      else
      {
        
        this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;    
        this.emptyAllocationForBid(bid_index);   
        return false;  
      }
       
    }

    public boolean allocateSimulationSingleBidInMultipleDataCentersRandom(Object bid, int bid_index)
     {
      ArrayList<Object> bidArray=(ArrayList<Object>) bid;  
      int lengthOfBid=bidArray.size();
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

       for(int i=0; i<lengthOfBid-1 ; i++)
       {
           ArrayList<Object> subbidArray=(ArrayList<Object>) bidArray.get(i);  
           int lengthOfSubbid=subbidArray.size();
           BigDecimal remaining_quantityBD=(BigDecimal)subbidArray.get(lengthOfSubbid-1);
           int remaining_quantity =remaining_quantityBD.intValue();
     
          
          int z=0;
            while (remaining_quantity>0 && z<lengthOfSubbid-1)
            {
              int result=this.tryAllocateSimulationFeatureMatrixMultipleDataCentersRandomPart2(subbidArray.get(z));                    
              if( result !=-1)
              {
                remaining_quantity=remaining_quantity-1;
              //  self.allocations[bid_index].append(result)
                String str_key=Integer.toString(bid_index)+"-"+Integer.toString(i)+"-"+Integer.toString(z)+"-"+Integer.toString(result);
                this.amount_of_requested_resources_mapped_to_phymachines.put(str_key, this.amount_of_requested_resources_mapped_to_phymachines.get(str_key)+1);
        
              }                 
              else
              {
                z=z+1 ;
              }
                 
            }
             if( remaining_quantity!= 0)
             {
           //   self.allocations[bid_index].append("rejected")     
                this.utilizedMachineCapacities=utilizedMachineCapacities_tmp;      
                this.emptyAllocationForBid(bid_index);         
              return false;   
             }
                            
       }
       
      
            return true;        
      
     
       
    }


    
    private int tryAllocateFeatureMatrixMultipleDataCentersPart2(Object featureMatrix) {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  
      int lengthOfFeatureMatrix= featureMatrixArray.size();
      
      ArrayList<Object> dataCenterFeatures =new ArrayList<>();
      for (int x=lengthOfFeatureMatrix-this.numberOfDataCenters ; x<lengthOfFeatureMatrix ; x++)
      {
          dataCenterFeatures.add(featureMatrixArray.get(x));
      }
      
      ArrayList<Integer> demandedDataCenter =new ArrayList<>();
      for(int i=0 ; i< dataCenterFeatures.size()  ; i++)
      {
        BigDecimal df =(BigDecimal)dataCenterFeatures.get(i);
        if (df.intValue() == 1)
        {
               demandedDataCenter.add(i);
        }
      }
      
      int ddc ;
      if (demandedDataCenter.size() >0 )
      {
          ddc=demandedDataCenter.get(0);
      }
      else
      {
        return -1;
      }
      
      ArrayList<Integer> feasiblePms =new ArrayList<>();
      for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
      {
        if (this.dataCentersPhysicalMachines.get(machine_index)== ddc)
        {
             feasiblePms.add(machine_index);
        }
      } 
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
       
        ArrayList<Integer> possibleAllocations=new ArrayList<Integer>();

        for( int mac_index : feasiblePms)
        {
          for (int i=0; i<this.dimension ; i++)
          {
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(mac_index).get(i)+ item <= this.physicalMachines.get(mac_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              possibleAllocations.add(mac_index);
        }

        if (possibleAllocations.size() == 0 )
        return -1;
    else
    {
      int allocated_pm=0;
      double minEnergyCost=100000000;
      HashMap<Integer, Double> energy_costs=new HashMap<Integer, Double>(); 
      for (int possible_pm : possibleAllocations)
      {
        ArrayList<Integer> util=new ArrayList<Integer>();
        for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma2 = (BigDecimal) featureMatrixArray.get(i);
            int item= fma2.intValue();
          util.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i)+item);
        }
        
        utilizedMachineCapacities_tmp.set(possible_pm, util);

        double total_energy_Cost = this.calculateTotalEnergyCost(utilizedMachineCapacities_tmp) ;

        energy_costs.put(possible_pm,total_energy_Cost);

         if (total_energy_Cost < minEnergyCost)
        {
          allocated_pm=possible_pm;
          minEnergyCost=total_energy_Cost;
        }
        
        ArrayList<Integer> util2=new ArrayList<Integer>();
        for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma3 = (BigDecimal) featureMatrixArray.get(i);
            int item= fma3.intValue();
          util2.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i) - item);
        }
        utilizedMachineCapacities_tmp.set(possible_pm, util2);
      }
      
      ArrayList<Integer> util3=new ArrayList<Integer>();

      for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma4 = (BigDecimal) featureMatrixArray.get(i);
            int item= fma4.intValue();
          util3.add(this.utilizedMachineCapacities.get(allocated_pm).get(i)+item);
        }

      /* Map.Entry<Integer, Double> min = Collections.min(energy_costs.entrySet(),
            Map.Entry.comparingByValue());
            allocated_pm=min.getKey();   */  
            
           
      this.utilizedMachineCapacities.set(allocated_pm,util3);
      
      return allocated_pm;
    }
    }
    
    private int tryAllocateSimulationFeatureMatrixMultipleDataCentersRandomPart2(Object featureMatrix) {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  
      int lengthOfFeatureMatrix= featureMatrixArray.size();
      
      ArrayList<Object> dataCenterFeatures =new ArrayList<>();
      for (int x=lengthOfFeatureMatrix-this.numberOfDataCenters ; x<lengthOfFeatureMatrix ; x++)
      {
          dataCenterFeatures.add(featureMatrixArray.get(x));
      }
      
      ArrayList<Integer> demandedDataCenter =new ArrayList<>();
      for(int i=0 ; i< dataCenterFeatures.size()  ; i++)
      {
        BigDecimal df =(BigDecimal)dataCenterFeatures.get(i);
        if (df.intValue() == 1)
        {
               demandedDataCenter.add(i);
        }
      }
      
      int ddc ;
      if (demandedDataCenter.size() >0 )
      {
          ddc=demandedDataCenter.get(0);
      }
      else
      {
        return -1;
      }
      
      ArrayList<Integer> feasiblePms =new ArrayList<>();
      for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
      {
        if (this.dataCentersPhysicalMachines.get(machine_index)== ddc)
        {
             feasiblePms.add(machine_index);
        }
      } 
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
        int allocated_pm=-1;
        
      
        for( int mac_index : feasiblePms)
        {
          for (int i=0; i<this.dimension ; i++)
          {
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(mac_index).get(i)+ item <= this.physicalMachines.get(mac_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              allocated_pm=mac_index;
              break;
        }

        if (allocated_pm == -1)
        return -1;
    else
    {
    
      
      ArrayList<Integer> util3=new ArrayList<Integer>();

      for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma4 = (BigDecimal) featureMatrixArray.get(i);
            int item= fma4.intValue();
          util3.add(this.utilizedMachineCapacities.get(allocated_pm).get(i)+item);
        }

    
           
      this.utilizedMachineCapacities.set(allocated_pm,util3);
      
      return allocated_pm;
    }
    }
    private Double tryAllocateFeatureMatrixMultipleDataCentersPart1(Object featureMatrix) {
      ArrayList<Object> featureMatrixArray=(ArrayList<Object>) featureMatrix;  
      int lengthOfFeatureMatrix= featureMatrixArray.size();
      
      ArrayList<Object> dataCenterFeatures =new ArrayList<>();
      for (int x=lengthOfFeatureMatrix-this.numberOfDataCenters ; x<lengthOfFeatureMatrix ; x++)
      {
          dataCenterFeatures.add(featureMatrixArray.get(x));
      }
      
      ArrayList<Integer> demandedDataCenter =new ArrayList<>();
      for(int i=0 ; i< dataCenterFeatures.size()  ; i++)
      {
        BigDecimal datacenfeatureBD= (BigDecimal) dataCenterFeatures.get(i);
        if (datacenfeatureBD.intValue() == 1)
        {
               demandedDataCenter.add(i);
        }
      }
      
      int ddc ;
      if (demandedDataCenter.size() >0 )
      {
          ddc=demandedDataCenter.get(0);
      }
      else
      {
       
        return null;
      }
      
      ArrayList<Integer> feasiblePms =new ArrayList<>();
      for (int machine_index=0 ; machine_index<this.physicalMachines.size(); machine_index++)
      {
        if (this.dataCentersPhysicalMachines.get(machine_index)== ddc)
        {
             feasiblePms.add(machine_index);
        }
      } 
      ArrayList<List<Integer>> utilizedMachineCapacities_tmp =new ArrayList<List<Integer>>();

       for (int machine_index=0;machine_index<this.physicalMachines.size();machine_index++)
       {
            ArrayList<Integer> utilizedMachinecapacities= new ArrayList<Integer>();
            for (int a=0;a<this.dimension;a++)
            {
              utilizedMachinecapacities.add(this.utilizedMachineCapacities.get(machine_index).get(a));
            }

            utilizedMachineCapacities_tmp.add(utilizedMachinecapacities);

       }  

        boolean isAllocationFeasible=false;
       
        ArrayList<Integer> possibleAllocations=new ArrayList<Integer>();

        for( int mac_index : feasiblePms)
        {
          for (int i=0; i<this.dimension ; i++)
          {
            BigDecimal fma = (BigDecimal) featureMatrixArray.get(i);
            int item= fma.intValue();
            if (this.utilizedMachineCapacities.get(mac_index).get(i)+ item <= this.physicalMachines.get(mac_index).get(i)) 
            {
              isAllocationFeasible=true;
            }
            else
            {
              isAllocationFeasible=false;
            }
            if (isAllocationFeasible==false)
              break;
          }

          if (isAllocationFeasible==true)
              possibleAllocations.add(mac_index);
        }

        if (possibleAllocations.size() == 0 )
        {
           
            return null;
        }
        
    else
    {
      HashMap<Integer, Double> energy_costs=new HashMap<Integer, Double>(); 
      for (int possible_pm : possibleAllocations)
      {
        ArrayList<Integer> util=new ArrayList<Integer>();
        for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma2=(BigDecimal) featureMatrixArray.get(i);
          int item= fma2.intValue();
          util.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i)+item);
        }
        
        utilizedMachineCapacities_tmp.set(possible_pm, util);

        energy_costs.put(possible_pm,this.calculateTotalEnergyCost(utilizedMachineCapacities_tmp));

        ArrayList<Integer> util2=new ArrayList<Integer>();
        for (int i = 0 ; i<this.dimension ; i++)
        {
          BigDecimal fma3=(BigDecimal) featureMatrixArray.get(i);
          int item= fma3.intValue();
          util2.add(utilizedMachineCapacities_tmp.get(possible_pm).get(i) - item);
        }
        utilizedMachineCapacities_tmp.set(possible_pm, util2);
      }
         

      
      return Collections.min(energy_costs.values());
    }
  }

    public  double getSumDouble(List<Double> nums) {
      double sum = 0;
      for (double i: nums) {
          sum += i;
      }
      return sum;
  }
  private List<Double> calculate_bids_prices() {
    List<Double> offered_prices_of_bids = new ArrayList<Double>();
    JsonArray bidss=(JsonArray)this.bids.get(0);
    for(int bid_index=0 ; bid_index < bidss.size() ; bid_index++)
    {
      ArrayList<Object> bidArray=(ArrayList<Object>) bidss.get(bid_index); 
      int index_of_price=bidArray.size()-1;
     
    
      BigDecimal prBidDec= (BigDecimal) bidArray.get(index_of_price);
      double price= (double) prBidDec.intValue();
     offered_prices_of_bids.add(price);

    }
    return offered_prices_of_bids;
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
  /* 
   
     
    
    public double determine_idle_cost_for_physical_machine(int machineIndex)
    {
        int data_center_pm_in=this.dataCentersPhysicalMachines.get(machineIndex); 
        double energy_cost_in_data_center=this.data_cen_energy_costs[data_center_pm_in]
        return (this.physicalMachineIdleFullEnergyCosts.get(machine_index).get(0)/1000)*this.pue*this.allocation_hours*energy_cost_in_data_center 
    }
    */

}
