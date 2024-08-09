v1.0 3/2024 Movie ticket booking website  
v1.1 21/4/2024 10:55 update:  
    ~ Exception handling  
    ~ Limit error warnings  
    ~ Reading the code is easier because some redundant code has been removed  

v1.2 3/8/2024 16:56 config docker:  
    How to use with CMD:  
    # Pull image:  
        ~ docker pull mysql:8.0.36-debian  
        ~ docker pull nguyenminhquan2108/cinema-server:0.0.1  
        ~ docker pull nguyenminhquan2108/cinema-client:0.0.1  
    # Create network in docker:  
        ~ docker network create cinema-network (cinema-network is name network in docker)  
    # Container:  
        ~ docker run --network cinema-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=\<optional password\> -d mysql:8.0.36-debian  
        ~ Create database in MySQL with name database 'cinemamanagement':  
            - docker exec -it mysql mysql -u root -p  
            - Type password: \<optional password\>  
            - Type: create database cinemamanagement;  
            - Type check database creation: show databases;  
            - Type: exit  
        ~ docker run --name cinema-server --network cinema-network -p 8080:8080 -e DB_URL=jdbc:mysql://mysql:3306/cinemamanagement -e DB_PASSWORD=\<optional password\> nguyenminhquan2108/cinema-server:0.0.1  
        ~ docker run --name cinema-client -p 5173:5173 -d nguyenminhquan2108/cinema-client:0.0.1  
    # Use app:  
        ~ Login account ADMIN with email: nnmhqn2003@gmail.com, password: 1  

Node: jdbc:mysql://host.docker.internal:3306/cinemamanagement  