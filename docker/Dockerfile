FROM 10.193.9.29:5000/public/dpp-centos-7.3:v1.0
MAINTAINER wang.liu <wang.liu@siemens.com>
### pi 客户端环境设置
RUN mkdir -p /opt/tools/pipc/jdbc
COPY ./pipc/jdbc/lib/* /opt/tools/pipc/jdbc/lib/
COPY ./pipc/jdbc/PIJDBCDriver.jar /opt/tools/pipc/jdbc/
RUN ls -la /opt/tools/pipc/jdbc/*
ENV PI_RDSA_LIB=/opt/tools/pipc/jdbc/lib/libRdsaWrapper-1.3.0b.so
ENV PI_RDSA_LIB64=/opt/tools/pipc/jdbc/lib/libRdsaWrapper64-1.3.0b.so
ENV CLASSPATH=$CLASSPATH:/opt/tools/pipc/jdbc/PIJDBCDriver.jar:.
WORKDIR /lib64
RUN ln -s /lib64/libssl.so.1.0.2k libssl.so.1.0.0 && \
    ln -s /lib64/libcrypto.so.1.0.2k libcrypto.so.1.0.0

### 数据业务
RUN mkdir /opt/tsdata-archive
COPY ./app/bin/* /opt/tsdata-archive/bin/
COPY ./app/conf/* /opt/tsdata-archive/conf/
COPY ./app/lib/* /opt/tsdata-archive/lib/
RUN ls -la /opt/tsdata-archive/*
ENV JAVA_OPTS_APOLLO=""

### Expose ports.  
EXPOSE 8094
### Define default command.  
COPY ./entrypoint.sh /opt/
ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["/bin/bash"]