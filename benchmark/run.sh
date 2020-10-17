# generate jar executable
mvn clean package jar:jar

# run benchmark
DB_URL='jdbc:postgresql://localhost/abd'
DB_USER='vasco'
DB_PWD='segredo'
java -jar target/benchmark-1.0-SNAPSHOT.jar -d ${DB_URL} -U ${DB_USER} -P ${DB_PWD} -p