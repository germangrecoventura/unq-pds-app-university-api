echo "Initiating the wait to complete the database"

while ! nc -z mysqldb 3306 ; do sleep 1 ; done

echo "Finished waiting to complete database"

java -jar /app/app-universidad.jar --server.port=${PORT}