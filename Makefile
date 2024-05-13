build: update
	gradle build
run:
	gradle run
clean:
	gradle clean
test:
	gradle test
package:
	gradle package

update:
	bash ./update.sh

# Think to update gradle/wrapper/gradle-wrapper.properties file
gradle-update:
	gradle wrapper --gradle-version 8.7 --distribution-type all

.PHONY: build run clean test package
