import csv

def process_file(input_file, output_file):
    with open(input_file, 'r') as infile, open(output_file, 'w', newline='') as outfile:
        csv_reader = csv.reader(infile)
        csv_writer = csv.writer(outfile)

        for row in csv_reader:
            # 只保留前42个数据
            # print(row[41])
            # break
            row_to_save = row[:41]
            csv_writer.writerow(row_to_save)

if __name__ == '__main__':
    source_file='kddcup.data_10_percent.csv'
    handled_file='kddcup.data_10_percent1.csv'
    process_file(source_file, handled_file)
    print("Processing complete. Output saved to:",handled_file)
