import sys, getopt
import os
import multiprocessing

def execute(process):                                                             
   os.system(f'python {process}')       

if __name__ == '__main__':  
                                   
   for i in range(0,3456,4): 
      all_processes = ('linear_relax_solver.py '+str(i), 'linear_relax_solver.py '+str(i+1), 'linear_relax_solver.py '+str(i+2), 'linear_relax_solver.py '+str(i+3))                                                                                       
      process_pool = multiprocessing.Pool(processes = 4)                                                        
      process_pool.map(execute, all_processes)
