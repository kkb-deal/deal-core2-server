# Latest frolvlad/alpine-oraclejdk8:slim
FROM frolvlad/alpine-oraclejdk8:slim

# Update apk mirror
RUN cp /etc/apk/repositories /etc/apk/repositories.bak
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# Install tools
RUN apk update && apk add curl

# Project path
ENV PROJECT_PATH=/kuick/servers

# Set localtime
COPY ./release/docker/localtime /etc/localtime

# Set tmp
VOLUME /tmp

# Workdir
WORKDIR $PROJECT_PATH

# Expose port
EXPOSE 8080