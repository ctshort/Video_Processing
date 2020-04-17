import os
import sys
import getopt

if(len(sys.argv) != 2):
        print("Correct Usage: submit_file_generator <dir_of_vids_to_filter>")
else:
        print("Preparing to convert all videos in "+str(sys.argv[1])+" directory using ASCII filter\n\n")


i = 0
for vid in os.listdir(str(sys.argv[1])):
        file = open("ascii.txt", "w")

        file.write("#Condor Submit File for ASCII Conversion of subVid"+str(i)+".mp4\n")

        file.write("universe = vanilla\n")
        file.write("executable = C:\\ProgramData\\Oracle\\Java\\javapath\\java.exe\n")
        file.write("arguments= -jar ASCII.jar subVid"+str(i)+".mp4 subVid"+str(i)+".mp4\n")
        file.write("transfer_input_files = ASCII.jar, "+str(sys.argv[1])+"/subVid"+str(i)+".mp4\n\n")

        file.write("error = error.ascii\n")
        file.write("log = log.ascii\n\n")

        file.write("transfer_executable = FALSE\n")
        file.write("copy_to_spool = FALSE \n\n")

        file.write("Requirements = OpSys == \"WINDOWS\" && Arch == \"X86_64\"\n\n")

        file.write("should_transfer_files = YES \n")
        file.write("when_to_transfer_output = ON_EXIT\n\n")

        file.write("queue")
        file.close()

        os.system("condor_submit ascii.txt")
        i = i+1

print("All jobs submitted to Condor")