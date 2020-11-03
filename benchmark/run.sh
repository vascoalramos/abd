DB_URL='jdbc:postgresql://localhost/abd'
DB_USER='vasco'
DB_PWD='segredo'

# generate jar executable
mvn clean && mvn package && mvn jar:jar

# run benchmark
printf "\n\nPopulate\n"
java -jar target/benchmark-1.0-SNAPSHOT.jar -d ${DB_URL} -U ${DB_USER} -P ${DB_PWD} -p

printf "\n\nRun Benchmark\n"
java -jar target/benchmark-1.0-SNAPSHOT.jar -d ${DB_URL} -U ${DB_USER} -P ${DB_PWD} -c 16 -x