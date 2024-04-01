import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, DeserializationException, InterruptedException {
      // double obj_value_greedy_solver=runGreedySolver("test_case_bids.json");

      // double obj_value_genetic_sorter = runGeneticSorter("test_case_bids.json");
    // double obj_value_art_colony=runArtColonyOptimizer("test_case_bids.json");
    //   GreedySolverExcel("test_case_bids.json");
       

  
     
   GeneticSorterExcel("test_case_bids.json");

   
   // AntColonyOptimizerExcel("test_case_bids.json");

 //  SimulationGreedySolverExcel("test_case_bids.json");

       
      }

   


   private static double runArtColonyOptimizer(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException {
      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      int test_case_index=3436;
      JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
    
   
      List<Object>  bids= Arrays.asList(dict.get("bids"));
     
  
  
  

     BigDecimal dimension= (BigDecimal)dict.get("dimension");
     BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
     BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
     BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
     int cap_factor=cpu_core.intValue()/512;
  
    
     
    
  
     List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
     for (int i=0;i<cap_factor;i++)
     {
        physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
     }
  
     List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
     for (int i=0;i<cap_factor;i++)
     {
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
     }
  
     HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
     int k=0;
     int p=0;
     for (int j=0 ; j<numOfDataCenters.intValue();j++)
     {
        while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
        { 
           dataCentersPhysicalMachines.put(k+p, j);
           k=k+1;
        }
        p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
        k=0;
     }
  
     ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
     List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();
  
     
     List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
   
     phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
  
     p=0;
     int change=cap_factor/numOfDataCenters.intValue();
     int z=0;
  
     for (int i=0;i<cap_factor;i++)
     {
        List<Integer> phymaclist =new ArrayList<Integer>();
        for (List<Integer> pm :phymacvalues)
        {
           if (numOfDataCenters.intValue()== 1)
           {
              phymaclist=pm ;
           }
           else
           {
              List<Integer>  datacenterlist =new ArrayList<Integer>();
              for (int j=0 ; j<numOfDataCenters.intValue();j++)
              {
                 datacenterlist.add(0);
              }
  
              datacenterlist.set(p, 1000000000);
              phymaclist.addAll(pm);
              phymaclist.addAll(datacenterlist);
                        
           }
  
           phyResourceCapacities.add(phymaclist)  ;
        }
  
        z=z+1;
        if (z==change)
        {
           p=p+1;
           z=0 ;
        }   
     }   
     JsonArray bidss=(JsonArray)bids.get(0);   

     GreedySolver gr_solver_x=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
     gr_solver_x.sortBids();
     gr_solver_x.allocateBids();
     
    // double initial_pheromone= gr_solver_x.current_obj_value/2;
    double initial_pheromone= 1;
     long start = System.currentTimeMillis();
     AntColonyOptimization antcolony_solver=new AntColonyOptimization(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index,10,initial_pheromone,0.9,2);
     
     antcolony_solver.solve_main();
     long end = System.currentTimeMillis();    
     float sec = (end - start) / 1000F;

     GreedySolver gr_solver=new GreedySolver(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
     
     gr_solver.allocateBidsWithGivenOrder(antcolony_solver.current_optimal_individual);

     System.out.println("Obj value with ant colony optimizer"+gr_solver.objectiveValue);
     System.out.println("runtime"+sec);
     System.out.println("total price"+gr_solver.total_price);
     System.out.println("total idle cost"+gr_solver.total_idle_cost);
     System.out.println("total util cost"+gr_solver.total_utilization_cost);
     double bid_acceptance_ratio1= countBidsAccepted(gr_solver.allocationOfBidsResults);

     int num_of_poweredon_machines1= countPoweredOnMachines(gr_solver.utilizedMachineCapacities);
     System.out.println("num of powered on machines"+num_of_poweredon_machines1);

     System.out.println("bid acceptance"+bid_acceptance_ratio1);
     return gr_solver.objectiveValue;
   }

   public static void GreedySolverExcel(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException 
   {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet spreadsheet = workbook.createSheet(" Greedy Heuristic Results");
      int rowid = 1;
      int cellid = 0;
      
      List<String> baslik = new ArrayList<String>(Arrays.asList("Test Case No","Test Case Name","Supply Demand Ratio","Cpu Core","Dimension","Data Center Count","Mean of subbids","Mean of quantity","Greedy & Sorting 1 Opt.Value","Greedy & Sorting 1 Runtime","Greedy & Sorting 1 Bid acceptance ratio","Greedy & Sorting 1 Total Price","Greedy & Sorting 1 Total Idle cost","Greedy & Sorting 1 Total Util cost","Greedy & Sorting 1 Num of powered on machines","Greedy & Sorting 1 Closeness to Gurobi","Greedy & Sorting 2 Opt.Value","Greedy & Sorting 2 Runtime","Greedy & Sorting 2 Bid acceptance ratio","Greedy & Sorting 2 Total Price","Greedy & Sorting 2 Total Idle cost","Greedy & Sorting 2 Total Util cost","Greedy & Sorting 2 Num of powered on machines","Greedy & Sorting 2 Closeness to Gurobi"));
      Row row = spreadsheet.createRow(0);
      for (int i =0; i<baslik.size();i++)
      {
         
         Cell cell = row.createCell(i);
         cell.setCellValue(baslik.get(i));
      }

      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      
      
      for(int test_case_index=0; test_case_index< 3456; test_case_index++)
      {  
         JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
         List<Object>  bids= Arrays.asList(dict.get("bids"));
         BigDecimal testcaseno= (BigDecimal)dict.get("test-case-no");
         String testcasename= (String)dict.get("test-case-name");
         BigDecimal dimension= (BigDecimal)dict.get("dimension");
         
         BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
         BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
         BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
         BigDecimal mean_of_subbids= (BigDecimal)dict.get("mean-of-subbids");
         BigDecimal mean_of_quantity= (BigDecimal)dict.get("mean-of-quantity");
         int cap_factor=cpu_core.intValue()/512;

         List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
         }
      
         List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
         }
        
         HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
         int k=0;
         int p=0;
         for (int j=0 ; j<numOfDataCenters.intValue();j++)
         {
            while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
            { 
               dataCentersPhysicalMachines.put(k+p, j);
               k=k+1;
            }
            p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
            k=0;
         }

         ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
         List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();

         
         List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
      
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));

         p=0;
         int change=cap_factor/numOfDataCenters.intValue();
         int z=0;

         for (int i=0;i<cap_factor;i++)
         {
            List<Integer> phymaclist =new ArrayList<Integer>();
            for (List<Integer> pm :phymacvalues)
            {
               if (numOfDataCenters.intValue()== 1)
               {
                  phymaclist=pm ;
               }
               else
               {
                  List<Integer>  datacenterlist =new ArrayList<Integer>();
                  for (int j=0 ; j<numOfDataCenters.intValue();j++)
                  {
                     datacenterlist.add(0);
                  }

                  datacenterlist.set(p, 1000000000);
                  phymaclist.addAll(pm);
                  phymaclist.addAll(datacenterlist);
                           
               }

               phyResourceCapacities.add(phymaclist)  ;
            }

            z=z+1;
            if (z==change)
            {
               p=p+1;
               z=0 ;
            }   
         }
      
         long start = System.currentTimeMillis();
         GreedySolver gr_solver=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
         gr_solver.sortBids();
         gr_solver.allocateBids();
         long end = System.currentTimeMillis();    
         float sec = (end - start) / 1000F;
         
         double bid_acceptance_ratio1= countBidsAccepted(gr_solver.allocationOfBidsResults);

         int num_of_poweredon_machines1= countPoweredOnMachines(gr_solver.utilizedMachineCapacities);

         long start2 = System.currentTimeMillis();
         GreedySolver gr_solver2=new GreedySolver(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
         gr_solver2.sortBids();
         gr_solver2.allocateBids();
         long end2 = System.currentTimeMillis();    
         float sec2 = (end2 - start2) / 1000F;
         
         double bid_acceptance_ratio2= countBidsAccepted(gr_solver2.allocationOfBidsResults);

         int num_of_poweredon_machines2= countPoweredOnMachines(gr_solver2.utilizedMachineCapacities);
         
         

         Row rowx = spreadsheet.createRow(rowid);
         
        
            Cell cell = rowx.createCell(0);
            cell.setCellValue(testcaseno.intValue());

            Cell cell2 = rowx.createCell(1);
            cell2.setCellValue(testcasename);

            Cell cell3 = rowx.createCell(2);
            cell3.setCellValue(supply_demand.doubleValue());

            Cell cell4 = rowx.createCell(3);
            cell4.setCellValue(cpu_core.intValue());

            Cell cell5 = rowx.createCell(4);
            cell5.setCellValue(dimension.intValue());

            Cell cell6 = rowx.createCell(5);
            cell6.setCellValue(numOfDataCenters.intValue());

            Cell cell7 = rowx.createCell(6);
            cell7.setCellValue(mean_of_subbids.doubleValue());

            Cell cell8 = rowx.createCell(7);
            cell8.setCellValue(mean_of_quantity.doubleValue());
            
            Cell cell9 = rowx.createCell(8);
            cell9.setCellValue(gr_solver.objectiveValue);
            
            Cell cell10 = rowx.createCell(9);
            cell10.setCellValue(sec);
            
            Cell cell11 = rowx.createCell(10);
            cell11.setCellValue(bid_acceptance_ratio1);

            Cell cell12 = rowx.createCell(11);
            cell12.setCellValue(gr_solver.total_price);

            Cell cell13 = rowx.createCell(12);
            cell13.setCellValue(gr_solver.total_idle_cost);

            Cell cell14 = rowx.createCell(13);
            cell14.setCellValue(gr_solver.total_utilization_cost);

            Cell cell15 = rowx.createCell(14);
            cell15.setCellValue(num_of_poweredon_machines1);

            Cell cell16 = rowx.createCell(16);
            cell16.setCellValue(gr_solver2.objectiveValue);

            Cell cell17 = rowx.createCell(17);
            cell17.setCellValue(sec2);

            Cell cell18 = rowx.createCell(18);
            cell18.setCellValue(bid_acceptance_ratio2);

            Cell cell19 = rowx.createCell(19);
            cell19.setCellValue(gr_solver2.total_price);

            Cell cell20 = rowx.createCell(20);
            cell20.setCellValue(gr_solver2.total_idle_cost);

            Cell cell21 = rowx.createCell(21);
            cell21.setCellValue(gr_solver2.total_utilization_cost);

            Cell cell22 = rowx.createCell(22);
            cell22.setCellValue(num_of_poweredon_machines2);

            rowid=rowid+1;

      System.out.println(test_case_index);      
      } 

     

      FileOutputStream out = new FileOutputStream(new File("greedy_results_java.xlsx"));

     workbook.write(out);
     out.close();
   }

   public static void SimulationGreedySolverExcel(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException 
   {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet spreadsheet = workbook.createSheet(" Simulation Greedy Heuristic Results");
      int rowid = 1;
      int cellid = 0;
      
      List<String> baslik = new ArrayList<String>(Arrays.asList("Test Case No","Test Case Name","Supply Demand Ratio","Cpu Core","Dimension","Data Center Count","Mean of subbids","Mean of quantity","Greedy Simulation Opt.Value","Greedy Simulation Runtime","Greedy Simulation Closeness to Gurobi"));
      Row row = spreadsheet.createRow(0);
      for (int i =0; i<baslik.size();i++)
      {
         
         Cell cell = row.createCell(i);
         cell.setCellValue(baslik.get(i));
      }

      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      
      
      
         for(int test_case_index=0; test_case_index< 3456; test_case_index++)
         {  
            JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
            List<Object>  bids= Arrays.asList(dict.get("bids"));
            BigDecimal testcaseno= (BigDecimal)dict.get("test-case-no");
            String testcasename= (String)dict.get("test-case-name");
            BigDecimal dimension= (BigDecimal)dict.get("dimension");
            
            BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
            BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
            BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
            BigDecimal mean_of_subbids= (BigDecimal)dict.get("mean-of-subbids");
            BigDecimal mean_of_quantity= (BigDecimal)dict.get("mean-of-quantity");
            int cap_factor=cpu_core.intValue()/512;

            List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
            for (int i=0;i<cap_factor;i++)
            {
               physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
               physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
            }
         
            List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
            for (int i=0;i<cap_factor;i++)
            {
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
               physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
            }
         
            HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
            int k=0;
            int p=0;
            for (int j=0 ; j<numOfDataCenters.intValue();j++)
            {
               while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
               { 
                  dataCentersPhysicalMachines.put(k+p, j);
                  k=k+1;
               }
               p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
               k=0;
            }

            ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
            List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();

            
            List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
         
            phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
            phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));

            p=0;
            int change=cap_factor/numOfDataCenters.intValue();
            int z=0;

            for (int i=0;i<cap_factor;i++)
            {
               List<Integer> phymaclist =new ArrayList<Integer>();
               for (List<Integer> pm :phymacvalues)
               {
                  if (numOfDataCenters.intValue()== 1)
                  {
                     phymaclist=pm ;
                  }
                  else
                  {
                     List<Integer>  datacenterlist =new ArrayList<Integer>();
                     for (int j=0 ; j<numOfDataCenters.intValue();j++)
                     {
                        datacenterlist.add(0);
                     }

                     datacenterlist.set(p, 1000000000);
                     phymaclist.addAll(pm);
                     phymaclist.addAll(datacenterlist);
                              
                  }

                  phyResourceCapacities.add(phymaclist)  ;
               }

               z=z+1;
               if (z==change)
               {
                  p=p+1;
                  z=0 ;
               }   
            }
         
            
            
            
            

            Row rowx = spreadsheet.createRow(rowid);
            
         
               Cell cell = rowx.createCell(0);
               cell.setCellValue(testcaseno.intValue());

               Cell cell2 = rowx.createCell(1);
               cell2.setCellValue(testcasename);

               Cell cell3 = rowx.createCell(2);
               cell3.setCellValue(supply_demand.doubleValue());

               Cell cell4 = rowx.createCell(3);
               cell4.setCellValue(cpu_core.intValue());

               Cell cell5 = rowx.createCell(4);
               cell5.setCellValue(dimension.intValue());

               Cell cell6 = rowx.createCell(5);
               cell6.setCellValue(numOfDataCenters.intValue());

               Cell cell7 = rowx.createCell(6);
               cell7.setCellValue(mean_of_subbids.doubleValue());

               Cell cell8 = rowx.createCell(7);
               cell8.setCellValue(mean_of_quantity.doubleValue());
               int colId=8;
               for (int ran=0; ran < 20 ; ran++)
               {
                  long start = System.currentTimeMillis();
                  GreedySolver gr_solver=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
                  gr_solver.shuffleBids();
                  gr_solver.allocateBids();
                  long end = System.currentTimeMillis();    
                  float sec = (end - start) / 1000F;
                     Cell cell9 = rowx.createCell(colId);
                     cell9.setCellValue(gr_solver.objectiveValue);
                     
                     Cell cell10 = rowx.createCell(colId+1);
                     cell10.setCellValue(sec);
                     colId =colId+2;
               }
               
               
               

               rowid=rowid+1;

         System.out.println(test_case_index);      
      } 
    
   
     

      FileOutputStream out = new FileOutputStream(new File("greedy_simulation_random_results_java.xlsx"));

     workbook.write(out);
     out.close();
   }

   

   public static void GeneticSorterExcel(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException 
   {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet spreadsheet = workbook.createSheet(" Genetic Sorter Heuristic Results");
      int rowid = 1;
      int cellid = 0;
      
      List<String> baslik = new ArrayList<String>(Arrays.asList("Test Case No","Test Case Name","Supply Demand Ratio","Cpu Core","Dimension","Data Center Count","Mean of subbids","Mean of quantity","Genetic Sorter Opt.Value","Genetic Sorter Runtime","Genetic Sorter Bid acceptance ratio","Genetic Sorter Total Price","Genetic Sorter Total Idle cost","Genetic Sorter Total Util cost","Genetic Sorter1 Num of powered on machines","Genetic Sorter Closeness to Gurobi"));
      Row row = spreadsheet.createRow(0);
      for (int i =0; i<baslik.size();i++)
      {
         
         Cell cell = row.createCell(i);
         cell.setCellValue(baslik.get(i));
      }

      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      
      
      for(int test_case_index=0; test_case_index< 3456; test_case_index++)
      {  
         JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
         List<Object>  bids= Arrays.asList(dict.get("bids"));
         BigDecimal testcaseno= (BigDecimal)dict.get("test-case-no");
         String testcasename= (String)dict.get("test-case-name");
         BigDecimal dimension= (BigDecimal)dict.get("dimension");
         
         BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
         BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
         BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
         BigDecimal mean_of_subbids= (BigDecimal)dict.get("mean-of-subbids");
         BigDecimal mean_of_quantity= (BigDecimal)dict.get("mean-of-quantity");
         int cap_factor=cpu_core.intValue()/512;

         List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
         }
      
         List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
         }
        
         HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
         int k=0;
         int p=0;
         for (int j=0 ; j<numOfDataCenters.intValue();j++)
         {
            while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
            { 
               dataCentersPhysicalMachines.put(k+p, j);
               k=k+1;
            }
            p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
            k=0;
         }

         ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
         List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();

         
         List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
      
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));

         p=0;
         int change=cap_factor/numOfDataCenters.intValue();
         int z=0;

         for (int i=0;i<cap_factor;i++)
         {
            List<Integer> phymaclist =new ArrayList<Integer>();
            for (List<Integer> pm :phymacvalues)
            {
               if (numOfDataCenters.intValue()== 1)
               {
                  phymaclist=pm ;
               }
               else
               {
                  List<Integer>  datacenterlist =new ArrayList<Integer>();
                  for (int j=0 ; j<numOfDataCenters.intValue();j++)
                  {
                     datacenterlist.add(0);
                  }

                  datacenterlist.set(p, 1000000000);
                  phymaclist.addAll(pm);
                  phymaclist.addAll(datacenterlist);
                           
               }

               phyResourceCapacities.add(phymaclist)  ;
            }

            z=z+1;
            if (z==change)
            {
               p=p+1;
               z=0 ;
            }   
         }
      
         long start = System.currentTimeMillis();
         GeneticSorter gen_sorter=new GeneticSorter(bids,3,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index,10,1,5);
         gen_sorter.sort_main();

         long end = System.currentTimeMillis();    
         float sec = (end - start) / 1000F;
         GreedySolver gr_solver=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
      
         gr_solver.allocateBidsWithGivenOrder(gen_sorter.current_optimal_individual);
         
         
         double bid_acceptance_ratio1= countBidsAccepted(gr_solver.allocationOfBidsResults);

         int num_of_poweredon_machines1= countPoweredOnMachines(gr_solver.utilizedMachineCapacities);

         
         
         

         Row rowx = spreadsheet.createRow(rowid);
         
        
            Cell cell = rowx.createCell(0);
            cell.setCellValue(testcaseno.intValue());

            Cell cell2 = rowx.createCell(1);
            cell2.setCellValue(testcasename);

            Cell cell3 = rowx.createCell(2);
            cell3.setCellValue(supply_demand.doubleValue());

            Cell cell4 = rowx.createCell(3);
            cell4.setCellValue(cpu_core.intValue());

            Cell cell5 = rowx.createCell(4);
            cell5.setCellValue(dimension.intValue());

            Cell cell6 = rowx.createCell(5);
            cell6.setCellValue(numOfDataCenters.intValue());

            Cell cell7 = rowx.createCell(6);
            cell7.setCellValue(mean_of_subbids.doubleValue());

            Cell cell8 = rowx.createCell(7);
            cell8.setCellValue(mean_of_quantity.doubleValue());
            
            Cell cell9 = rowx.createCell(8);
            cell9.setCellValue(gr_solver.objectiveValue);
            
            Cell cell10 = rowx.createCell(9);
            cell10.setCellValue(sec);
            
            Cell cell11 = rowx.createCell(10);
            cell11.setCellValue(bid_acceptance_ratio1);

            Cell cell12 = rowx.createCell(11);
            cell12.setCellValue(gr_solver.total_price);

            Cell cell13 = rowx.createCell(12);
            cell13.setCellValue(gr_solver.total_idle_cost);

            Cell cell14 = rowx.createCell(13);
            cell14.setCellValue(gr_solver.total_utilization_cost);

            Cell cell15 = rowx.createCell(14);
            cell15.setCellValue(num_of_poweredon_machines1);

            

            rowid=rowid+1;

            System.out.println(test_case_index);
      } 

     

      FileOutputStream out = new FileOutputStream(new File("geneticsorter_results_java.xlsx"));

     workbook.write(out);
     out.close();
   }

   public static void AntColonyOptimizerExcel(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException 
   {
      XSSFWorkbook workbook = new XSSFWorkbook();
      XSSFSheet spreadsheet = workbook.createSheet(" Ant Colony Optimizer Heuristic Results");
      int rowid = 1;
      int cellid = 0;
      
      List<String> baslik = new ArrayList<String>(Arrays.asList("Test Case No","Test Case Name","Supply Demand Ratio","Cpu Core","Dimension","Data Center Count","Mean of subbids","Mean of quantity","Ant Colony Opt.Value","Ant Colony Runtime","Ant Colony Bid acceptance ratio","Ant Colony Total Price","Ant Colony Total Idle cost","Ant Colony Total Util cost","Ant Colony Num of powered on machines","Ant Colony Closeness to Gurobi"));
      Row row = spreadsheet.createRow(0);
      for (int i =0; i<baslik.size();i++)
      {
         
         Cell cell = row.createCell(i);
         cell.setCellValue(baslik.get(i));
      }

      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      
      
      for(int test_case_index=3436; test_case_index< 3456; test_case_index++)
      {  
         JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
         List<Object>  bids= Arrays.asList(dict.get("bids"));
         BigDecimal testcaseno= (BigDecimal)dict.get("test-case-no");
         String testcasename= (String)dict.get("test-case-name");
         BigDecimal dimension= (BigDecimal)dict.get("dimension");
         
         BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
         BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
         BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
         BigDecimal mean_of_subbids= (BigDecimal)dict.get("mean-of-subbids");
         BigDecimal mean_of_quantity= (BigDecimal)dict.get("mean-of-quantity");
         int cap_factor=cpu_core.intValue()/512;

         List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
            physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
         }
      
         List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
         for (int i=0;i<cap_factor;i++)
         {
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
            physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
         }
        
         HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
         int k=0;
         int p=0;
         for (int j=0 ; j<numOfDataCenters.intValue();j++)
         {
            while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
            { 
               dataCentersPhysicalMachines.put(k+p, j);
               k=k+1;
            }
            p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
            k=0;
         }

         ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
         List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();

         
         List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
      
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
         phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));

         p=0;
         int change=cap_factor/numOfDataCenters.intValue();
         int z=0;

         for (int i=0;i<cap_factor;i++)
         {
            List<Integer> phymaclist =new ArrayList<Integer>();
            for (List<Integer> pm :phymacvalues)
            {
               if (numOfDataCenters.intValue()== 1)
               {
                  phymaclist=pm ;
               }
               else
               {
                  List<Integer>  datacenterlist =new ArrayList<Integer>();
                  for (int j=0 ; j<numOfDataCenters.intValue();j++)
                  {
                     datacenterlist.add(0);
                  }

                  datacenterlist.set(p, 1000000000);
                  phymaclist.addAll(pm);
                  phymaclist.addAll(datacenterlist);
                           
               }

               phyResourceCapacities.add(phymaclist)  ;
            }

            z=z+1;
            if (z==change)
            {
               p=p+1;
               z=0 ;
            }   
         }
         GreedySolver gr_solver_x=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
         gr_solver_x.sortBids();
         gr_solver_x.allocateBids();
         
         double initial_pheromone= gr_solver_x.objectiveValue/10;
       //  double initial_pheromone= 1;
         double elitist_selection_prob= 0.9;
         double epsilon= 2;
         int populationsize=30;
         long start = System.currentTimeMillis();
         AntColonyOptimization antcolony_solver=new AntColonyOptimization(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index,populationsize,initial_pheromone,elitist_selection_prob,epsilon);
     
         antcolony_solver.solve_main();

         long end = System.currentTimeMillis();    
         float sec = (end - start) / 1000F;
         GreedySolver gr_solver=new GreedySolver(bids,1,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
      
         gr_solver.allocateBidsWithGivenOrder(antcolony_solver.current_optimal_individual);
         
         
         double bid_acceptance_ratio1= countBidsAccepted(gr_solver.allocationOfBidsResults);

         int num_of_poweredon_machines1= countPoweredOnMachines(gr_solver.utilizedMachineCapacities);

         
         
         

         Row rowx = spreadsheet.createRow(rowid);
         
        
            Cell cell = rowx.createCell(0);
            cell.setCellValue(testcaseno.intValue());

            Cell cell2 = rowx.createCell(1);
            cell2.setCellValue(testcasename);

            Cell cell3 = rowx.createCell(2);
            cell3.setCellValue(supply_demand.doubleValue());

            Cell cell4 = rowx.createCell(3);
            cell4.setCellValue(cpu_core.intValue());

            Cell cell5 = rowx.createCell(4);
            cell5.setCellValue(dimension.intValue());

            Cell cell6 = rowx.createCell(5);
            cell6.setCellValue(numOfDataCenters.intValue());

            Cell cell7 = rowx.createCell(6);
            cell7.setCellValue(mean_of_subbids.doubleValue());

            Cell cell8 = rowx.createCell(7);
            cell8.setCellValue(mean_of_quantity.doubleValue());
            
            Cell cell9 = rowx.createCell(8);
            cell9.setCellValue(gr_solver.objectiveValue);
            
            Cell cell10 = rowx.createCell(9);
            cell10.setCellValue(sec);
            
            Cell cell11 = rowx.createCell(10);
            cell11.setCellValue(bid_acceptance_ratio1);

            Cell cell12 = rowx.createCell(11);
            cell12.setCellValue(gr_solver.total_price);

            Cell cell13 = rowx.createCell(12);
            cell13.setCellValue(gr_solver.total_idle_cost);

            Cell cell14 = rowx.createCell(13);
            cell14.setCellValue(gr_solver.total_utilization_cost);

            Cell cell15 = rowx.createCell(14);
            cell15.setCellValue(num_of_poweredon_machines1);

            

            rowid=rowid+1;

            System.out.println(test_case_index);
      } 

     

      FileOutputStream out = new FileOutputStream(new File("antcolonysystem_results_java_popsize30_elitprob_0_9.xlsx"));

     workbook.write(out);
     out.close();
   }

   private static int countPoweredOnMachines(ArrayList<List<Integer>> utilizedMachineCapacities) {
      int num_of_poweredon_machines=0;

      for (int i=0;i<utilizedMachineCapacities.size();i++)
      {
        if(utilizedMachineCapacities.get(i).get(0) !=0)
        {
         num_of_poweredon_machines=num_of_poweredon_machines+1;
        }
      }
      return num_of_poweredon_machines;
    
   }

   private static double countBidsAccepted(HashMap<Integer, Boolean> allocationOfBidsResults)
    {
      int accepted=0; 
      for(int i=0;i<allocationOfBidsResults.size();i++)
      {
         if (allocationOfBidsResults.get(i) ==true)
         {
            accepted =accepted+1;
         }
      }

      return (double) accepted / (double) allocationOfBidsResults.size();
   }

   public static double runGreedySolver(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException
   {
    //JSONParser parser = new JSONParser();
    JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
    List testCaseList=(List) testCasesJson.get("test-cases");
    int test_case_index=3455;
    JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
  
 
    List<Object>  bids= Arrays.asList(dict.get("bids"));
    

    

   
   BigDecimal dimension= (BigDecimal)dict.get("dimension");
   BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
   BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
   BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
   int cap_factor=cpu_core.intValue()/512;

   
   
  

   List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
   for (int i=0;i<cap_factor;i++)
   {
      physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
      physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
   }

   List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
   for (int i=0;i<cap_factor;i++)
   {
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
      physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
   }

   HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
   int k=0;
   int p=0;
   for (int j=0 ; j<numOfDataCenters.intValue();j++)
   {
      while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
      { 
         dataCentersPhysicalMachines.put(k+p, j);
         k=k+1;
      }
      p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
      k=0;
   }

   ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
   List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();

   
   List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
 
   phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
   phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));

   p=0;
   int change=cap_factor/numOfDataCenters.intValue();
   int z=0;

   for (int i=0;i<cap_factor;i++)
   {
      List<Integer> phymaclist =new ArrayList<Integer>();
      for (List<Integer> pm :phymacvalues)
      {
         if (numOfDataCenters.intValue()== 1)
         {
            phymaclist=pm ;
         }
         else
         {
            List<Integer>  datacenterlist =new ArrayList<Integer>();
            for (int j=0 ; j<numOfDataCenters.intValue();j++)
            {
               datacenterlist.add(0);
            }

            datacenterlist.set(p, 1000000000);
            phymaclist.addAll(pm);
            phymaclist.addAll(datacenterlist);
                      
         }

         phyResourceCapacities.add(phymaclist)  ;
      }

      z=z+1;
      if (z==change)
      {
         p=p+1;
         z=0 ;
      }   
   }   
   long start = System.currentTimeMillis();
   GreedySolver gr_solver=new GreedySolver(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
   gr_solver.sortBids();
   gr_solver.allocateBids();
   long end = System.currentTimeMillis();

   float sec = (end - start) / 1000F;
    System.out.println(sec + " seconds passeds");
   System.out.println("obj. value"+gr_solver.objectiveValue);
            


 //  GreedySolver gSolver=new GreedySolver(allTheBids, 1, dimension.intValue(), numOfDataCenters.intValue(),  dataCentersPhysicalMachines, physicalMachines, physicalMachineIdleFullEnergyCosts);
 //  System.out.println(gSolver.sortBids());
  // gSolver.allocateBids();

   /*  JsonArray jsonArray=(JsonArray)bids.get(0); //bids
    JsonArray js2= (JsonArray) jsonArray.get(0);  //bid
    JsonArray js3= (JsonArray) js2.get(3); // subbid
    ArrayList<Object> listdata = new ArrayList<Object>();  
          
    
        for (int i=0;i<js3.size();i++){   
              
            listdata.add(js3.get(i));  
        }   
         
    JsonArray js4= (JsonArray) listdata.get(0);
    BigDecimal bdd=(BigDecimal) js4.get(0);
    System.out.println(bdd.doubleValue());*/
    return gr_solver.objectiveValue;
   }   

   public static double runGeneticSorter(String fileName) throws FileNotFoundException, IOException, ParseException, DeserializationException
   {
    
      JsonObject  testCasesJson = (JsonObject) Jsoner.deserialize(new FileReader(fileName));
      List testCaseList=(List) testCasesJson.get("test-cases");
      int test_case_index=2519;
      JsonObject dict =(JsonObject) testCaseList.get(test_case_index);
    
   
      List<Object>  bids= Arrays.asList(dict.get("bids"));
     
  
  
  

     BigDecimal dimension= (BigDecimal)dict.get("dimension");
     BigDecimal numOfDataCenters= (BigDecimal)dict.get("data-center");
     BigDecimal supply_demand= (BigDecimal)dict.get("supply-demand-ratio");
     BigDecimal cpu_core= (BigDecimal)dict.get("cpu-core");
     int cap_factor=cpu_core.intValue()/512;
  
    
     
    
  
     List<List<Integer>>   physicalMachines=new ArrayList<List<Integer>> ();
     for (int i=0;i<cap_factor;i++)
     {
        physicalMachines.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
        physicalMachines.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
     }
  
     List<List<Double>>   physicalMachineIdleFullEnergyCosts=new ArrayList<List<Double>> ();
     for (int i=0;i<cap_factor;i++)
     {
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(163),new Double(423))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(46.6),new Double(247))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(114),new Double(367))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(99.2),new Double(412))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(119),new Double(609))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(48.1),new Double(273))));
        physicalMachineIdleFullEnergyCosts.add(new ArrayList<>(Arrays.asList(new Double(137),new Double(491))));
     }
  
     HashMap<Integer, Integer> dataCentersPhysicalMachines =new HashMap<Integer, Integer>();
     int k=0;
     int p=0;
     for (int j=0 ; j<numOfDataCenters.intValue();j++)
     {
        while (k<(physicalMachines.size()/numOfDataCenters.intValue()) && k+p<physicalMachines.size())
        { 
           dataCentersPhysicalMachines.put(k+p, j);
           k=k+1;
        }
        p=p+Math.round(physicalMachines.size()/numOfDataCenters.intValue())  ;
        k=0;
     }
  
     ArrayList<Double> dataCenterEnergyCosts=new ArrayList<>(Arrays.asList(new Double(0.128),new Double(0.180),new Double(0.261),new Double(0.339)));
     List<List<Integer>> phyResourceCapacities =new ArrayList<List<Integer>> ();
  
     
     List<List<Integer>>   phymacvalues=new ArrayList<List<Integer>> ();
   
     phymacvalues.add(new ArrayList<>(Arrays.asList(128,256,100,1900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(44,64,20,900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(32,384,10,600)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(128,512,100,3800)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(80,256,50,1900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(36,64,10,900)));
     phymacvalues.add(new ArrayList<>(Arrays.asList(64,256,50,1900)));
  
     p=0;
     int change=cap_factor/numOfDataCenters.intValue();
     int z=0;
  
     for (int i=0;i<cap_factor;i++)
     {
        List<Integer> phymaclist =new ArrayList<Integer>();
        for (List<Integer> pm :phymacvalues)
        {
           if (numOfDataCenters.intValue()== 1)
           {
              phymaclist=pm ;
           }
           else
           {
              List<Integer>  datacenterlist =new ArrayList<Integer>();
              for (int j=0 ; j<numOfDataCenters.intValue();j++)
              {
                 datacenterlist.add(0);
              }
  
              datacenterlist.set(p, 1000000000);
              phymaclist.addAll(pm);
              phymaclist.addAll(datacenterlist);
                        
           }
  
           phyResourceCapacities.add(phymaclist)  ;
        }
  
        z=z+1;
        if (z==change)
        {
           p=p+1;
           z=0 ;
        }   
     }   
     JsonArray bidss=(JsonArray)bids.get(0);   
     long start = System.currentTimeMillis();
     GeneticSorter gensorter=new GeneticSorter(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index,20,bidss.size()-1,5);
     
     gensorter.sort_main();
     long end = System.currentTimeMillis();    
     float sec = (end - start) / 1000F;

     GreedySolver gr_solver=new GreedySolver(bids,2,dimension.intValue(),numOfDataCenters.intValue(),dataCentersPhysicalMachines,physicalMachines,physicalMachineIdleFullEnergyCosts,dataCenterEnergyCosts,phyResourceCapacities,test_case_index);
     
     gr_solver.allocateBidsWithGivenOrder(gensorter.current_optimal_individual);

     System.out.println("Obj value with genetic sorter"+gr_solver.objectiveValue);
     System.out.println("runtime"+sec);
     System.out.println("total price"+gr_solver.total_price);
     System.out.println("total idle cost"+gr_solver.total_idle_cost);
     System.out.println("total util cost"+gr_solver.total_utilization_cost);
     double bid_acceptance_ratio1= countBidsAccepted(gr_solver.allocationOfBidsResults);

     int num_of_poweredon_machines1= countPoweredOnMachines(gr_solver.utilizedMachineCapacities);
     System.out.println("num of powered on machines"+num_of_poweredon_machines1);

     System.out.println("bid acceptance"+bid_acceptance_ratio1);
     return gr_solver.objectiveValue;

   }
}
