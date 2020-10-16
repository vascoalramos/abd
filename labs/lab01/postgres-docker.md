# Install PostgreSQL with Docker

1. Get PostgreSQL image: 
    ```bash
    docker pull postgres:12
    ```

2. Create a folder to hold your database data (a volume link): 
    ```bash
    mkdir /path/../data
    ```

2. Execute docker container:
    ```bash
    docker run --user $(id -u):$(id -g) -v /full/path/.../data:/var/lib/postgresql/data -e POSTGRES_DB=<db_name> -e POSTGRES_USER=<user_name> -e POSTGRES_PASSWORD=<pwd> -p 5432:5432 --name postgres -it postgres:12
    ```

3. On other terminal, enter the database:
    ```bash
    docker exec -it postgres psql -U <user_name> <db_name>
    ```

4. To start your container, if already created:
    ```bash
    docker start -i postgres
    ```