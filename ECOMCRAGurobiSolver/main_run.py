import sys, getopt
import os
import multiprocessing




def execute(process):                                                             
   os.system(f'python {process}')       

if __name__ == '__main__':  
                                   
   for i in range(0,3456,12): 
      all_processes = ('optimal_solver_run.py '+str(i), 'optimal_solver_run.py '+str(i+1), 'optimal_solver_run.py '+str(i+2), 'optimal_solver_run.py '+str(i+3),'optimal_solver_run.py '+str(i+4),'optimal_solver_run.py '+str(i+5),'optimal_solver_run.py '+str(i+6),'optimal_solver_run.py '+str(i+7),'optimal_solver_run.py '+str(i+8),'optimal_solver_run.py '+str(i+9),'optimal_solver_run.py '+str(i+10),'optimal_solver_run.py '+str(i+11))                                                                                       
      process_pool = multiprocessing.Pool(processes = 12)                                                        
      process_pool.map(execute, all_processes)
