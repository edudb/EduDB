#FROM openjdk:8-jdk-alpine
#RUN mkdir -p /usr/src/app
#COPY . /usr/src/app
#WORKDIR /usr/src/app
#RUN javac -cp src:libs/* src/net/edudb/server/Server.java
#EXPOSE 9999
#CMD ["java", "-cp", "src:libs/*", "net.edudb.server.Server"]


FROM openjdk:17
RUN mkdir -p /usr/src/app
COPY Core/target/old-server.jar /usr/src/app
WORKDIR /usr/src/app
EXPOSE 9999
CMD ["java", "-jar", "old-server.jar"]
