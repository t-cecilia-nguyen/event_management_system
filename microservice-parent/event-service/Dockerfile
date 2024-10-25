#--- Image oriented
#BUILD STATE
#(Start from base image - > gradle:)
#---

FROM gradle:8-jdk22-alpine AS builder

#---
#Copy everything from my source code to this image location (all are linux image base)
#flag: both owner and group permission
#---
COPY --chown=gradle:gradle . /home/gradle/src

#goto this folder
WORKDIR /home/gradle/src

#execute this build command except test command, we should have a jar file after this cmd
RUN gradle build -x test


#---
#END BUULD STATE
#
#PAKAGE STATE
#(start with base imgage FROM)
#---

FROM openjdk:22-jdk

RUN mkdir /app


#after build executed, we will have these files,
#then copy all .jar files to our new image inside app folder called product-service.jar file
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/event-service.jar


#set environtment vars

ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=password

#Inform Docker that the container listens on the specified network ports at runtime.
EXPOSE 8095

#Allow you to configure a container that will run as an executable
#or basically how to run the program (Shell form, and EXEC form)

ENTRYPOINT ["java", "-jar", "/app/event-service.jar"]