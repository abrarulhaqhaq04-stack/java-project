FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && \
    apt-get install -y wget && \
    wget -O mysql-connector.jar \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.4.0/mysql-connector-j-9.4.0.jar && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY src/Main.java .

RUN javac -cp mysql-connector.jar Main.java

EXPOSE 8080

CMD ["java", "-cp", ".:mysql-connector.jar", "Main"]