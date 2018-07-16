FROM java:8
COPY ./target/example-1.0-SNAPSHOT.jar .
EXPOSE 7777
EXPOSE 7778
CMD java -jar example-1.0-SNAPSHOT.jar