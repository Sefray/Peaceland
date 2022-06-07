FROM sbtscala/scala-sbt:17.0.2_1.6.2_2.13.8

COPY . /app

WORKDIR /app

RUN sbt compile

ENTRYPOINT sbt run
