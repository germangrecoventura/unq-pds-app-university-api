echo "Initiating the wait to complete the database"

while ! nc -z ${MYSQL_HOST} ${MYSQL_PORT}; do sleep 1; done

echo "Finished waiting to complete database"

java -jar /app-universidad.jar --server.port=${PORT}