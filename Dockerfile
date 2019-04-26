FROM java:8
RUN mkdir -p /usr/src/app
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN javac -cp src:libs/* src/net/edudb/server/Server.java
EXPOSE 9999
CMD ["java", "-cp", "src:libs/*", "net.edudb.server.Server"]