# LoggerUtil

Examples:
To generate log files with 50000 records use command:
java -jar LoggerUtil.jar -g 50000

To print out logs by username 'hurtfulabout' in 3 threads: 
java -jar LoggerUtil.jar -c 3 -p /Users/alisa/Documents/logs/  -u hurtfulabout

To print out logs by time period in 1 thread:
java -jar LoggerUtil.jar -p /Users/alisa/Documents/logs/  -s "2020/06/01 16:30:00" -e "2020/06/01 16:31:00"

To print out logs by date in 2 threads: 
java -jar LoggerUtil.jar -c 2 -p /Users/alisa/Documents/logs/  -d "2020/06/01"

To group user names with number of records in 1 thread in default logs folder:
java -jar LoggerUtil.jar -a

All commands results is possibly to provide output file:
java -jar LoggerUtil.jar -c 3 -p /Users/alisa/Documents/logs/  -u hurtfulabout -o output.log


