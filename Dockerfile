# 使用官方 Java 11 镜像
FROM openjdk:11-jdk-slim

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