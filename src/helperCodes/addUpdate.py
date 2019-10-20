import numpy as np
import random
import math
from scipy import sparse
from os import listdir
from os.path import isfile, join

MAX_INT = 2147483647
SOURCE_FOLDER = "./"
UPDATE_FOLDER = "./dataWithUpdate/"

def random_stream(file, min_value, max_value, tag, isTurnstile, numItems):
    read_file = SOURCE_FOLDER + file
    write_file = UPDATE_FOLDER + tag + '_range-' + str(min_value) + "-" + str(max_value) + "_" + file

    read_fp = open(read_file, "r")
    write_fp = open(write_file, "w")

    record = np.zeros(numItems+1, dtype = int)
    record_count = np.zeros(numItems+1, dtype = int)

    print("####INFO: generating started")

    for line in read_fp:
        data = line.rstrip().split(',');
        sample = int(data[0])
        current_value = record[sample]
        update = 0
        while (update == 0):
            update = random.randint(min_value, max_value)

        if isTurnstile:
            while (update + current_value < 0) or (update == 0):
                update = random.randint(min_value, max_value)

        if (update + current_value > MAX_INT):
            update = 0

        record[sample] += update
        record_count[sample] += 1
        write_fp.write(str(sample) + "," + str(update) + "\n")

    print("####INFO: generating finished, start write record")

    write_fp.write("########INFORMATION SECTION########\n")
    for i in range(len(record)):
        if record[i] != 0:
            write_fp.write("#," + str(i) + "," + str(record[i]) + "," + str(record_count[i]) + "\n")

    write_fp.close()
    return

# generate insertion-only stream
def insertion_only(file, min_value, max_value, num_items):
    if min_value < 0:
        raise ValueError("min_value smaller than 0 for insertion-only stream")

    random_stream(file, min_value, max_value, "insertionOnly", False, num_items)

# generate general stream
def general(file, min_value, max_value, num_items):
    if abs(min_value) != abs(max_value):
        raise ValueError("absolute values of min_value and max_value be the same for general stream")

    random_stream(file, min_value, max_value, "general", False, num_items)

# generate turnstile stream
def turnstile(file, min_value, max_value, num_items):
    random_stream(file, min_value, max_value, "turnstile", True, num_items)

# get all data files in source directory
only_files = [f for f in listdir(SOURCE_FOLDER) if isfile(SOURCE_FOLDER + f) and "txt" in (SOURCE_FOLDER + f)]
NUM_ITEMS = 100

print("####INFO: start generating")
for file in only_files:
    print("####INFO: start file: " + file)
    for value in [10]:
        insertion_only(file, 1, value, NUM_ITEMS)
        print("####INFO: finish insertion-only stream")
        turnstile(file, -value, value, NUM_ITEMS)
        print("####INFO: finish turnstile stream")
        general(file, -value, value, NUM_ITEMS)
        print("####INFO: finish general stream")

        print("####INFO: finish range: " + str(value))

    print("####INFO: finish file: " + file)
