FROM openjdk:8-jre

ENV PATH /usr/local/tomee/bin:$PATH
RUN mkdir -p /usr/local/tomee

WORKDIR /usr/local/tomee

# curl -fsSL 'https://www.apache.org/dist/tomee/KEYS' | awk -F ' = ' '$1 ~ /^ +Key fingerprint$/ { gsub(" ", "", $2); print $2 }' | sort -u



RUN set -x \
        && curl -fSL https://repo.maven.apache.org/maven2/org/apache/tomee/apache-tomee/7.0.2/apache-tomee-7.0.2-plume.tar.gz -o tomee.tar.gz \
        && tar -zxf tomee.tar.gz \
        && mv apache-tomee-plume-7.0.2/* /usr/local/tomee \
        && rm -Rf apache-tomee-plume-7.0.2 \
        && rm bin/*.bat \
        && rm tomee.tar.gz*

ADD haushaltsverwaltung.war /usr/local/tomee/webapps

COPY server.xml /usr/local/tomee/conf/
COPY index.jsp  /usr/local/tomee/webapps/ROOT/

CMD ["catalina.sh", "run"]

