FROM amazoncorretto:17.0.1-al2

RUN amazon-linux-extras install docker && \
  curl -L "https://github.com/docker/compose/releases/download/v2.4.1/docker-compose-linux-$(uname -m)" -o /usr/local/bin/docker-compose && \
  chmod +x /usr/local/bin/docker-compose && \
  yum clean all && \
  rm -rf /var/cache/yum

WORKDIR /eventuate-tram-sagas-examples-customers-and-orders-java-development
CMD echo Ready ; sleep 260000

COPY build /tmp-src

RUN echo "Building in image..." && (cd /tmp-src ; ./gradlew testClasses assemble ; cd ; rm -fr /tmp-src)
