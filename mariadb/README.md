# MongoDB Docker Container

This setup provides a MongoDB server running in a Docker container. Follow the instructions below to set up and test the MongoDB server.

## Prerequisites

- Docker installed on your machine.
- Docker Compose installed on your machine.

## Setup

1. Clone this repository or download the `docker-compose.yml` file.
2. Open a terminal and navigate to the directory containing the `docker-compose.yml` file.
3. Run the following command to start the MongoDB container:

    ```bash
    docker-compose up -d
    ```

    This will start the MongoDB container in detached mode.

## Testing the Connection

To test the connection to the MongoDB server, follow these steps:

1. Open a terminal.
2. Run the following command to access the MongoDB shell inside the running container:

    ```bash
    docker exec -it mongodb mongo
    ```

3. Once inside the MongoDB shell, you can run MongoDB commands. For example, to show the databases, use:

    ```bash
    show dbs
    ```

    To create a new database and collection, and insert a document, you can use:

    ```bash
    use testdb
    db.testcollection.insertOne({ name: "test" })
    db.testcollection.find()
    ```

## Stopping the Container

To stop the MongoDB container, run the following command:

```bash
docker-compose down


### Additional Notes

- The MongoDB container is configured to store its data in a Docker volume named `mongo-data`, ensuring data persistence.
- The MongoDB server is accessible on port `27017` of your localhost.

This setup should provide a clear way to run and test a MongoDB container using Docker and Docker Compose.