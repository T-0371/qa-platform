# 使用 Eclipse Temurin Java 11 镜像（OpenJDK 的替代品）
FROM eclipse-temurin:11-jdk-alpine

# 设置环境变量
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . .

# 给 mvnw 添加执行权限
RUN chmod +x mvnw

# 构建项目
RUN ./mvnw clean package -DskipTests

# 运行应用
CMD ["java", "-Xmx256m", "-Xms64m", "-XX:MaxMetaspaceSize=128m", "-jar", "target/qa-platform.jar", "--spring.profiles.active=prod"]