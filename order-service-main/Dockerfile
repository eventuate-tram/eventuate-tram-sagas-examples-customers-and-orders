ARG baseImageVersion
FROM eventuateio/eventuate-examples-docker-images-spring-example-base-image:$baseImageVersion
ARG serviceImageVersion
COPY build/libs/order-service-main-$serviceImageVersion.jar service.jar
