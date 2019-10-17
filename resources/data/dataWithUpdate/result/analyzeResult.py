import numpy as np
from os import listdir
from os.path import isfile, join

SOURCE_FOLDER = "./"
SOURCE_FILE = "./constant_max100-zipf_s0.6_len1000_runningResult_error0.01_badprob0.001.txt"
WRITE_FILE = "./output/output.txt"
PERCENTILE_E = 100 - 0.01

def analyze(file, w_fp, num_items):
    read_fp = open(file, "r")

    distinct_count, total_count = 0, 0
    expected_count = np.zeros(num_items, dtype='int32')
    l0Insert_count = np.zeros(num_items, dtype='int32')
    l0General_count = np.zeros(num_items, dtype='int32')
    l0Turnstile_count = np.zeros(num_items, dtype='int32')
    l0Pairwise_count = np.zeros(num_items, dtype='int32')
    l0Insert_fail, l0General_fail, l0Turnstile_fail, l0Pairwise_fail = 0, 0, 0, 0
    l0Insert_error, l0General_error, l0Turnstile_error, l0Pairwise_error = 0, 0, 0, 0

    # loop all lines, extract data
    for line in read_fp:
        data = line.rstrip().split(',')

        if line.startswith("#"):
            if line.startswith("###"):
                continue

            index = int(data[1]) - 1
            expected_count[index] = 1
            distinct_count += 1

        else:
            if data[0].split(':')[1] != "FAIL":
                index = int(data[0].split(':')[1]) - 1

                if expected_count[index] == 0:
                    l0Insert_error += 1
                else:
                    l0Insert_count[index] += 1
            else:
                l0Insert_fail += 1

            if data[1].split(':')[1] != "FAIL":
                index = int(data[1].split(':')[1]) - 1

                if expected_count[index] == 0:
                    l0General_error += 1
                else:
                    l0General_count[index] += 1
            else:
                l0General_fail += 1

            if data[2].split(':')[1] != "FAIL":
                index = int(data[2].split(':')[1]) - 1

                if expected_count[index] == 0:
                    l0Turnstile_error += 1
                else:
                    l0Turnstile_count[index] += 1
            else:
                l0Turnstile_fail += 1

            if data[3].split(':')[1] != "FAIL":
                index = int(data[3].split(':')[1]) - 1

                if expected_count[index] == 0:
                    l0Pairwise_error += 1
                else:
                    l0Pairwise_count[index] += 1
            else:
                l0Pairwise_fail += 1

            total_count += 1

    print(expected_count)
    print(l0Insert_count)
    print(l0Insert_fail)
    print(l0Insert_error)
    print(l0Turnstile_count)
    print(l0Turnstile_fail)
    print(l0Turnstile_error)
    print(l0General_count)
    print(l0General_fail)
    print(l0General_error)
    print(l0Pairwise_count)
    print(l0Pairwise_fail)
    print(l0Pairwise_error)

    norm_term = distinct_count / total_count

    # calculate normalized std
    l0Insert_norm_std = np.std(l0Insert_count) * norm_term
    l0General_norm_std = np.std(l0General_count) * norm_term
    l0Turnstile_norm_std = np.std(l0Turnstile_count) * norm_term
    l0Pairwise_norm_std = np.std(l0Pairwise_count) * norm_term

    # get deviation
    l0Insert_dev = []
    l0General_dev = []
    l0Turnstile_dev = []
    l0Pairwise_dev = []

    for i in range(num_items):
        if expected_count[i] == 0:
            continue
        else:
            expected_count[i] = int(total_count / distinct_count)
            l0Insert_dev.append( float(abs(l0Insert_count[i] - expected_count[i]) / expected_count[i]) )
            l0General_dev.append( float(abs(l0General_count[i] - expected_count[i]) / expected_count[i]) )
            l0Turnstile_dev.append( float(abs(l0Turnstile_count[i] - expected_count[i]) / expected_count[i]) )
            l0Pairwise_dev.append( float(abs(l0Pairwise_count[i] - expected_count[i]) / expected_count[i]) )

    # calculate average deviation
    l0Insert_avg_dev = np.mean(l0Insert_dev) * norm_term
    l0General_avg_dev = np.mean(l0General_dev) * norm_term
    l0Turnstile_avg_dev = np.mean(l0Turnstile_dev) * norm_term
    l0Pairwise_avg_dev = np.mean(l0Pairwise_dev) * norm_term

    # calculate (1 - delta) percentile deviation
    l0Insert_percentile_dev = np.percentile(l0Insert_dev, PERCENTILE_E, interpolation='nearest')
    l0General_percentile_dev = np.percentile(l0General_dev, PERCENTILE_E, interpolation='nearest')
    l0Turnstile_percentile_dev = np.percentile(l0Turnstile_dev, PERCENTILE_E, interpolation='nearest')
    l0Pairwise_percentile_dev = np.percentile(l0Pairwise_dev, PERCENTILE_E, interpolation='nearest')

    w_fp.write("norm_std,")
    w_fp.write("insertionOnly:" + str(l0Insert_norm_std) + ",general:" + str(l0General_norm_std) + ",turnstile:" + str(l0Turnstile_norm_std) + ",pairwise:" + str(l0Pairwise_norm_std) + "\n")
    w_fp.write("avg_dev,")
    w_fp.write("insertionOnly:" + str(l0Insert_avg_dev) + ",general:" + str(l0General_avg_dev) + ",turnstile:" + str(l0Turnstile_avg_dev) + ",pairwise:" + str(l0Pairwise_avg_dev) + "\n")
    w_fp.write("percentile_dev,")
    w_fp.write("insertionOnly:" + str(l0Insert_percentile_dev) + ",general:" + str(l0General_percentile_dev) + ",turnstile:" + str(l0Turnstile_percentile_dev) + ",pairwise:" + str(l0Pairwise_percentile_dev) + "\n")
    w_fp.write("uniqueNum," + str(distinct_count) + "\n")

# get all data files in source directory
only_files = [f for f in listdir(SOURCE_FOLDER) if isfile(SOURCE_FOLDER + f) and "txt" in (SOURCE_FOLDER + f)]

NUM_ITEMS = 100

fp = open(WRITE_FILE, "a")

print("####INFO: Start analyze")

for file in only_files:
    if "data" in file:
        continue

    print("####INFO: analyze file:", file)
    read_file = SOURCE_FOLDER + file
    fp.write("####file,"+file+"\n")
    analyze(file, fp, NUM_ITEMS)

print("####INFO: Finish analyze")
