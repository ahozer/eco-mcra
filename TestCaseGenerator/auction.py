from bid import Bid 

class Auction:
    def __init__(self,supply_demand_ratio,number_of_available_cpu_core,number_of_available_memory,number_of_available_network_speed,number_of_available_storage,dimension,number_of_regions,mean_subbids,mean_quantity):
      
        self.supply_demand_ratio=supply_demand_ratio
        self.number_of_available_cpu_core=number_of_available_cpu_core
        self.number_of_available_memory=number_of_available_memory
        self.number_of_available_network_speed=number_of_available_network_speed
        self.number_of_available_storage=number_of_available_storage
        self.dimension=dimension
        self.number_of_regions=number_of_regions
        self.mean_subbids=mean_subbids
        self.mean_quantity=mean_quantity
        
        self.total_bids=[]
        self.supply_demand=[0,0,0,0]
      

    def calculate_supply_demand_ratio(self):
        sum_of_dimension=[0,0,0,0]
        for bid in self.total_bids:
            for i in range(len(bid)-1):
                for x in range(self.dimension):
                    sum_of_dimension[x]=sum_of_dimension[x]+bid[i][0][x]

        return [sum_of_dimension[0]/self.number_of_available_cpu_core,sum_of_dimension[1]/self.number_of_available_memory,sum_of_dimension[2]/self.number_of_available_network_speed,sum_of_dimension[3]/self.number_of_available_storage]
        


    def create_bids(self):
        
        while max(self.supply_demand) < self.supply_demand_ratio:
            bid_to_submit=Bid(self.dimension,self.number_of_regions,self.mean_subbids,self.mean_quantity).create_bid()
            self.total_bids.append(bid_to_submit)
            self.supply_demand=self.calculate_supply_demand_ratio()
          
      
      #  print(self.supply_demand.index(max(self.supply_demand)))
