# Makefile for Secret Monkey - Automated Build, Test, Run

install:
	@mvn clean install -DskipTests

server:
	@mvn spring-boot:run

clean:
	@mvn clean

test:
	@mvn clean test

help:
	@echo "Available targets:"
	@echo "install       - Compile the code using Maven."
	@echo "server        - Run the API"
	@echo "test          - Run all tests."
	@echo "help          - Show this help message."

.PHONY: install server clean test help
