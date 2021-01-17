FROM registry.kuick.cn/cc/deal-core2-server:base

# Copy jar to docker
COPY ./build/libs/*.jar $PROJECT_PATH/

# Copy tingyun to docker
COPY release/apm/tingyun $PROJECT_PATH/tingyun

# Copy arms to docker
COPY release/apm/armsagent $PROJECT_PATH/armsagent

# Copy scripts to docker
COPY release/docker/config.sh $PROJECT_PATH
COPY release/docker/run.sh $PROJECT_PATH

# Chmod scripts
RUN chmod 755 $PROJECT_PATH/config.sh
RUN chmod 755 $PROJECT_PATH/run.sh

# ENV
# Optional: -javaagent:tingyun/tingyun-agent-java.jar
# Optional: -javaagent:armsagent/arms-bootstrap-1.7.0-SNAPSHOT.jar
ENV JAVA_OPTS="-Duser.timezone=GMT+8 -Xms512m -Xmx2048m"
# -javaagent:armsagent/arms-bootstrap-1.7.0-SNAPSHOT.jar

# Startup auto run scripts
ENTRYPOINT ["./run.sh"]