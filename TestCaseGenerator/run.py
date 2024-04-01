import json
from auction import Auction 
import random
from datetime import datetime

start=datetime.now()
total_cpu_core_base=512
total_memory_base=1792
total_network_speed_base=340
total_storage_base=11900
number_of_available_cpu_cores=[total_cpu_core_base*8,total_cpu_core_base*12,total_cpu_core_base*16,total_cpu_core_base*20]
number_of_available_memory=[total_memory_base*8,total_memory_base*12,total_memory_base*16,total_memory_base*20]
number_of_available_network_speed=[total_network_speed_base*8,total_network_speed_base*12,total_network_speed_base*16,total_network_speed_base*20]
number_of_available_storage=[total_storage_base*8,total_storage_base*12,total_storage_base*16,total_storage_base*20]
supply_demand_ratios=[0.25,0.50,0.75,1.00,2.00,3.00,4.00,5.00]
number_of_regions=[1,2,4] # data center 
dimension=[1,2,3,4]
mean_of_subbids=[1,2,3]
mean_of_quantity=[1,1.5,2]

test_case_bids=[]

bid_list_length=[]
count_of_bids=0
i=0
z=0
for a in range(8): #supply-demand
    for b in range(4): #capacities
        for c in range(4): #dimension
            for d in range(3): #regions
                for e in range(3): #mean_of_subbids
                    for f in range(3): #mean_of_quantity
                       
                        auc=Auction(supply_demand_ratios[a],number_of_available_cpu_cores[b],number_of_available_memory[b],number_of_available_network_speed[b],number_of_available_storage[b],dimension[c],number_of_regions[d],mean_of_subbids[e],mean_of_quantity[f])   
                        auc.create_bids()
                        dic={
                            "test-case-no":z,
                            "test-case-name":"TestSD"+str(supply_demand_ratios[a])+"_CPU"+str(number_of_available_cpu_cores[b])+"_Dim"+str(dimension[c])+"_DataCenters"+str(number_of_regions[d])+"_Subbid"+str(mean_of_subbids[e])+"_Quantity"+str(mean_of_quantity[f]),
                            "supply-demand-ratio":supply_demand_ratios[a],
                            "cpu-core":number_of_available_cpu_cores[b],
                            "dimension":dimension[c],
                            "data-center":number_of_regions[d],
                            "mean-of-subbids":mean_of_subbids[e],
                            "mean-of-quantity":mean_of_quantity[f],
                            "bids":auc.total_bids                     
                            }
                        z=z+1    
                        test_case_bids.append(dic) 
                        bid_list_length.append(len(auc.total_bids))
                        if len(auc.total_bids)<20:
                            print(supply_demand_ratios[a],number_of_available_cpu_cores[b],dimension[c],number_of_regions[d],mean_of_subbids[e],mean_of_quantity[f]) 
                            print(auc.supply_demand.index(max(auc.supply_demand)))
                            print(len(auc.total_bids))
                            print(i)
                            i=i+1
                       #     print(auc.total_bids)
                        count_of_bids=count_of_bids+len(auc.total_bids)   
             

#auc=Auction(dimension[3],supply_demand_ratios[0],number_of_regions[2],mean_of_subbids[2],mean_of_quantity[2],number_of_available_cpu_cores[0],number_of_available_memory[0],number_of_available_network_speed[0],number_of_available_storage[0])

test_case_dic={"test-cases":test_case_bids}
with open("test_case_bids.json", "w") as outfile:
    json.dump(test_case_dic, outfile)

#print(auc.total_bids)

print(test_case_bids)    
print(datetime.now()-start)
print("max",max(bid_list_length))
print("min",min(bid_list_length))
print(count_of_bids)

print(z)
