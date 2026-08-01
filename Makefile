.PHONY: run clean compile refresh

run:
	./gradlew run

clean:
	./gradlew clean

compile:
	./gradlew compileJava

complete_build:
	./gradlew clean
	./gradlew build
