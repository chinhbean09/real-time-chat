    # Build Stage: Sử dụng Maven image để build ứng dụng
    FROM maven:3.8.5-openjdk-17-slim AS build
    WORKDIR /app

    # Bước 1: Copy pom.xml và cache các dependency
    COPY pom.xml .
    RUN mvn dependency:go-offline -B

    # Bước 2: Copy toàn bộ code (bao gồm cả thư mục src)
    COPY src ./src

    # Build dự án, bỏ qua test để tăng tốc (có thể thay đổi tùy theo yêu cầu)
    RUN mvn clean install -DskipTests=true

    # Run Stage: Sử dụng OpenJDK alpine cho image nhẹ hơn
    FROM openjdk:17-alpine

    RUN apk add --no-cache tzdata && \
        ln -snf /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
        echo "Asia/Ho_Chi_Minh" > /etc/timezone

    # Tạo user không root (ví dụ: coms-chinhbean)
    RUN adduser -D coms-chinhbean

    WORKDIR /run

    # COPY file jar từ stage build.
    # Chỉnh sửa tên file jar cho phù hợp với output của Maven (ở đây dùng contract-management-0.0.1-SNAPSHOT.jar làm ví dụ)
    COPY --from=build /app/target/real-time-chat-0.0.1-SNAPSHOT.jar /run/real-time-chat-0.0.1-SNAPSHOT.jar

    # Thay đổi quyền sở hữu cho thư mục chạy, đảm bảo user không root có quyền truy cập
    RUN chown -R coms-chinhbean:coms-chinhbean /run

    # Chạy container với user không root
    USER coms-chinhbean

    # Mở cổng 8080 để ứng dụng có thể nhận request
    EXPOSE 8081

    # Lệnh khởi động ứng dụng
    ENTRYPOINT ["java", "-jar", "/run/real-time-chat-0.0.1-SNAPSHOT.jar"]