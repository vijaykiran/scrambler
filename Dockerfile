FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/scrambler.jar /scrambler/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/scrambler/app.jar"]
