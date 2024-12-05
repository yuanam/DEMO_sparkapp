#coding:utf-8
 
import numpy as np
import pandas as pd
import csv
import time
import math
global label_list
 
def Handle_data():
    source_file = "kddcup.data_10_percent1.csv"
    handled_file = "kddcup_normal.data_10_percent.csv"
    data_file = open(handled_file,'w',newline='')
    with open(source_file,'r') as data_source:
        csv_reader = csv.reader(data_source)       
        count = 0        
        row_num = ""        
        for row in csv_reader:
            count = count+1        
            row_num = row               
        sum = np.zeros(len(row_num))  #和
        sum.astype(float) 
        avg = np.zeros(len(row_num)) #平均值
        avg.astype(float)
        stadsum = np.zeros(len(row_num)) #绝对误差
        stadsum.astype(float)        
        stad = np.zeros(len(row_num)) #平均绝对误差
        stad.astype(float)  
        dic = {} 
        lists = [] 
        for i in range(0,len(row_num)):
            with open(source_file,'r') as data_source:
                csv_reader = csv.reader(data_source)
                for row in csv_reader:
                    sum[i] += float(row[i])
            avg[i] = sum[i] / count    #每一列的平均值求得                    
            with open(source_file,'r') as data_source:
                csv_reader = csv.reader(data_source)
                for row in csv_reader:
                    stadsum[i] += math.pow(abs(float(row[i]) - avg[i]), 2)
            stad[i] = stadsum[i] / count #每一列的平均绝对误差求得       
            with open(source_file,'r') as data_source:
                csv_reader = csv.reader(data_source)
                list = []                                                              
                for row in csv_reader:                        
                    temp_line=np.array(row)   #将每行数据存入temp_line数组里                  
                    if avg[i] == 0 or stad[i] == 0:
                        temp_line[i] = 0
                    else:
                        temp_line[i] = abs(float(row[i]) - avg[i]) / stad[i]                                             
                    list.append(temp_line[i])                          
                lists.append(list)                
        for j in range(0,len(lists)):                                                                
            dic[j] = lists[j] #将每一列的元素值存入字典中                                                                                                          
        df = pd.DataFrame(data = dic)
        df.to_csv(data_file,index=False,header=False)                              
        data_file.close()
 
 
if __name__=='__main__':
    start_time=time.perf_counter()
    global label_list   #声明一个全局变量的列表并初始化为空
    label_list=[]
    Handle_data()
    end_time=time.perf_counter()
    print("Running time:",(end_time-start_time))  #输出程序运行时间