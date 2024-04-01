import gurobipy as gp
from gurobipy import GRB
import json 
import sys, getopt

def linear_relax_solve(bids,dimension,phy_resource_capacities,phymac_idlefullenergies,data_centers_physical_machines,index):
    pue=1.57
    allocation_time=720
    data_cen_energy_costs=[0.128,0.180,0.261,0.339]
    model = gp.Model("Linear-Relax-Sorter")
    model.setParam(GRB.Param.Threads,4)
    
    amount_of_bids = len(bids)
    amount_of_phy_machines=len(phy_resource_capacities)
    
    
    result_dictionary={}
    
    offered_prices_of_bids=[]
    for k in range(amount_of_bids):
        length_of_bid=len(bids[k])
        offered_prices_of_bids.append(bids[k][length_of_bid-1])

    bids_accepted_or_not=[]             
    for k in range(amount_of_bids):
        bids_accepted_or_not.append(model.addVar(lb=0,ub=1,vtype=GRB.CONTINUOUS, name="x"+str(k)))
    
    phy_machine_powered_on_or_off=[]
    for i in range(amount_of_phy_machines):
        phy_machine_powered_on_or_off.append(model.addVar(lb=0,ub=1,vtype=GRB.CONTINUOUS, name="a"+str(i)))

    amount_of_requested_resources_mapped_to_phymachines={}
    
    for k in range(amount_of_bids):
        amount_of_subbids=len(bids[k])-1
        for l in range(amount_of_subbids):
            amount_of_substituble_resources=len(bids[k][l])-1
            for m in range(amount_of_substituble_resources):
                for i in range(amount_of_phy_machines):
                    str_key=str(k)+str(l)+str(m)+str(i)
                    amount_of_requested_resources_mapped_to_phymachines[str_key]=model.addVar(lb=0,ub=GRB.INFINITY,vtype=GRB.CONTINUOUS, name="y"+str(k)+"-"+str(l)+"-"+str(m)+"-"+str(i))
    
    utilization_vectors={}

    for i in range(amount_of_phy_machines):
        for d in range(dimension):
            str_key=str(i)+str(d)
            utilization_vectors[str_key]=model.addVar(lb=0,vtype=GRB.CONTINUOUS, name="u"+str(i)+str(d))
    
    #z0 variable is going to hold the total price of accepted bids
    z0=model.addVar(lb=0,vtype=GRB.CONTINUOUS, name="z0")
    #z1 variable is going to hold the total idle energy cost of all  machines
    z1=model.addVar(lb=0,vtype=GRB.CONTINUOUS, name="z1")

    #z2 variable is going to hold the total utilization energy cost of all machines
    z2=model.addVar(lb=0,vtype=GRB.CONTINUOUS, name="z2")
    
    #z3 variable is going to hold how many machines are powered on
    z3=model.addVar(lb=0,vtype=GRB.CONTINUOUS, name="z3")


    model.addConstr(z3==sum(phy_machine_powered_on_or_off),"z33")

    #for each machine, utilization vector is <= its capacity if its power on, 0 if its powered off 
    for i in range(amount_of_phy_machines):
        for d in range(dimension):
            str_key=str(i)+str(d)
            model.addConstr(utilization_vectors[str_key]<=phy_resource_capacities[i][d]*phy_machine_powered_on_or_off[i],"c"+str(i)+str(d))        
    

    #The utilization vector of feature d in phy mac i is the sum of requested amount of feature d placed in phy mac i.
    for i in range(amount_of_phy_machines):
        for d in range(dimension):
            sum_of_substituble_resources=[]
            for k in range(amount_of_bids):
                amount_of_subbids=len(bids[k])-1
                for l in range(amount_of_subbids):
                    amount_of_substituble_resources=len(bids[k][l])-1
                    for m in range(amount_of_substituble_resources):
                        str_key=str(k)+str(l)+str(m)+str(i)   
                        sum_of_substituble_resources.append(amount_of_requested_resources_mapped_to_phymachines[str_key]*bids[k][l][m][d])
            
            str_key_2=str(i)+str(d)
            model.addConstr(sum(sum_of_substituble_resources)==utilization_vectors[str_key_2],"d"+str(i)+str(d))
                    
            
    
    #if a bid is satisfied , for all its subbids, requested amount of resources are allocated
    for k in range(amount_of_bids):
        amount_of_subbids=len(bids[k])-1
        for l in range(amount_of_subbids):
            amount_of_substituble_resources=len(bids[k][l])-1
            sum_of_substituble_resources_mapped_to_phymac=[]
            for m in range(amount_of_substituble_resources):
                for i in range(amount_of_phy_machines):
                    str_key=str(k)+str(l)+str(m)+str(i)  
                    sum_of_substituble_resources_mapped_to_phymac.append(amount_of_requested_resources_mapped_to_phymachines[str_key])
            

            int_quantity_of_subbid=int(bids[k][l][amount_of_substituble_resources])
            model.addConstr(sum(sum_of_substituble_resources_mapped_to_phymac)==int_quantity_of_subbid*bids_accepted_or_not[k],"e"+str(k)+str(l))        

            
    sum_of_prices=sum([bids_accepted_or_not[k]*offered_prices_of_bids[k] for k in range(amount_of_bids)])
    
    model.addConstr(sum_of_prices == z0 ,"z00")
    
    
#   sum_of_total_energy_cost=0
    sum_of_idle_energy_cost=0
    sum_of_util_energy_cost=0
    for i in range(amount_of_phy_machines):
        data_center_pm_in=data_centers_physical_machines[i]  
        energy_cost_in_data_center=data_cen_energy_costs[data_center_pm_in]

        idle_power=(phymac_idlefullenergies[i][0]/1000)*phy_machine_powered_on_or_off[i]*pue*allocation_time*energy_cost_in_data_center
        if phymac_idlefullenergies[i][1]!=0:
            utilization_power=((phymac_idlefullenergies[i][1]-phymac_idlefullenergies[i][0])/1000)*(utilization_vectors[str(i)+"0"]/phy_resource_capacities[i][0])*pue*allocation_time*energy_cost_in_data_center
        else:
            utilization_power=0

        sum_of_idle_energy_cost=sum_of_idle_energy_cost + idle_power
        sum_of_util_energy_cost=sum_of_util_energy_cost+utilization_power
#     sum_of_total_energy_cost=sum_of_total_energy_cost+idle_power+utilization_power

    model.addConstr(sum_of_idle_energy_cost==z1,"z11")    
    
    model.addConstr(sum_of_util_energy_cost==z2,"z22")

    #objective function is sum_of_prices-total energy cost

    model.setObjective(z0-z1-z2, GRB.MAXIMIZE)

    

    model.optimize()        

    status=model.status

    if status != 3 and status !=4 : 

        model.write("linrelax_results\linrel_solution"+str(index)+".sol")
        
        optimal_value=model.objVal 
        result_dictionary["obj-value"]=optimal_value
        result_dictionary["linrelax-runtime"]=model.Runtime
        result_dictionary["solution-stat"] =status
        profit_dictionary={}
        for i in range(amount_of_bids):
            bid_profit_value=model.getVarByName("x"+str(i))
    
            profit_dictionary[i]=bid_profit_value.X
        
        result_dictionary["profit_dictionary"] =profit_dictionary

        return result_dictionary


n = len(sys.argv)
test_index_s=sys.argv[1]
test_case_index=int(test_index_s)
#bid_index=0
f = open('test_case_bids.json')
data = json.load(f)
test_case_no= data["test-cases"][test_case_index]["test-case-no"]
test_case_name= data["test-cases"][test_case_index]["test-case-name"]
supply_demand_ratio= data["test-cases"][test_case_index]["supply-demand-ratio"]

bids = data["test-cases"][test_case_index]["bids"]
dimension=data["test-cases"][test_case_index]['dimension']
num_of_Dcs=data["test-cases"][test_case_index]['data-center']
cpu_core = int(data["test-cases"][test_case_index]["cpu-core"])
mean_of_subbids = int(data["test-cases"][test_case_index]["mean-of-subbids"])
mean_of_quantity = int(data["test-cases"][test_case_index]["mean-of-quantity"])

cap_factor=int(cpu_core /512)
phymacs=[]
phymacvalues=[[128,256,100,1900],[44,64,20,900],[32,384,10,600],[128,512,100,3800],[80,256,50,1900],[36,64,10,900],[64,256,50,1900]]
p=0
change=cap_factor/num_of_Dcs
z=0
for i in range(cap_factor):
    for phymac in phymacvalues:
        if num_of_Dcs==1:
            phymaclist=phymac
        else:
            datacenterlist=[]
            for j in range(num_of_Dcs):
                datacenterlist.append(0)
            datacenterlist[p]=1000000000    
            phymaclist=phymac+datacenterlist

        phymacs.append(phymaclist)   

    z=z+1
    if z==change:
        p=p+1
        z=0    

phy_datacenter={}
k=0
p=0

for j in range(num_of_Dcs):

    while k<(len(phymacs)/num_of_Dcs) and k+p<len(phymacs):
    
        phy_datacenter[k+p]=j  
        k=k+1  
    p=p+round(len(phymacs)/num_of_Dcs)    
    k=0

phymac_idlefullenergy=[]
for i in range(cap_factor):
    phymac_idlefullenergy.append([163,423])
    phymac_idlefullenergy.append([46.6,247])
    phymac_idlefullenergy.append([114,367])
    phymac_idlefullenergy.append([99.2,412])
    phymac_idlefullenergy.append([119,609])
    phymac_idlefullenergy.append([48.1,273])
    phymac_idlefullenergy.append([137,491])  

if num_of_Dcs == 1 :
    total_dimension=dimension
else:
    total_dimension=dimension+num_of_Dcs    
results=linear_relax_solve(bids,total_dimension,phymacs,phymac_idlefullenergy,phy_datacenter,test_case_no)

if results != None:
    linrelax_results=[]
    dic={
    "test-case-no":test_case_no,
    "test-case-name":test_case_name,
    "supply-demand-ratio":supply_demand_ratio,
    "cpu-core":cpu_core,
    "dimension":dimension,
    "data-center":num_of_Dcs,
    "mean-of-subbids":mean_of_subbids,
    "mean-of-quantity":mean_of_quantity,
    "objective-value":results["obj-value"],
    "linrelax-runtime":results["linrelax-runtime"],
    "status":results["solution-stat"],
    "profit-dictionary":results["profit_dictionary"]                  
    }

    linrelax_results.append(dic)

    linrelax_dic={"linear-relax-results":linrelax_results}
    with open("linrelax_results\linearrelax_"+str(test_case_no)+".json", "w") as outfile:
        json.dump(linrelax_dic, outfile)