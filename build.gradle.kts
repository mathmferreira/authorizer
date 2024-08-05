plugins {
	java
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "br.com.caju"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val commonsLangVersion = "3.15.0"
val commonsCollectionsVersion = "4.4"
val restAssuredVersion = "5.5.0"
val hamcrestVersion = "3.0"
val openApiVersion = "2.6.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	implementation("org.apache.commons:commons-lang3:$commonsLangVersion")
	implementation("org.apache.commons:commons-collections4:$commonsCollectionsVersion")
	implementation("io.rest-assured:rest-assured:$restAssuredVersion")
	implementation("io.rest-assured:spring-mock-mvc:$restAssuredVersion")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.integration:spring-integration-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
