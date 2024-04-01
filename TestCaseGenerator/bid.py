import numpy as np
from statistics import mean
import random
class Bid:
    
    def __init__(self, dimension,number_of_regions,mean_subbids,mean_quantity):
        self.dimension=dimension
        self.number_of_regions=number_of_regions
        self.mean_subbids=mean_subbids
        self.mean_quantity=mean_quantity
        self.base_price=12.04
       
    def create_bid_capacity_list(self):
        normal_distribution_parameters=([16,32],[32,64],[5,10],[300,300])
     #   normal_distribution_parameters=([4,8],[16,32],[5,10],[150,300])

        capacity_list=[]
        for x in range(self.dimension):
            cap_list=np.random.normal(normal_distribution_parameters[x][0], normal_distribution_parameters[x][1], 1)
            cap=int(round(cap_list[0]))
            if cap<=0 and x!=3:
                cap=normal_distribution_parameters[x][0]

            if cap<0 and x==3:
                cap=normal_distribution_parameters[x][0]   

            if x==1 and cap<capacity_list[0]:
                cap=capacity_list[0]

            capacity_list.append(cap)        
        
        return capacity_list
            
   
    def poisson_distribution_generate_number(self,lamb):
        number = np.random.poisson(lam=lamb, size=1)
        poisson_number=int(number[0])
        return poisson_number if poisson_number!=0  else  1 

    def add_region_alternatives_to_capacity_list(self,capacity_list,alternative):
       
        alternative_list= random.sample(range(1, self.number_of_regions+1), alternative)
        all_capacity_lists=[]
        for i in range(self.number_of_regions):
            capacity_list.append(0)

        for i in range(alternative):
            copy_cap_list = capacity_list[:] 
            copy_cap_list[self.dimension-1+alternative_list[i]]=1
            all_capacity_lists.append(copy_cap_list)

        return tuple(all_capacity_lists) 
          



    def create_bid(self):
        
 
        
        subbid_count=self.poisson_distribution_generate_number(self.mean_subbids)
        subbids=[]
        for x in range(subbid_count):
            alternative=random.randint(1,self.number_of_regions)
            for y in range(alternative):
                cap_list=self.create_bid_capacity_list()
                if self.number_of_regions>1:
                    alternative_lists=self.add_region_alternatives_to_capacity_list(cap_list,alternative)
                else:
                    alternative_lists=tuple([cap_list])    

            quantity_count=self.poisson_distribution_generate_number(self.mean_quantity)
            subbid=alternative_lists+(quantity_count,)     
            subbids.append(subbid)   

        bid=tuple(subbids)    
        raw_price=0
        for subbid in bid:
            raw_price=raw_price+self.calculate_raw_price_of_subbid(subbid)
            
        value_of_bid=self.decide_price_for_bid(raw_price)
        
        final_bid=bid+(value_of_bid,)
        return final_bid
    #y=(([2,3,4,5],1),([3,4,5,6],2),230)
    def calculate_raw_price_of_subbid(self,subbid):
        raw_prices=[]    
        single_prices_for_unit=[self.base_price]
        y=0
      
        while  y<len(subbid)-1:
            raw_prices.append(0)
            for x in range(len(single_prices_for_unit)):
        
                raw_prices[y]=raw_prices[y]+subbid[y][x]*single_prices_for_unit[x]
            
            y=y+1
        
        return mean(raw_prices)*subbid[y]

    def decide_price_for_bid(self,raw_price):
        mu, sigma = raw_price, raw_price/4
        gaussian_distribution_for_bid_price = np.random.normal(mu, sigma, 1)
        price_to_offer=  gaussian_distribution_for_bid_price[0] 
        if price_to_offer < 0.5*sigma:
            price_to_offer= 0.5*sigma
            
        return int(round(price_to_offer))










      



