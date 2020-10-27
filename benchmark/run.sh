DB_URL='jdbc:postgresql://localhost/abd'
DB_USER='vasco'
DB_PWD='segredo'

# generate jar executable
mvn clean package jar:jar

# run benchmark
echo "\n\nPopulate\n"
java -jar target/benchmark-1.0-SNAPSHOT.jar -d ${DB_URL} -U ${DB_USER} -P ${DB_PWD} -p

echo "\n\nRun Benchmark\n"
java -jar target/benchmark-1.0-SNAPSHOT.jar -d ${DB_URL} -U ${DB_USER} -P ${DB_PWD} -c 16 -R 120 -x