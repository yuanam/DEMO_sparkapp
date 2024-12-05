#coding:utf-8
 
import numpy as np
import pandas as pd
import csv
import time
global label_list  #label_list为全局变量
 
 
def Find_Maxmin():
    source_file = "kddcup.data_10_percent1.csv" 
    handled_file = "kddcup_1.data_10_percent_corrected.csv"
    dic = {}
    data_file = open(handled_file,'w',newline='')
    with open(source_file,'r') as data_source:
        csv_reader=csv.reader(data_source)       
        count = 0        
        row_num = ""        
        for row in csv_reader:
            count = count+1        
            row_num = row       
        with open(source_file,'r') as data_source:
            csv_reader=csv.reader(data_source)
            final_list = list(csv_reader)
            print(final_list)
            jmax = []
            jmin = []
            for k in range(0, len(final_list)):                              
                jmax.append(max(final_list[k]))
                jmin.append(min(final_list[k]))
            jjmax = float(max(jmax))  
            jjmin = float(min(jmin))           
            listss = []  
            for i in range(0,len(row_num)):
                lists = [] 
                with open(source_file,'r') as data_source:
                    csv_reader=csv.reader(data_source)           
                    for row in csv_reader: 
                        if (jjmax-jjmin) == 0:
                            x = 0
                        else:
                            x = (float(row[i])-jjmin) / (jjmax-jjmin)                      
                        lists.append(x)
                listss.append(lists)
            for j in range(0,len(listss)):                                         
                dic[j] = listss[j]
            df = pd.DataFrame(data = dic)
            df.to_csv(data_file,index=False,header=False)
            data_file.close()
 
 
 
if __name__=='__main__':
    start_time=time.perf_counter()
    global label_list   #声明一个全局变量的列表并初始化为空
    label_list=[]
    Find_Maxmin()
    end_time=time.perf_counter()
    print("Running time:",(end_time-start_time))  #输出程序运行时间